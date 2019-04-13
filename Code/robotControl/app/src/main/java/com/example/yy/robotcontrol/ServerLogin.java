package com.example.yy.robotcontrol;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class ServerLogin extends AppCompatActivity {
    private TextView head_textView;
    private Button btn_login;
    private EditText edit_userName;
    private EditText edit_password;
    private String userName;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_login);

        btn_login = (Button)findViewById(R.id.btn_login);
        head_textView = (TextView)findViewById(R.id.head_title);
        edit_userName = (EditText)findViewById(R.id.et_userName);
        edit_password = (EditText)findViewById(R.id.et_password);

        //get the server's name
        final Intent intent = getIntent();
        String data = intent.getStringExtra("serverID");
        head_textView.setText(data);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = edit_userName.getText().toString();
                password = edit_password.getText().toString();

                Shell shell = new Shell(intent.getStringExtra("IP"), userName, password);
//                shell.execute("roscore");

                Intent intent_toControl = new Intent();
                intent_toControl.setClass(ServerLogin.this, ControlActivity.class);
                startActivity(intent_toControl);
            }
        });
    }
}
