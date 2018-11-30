package com.example.yy.robotcontrol;

import android.content.DialogInterface;
import android.content.Intent;
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

public class SelectServer extends AppCompatActivity {
    private ListView listView;
    private Button button;
    private AlertDialog alertDialog;
    private String[] service = {"SERVICE-TEST1", "SERVICE-TEST2", "SERVICE-TEST3", "SERVICE-TEST4", "SERVICE-TEST5"};
    private ArrayAdapter<String> adapter;
    private String ip_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_server);

        listView = (ListView)findViewById(R.id.service_list_view);
        button = (Button)findViewById(R.id.input_ip);

        adapter = new ArrayAdapter<>(SelectServer.this, android.R.layout.simple_list_item_1, service);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("serverName", service[position]);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(SelectServer.this, ServerLogin.class);
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               initDialog();
               alertDialog.show();
            }
        });
    }

    public void initDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(SelectServer.this);
        builder.setTitle("请输入IP");
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        final EditText editText = new EditText(SelectServer.this);
        editText.setHint("请输入IP地址");
        builder.setView(editText);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ip_address = editText.getText().toString();

                Intent intent = new Intent();
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
}
