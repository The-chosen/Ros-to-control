package com.example.yy.robotcontrol;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class SelectServer extends AppCompatActivity {
    private ListView listView;
    private Button button_input_ip;
    private AlertDialog alertDialog;
    private ArrayList<String> service = new ArrayList();
    private ArrayAdapter<String> adapter;
    private String ip_address;

    private WifiManager wm;
    private WifiInfo wi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_server);

//        new tvThread().start();
        obtainListInfo();

        listView = (ListView)findViewById(R.id.service_list_view);
        button_input_ip = (Button)findViewById(R.id.input_ip);

        adapter = new ArrayAdapter<>(SelectServer.this, android.R.layout.simple_list_item_1, service);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();

                String[] info = service.get(position).split(",");
                bundle.putString("serverID", info[0]);
                bundle.putString("serverMAC", info[1]);
                bundle.putString("IP", intToIp(wi.getIpAddress()));

                Intent intent = new Intent();
                intent.setClass(SelectServer.this, ServerLogin.class);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });



        button_input_ip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               initDialog();
               alertDialog.show();
            }
        });
    }
//    private class tvThread extends Thread {
//        @Override
//        public void run() {
//            while (true) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        obtainListInfo();
//                    }
//                });
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    private void obtainListInfo(){
        //显示扫描到的所有wifi信息
        wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        //已连接wifi信息
        wi = wm.getConnectionInfo();

        if (wm.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            StringBuilder listinfo = new StringBuilder();
            //搜索到的wifi列表信息
            List<ScanResult> scanResults = filterScanResult(wm.getScanResults());

            for (ScanResult sr:scanResults) {
                listinfo.append(sr.SSID);
                listinfo.append(",");
                listinfo.append(sr.BSSID);
                service.add(listinfo.toString());
                listinfo = new StringBuilder();
            }
        }
    }

    //输入ip按钮事件
    public void initDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(SelectServer.this);
        builder.setTitle("请输入IP");
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        final EditText editText = new EditText(SelectServer.this);
        editText.setHint("172.20.10.2");
//        editText.setHint("请输入IP地址");
        builder.setView(editText);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ip_address = editText.getText().toString();

                Intent intent = new Intent();
                intent.putExtra("IP", URI.create("http://" + editText.getText().toString() + ":11311"));
                intent.setClass(SelectServer.this, ControlActivity.class);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
    }

    //把整型地址转换成“*.*.*.*”地址
    private String intToIp(int i) {
        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }

    //以SSID为关键字,过滤掉信号弱的选项
    public static List<ScanResult> filterScanResult(final List<ScanResult> list) {
        LinkedHashMap<String, ScanResult> linkedMap = new LinkedHashMap<>(list.size());
        for (ScanResult rst : list) {
            if (linkedMap.containsKey(rst.SSID)) {
                if (rst.level > linkedMap.get(rst.SSID).level) {
                    linkedMap.put(rst.SSID, rst);
                }
                continue;
            }
            linkedMap.put(rst.SSID, rst);
        }
        list.clear();
        list.addAll(linkedMap.values());
        return list;
    }
}
