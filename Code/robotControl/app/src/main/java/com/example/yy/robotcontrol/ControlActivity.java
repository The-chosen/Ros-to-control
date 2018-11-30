package com.example.yy.robotcontrol;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class ControlActivity extends AppCompatActivity {
    private Button showAllRobotsIcon;
    private Button closeAllRobotsIcon;
    private Button updateIcon;
    private Button locateIcon;
    private Button stopIcon;
    private Button setIcon;
    private Button functionIcon;
    private Button specificIcon;
    private Button controllerIcon;
    private Button closeSpecificIcon;
    private Button closeFunctionBtn;

    private RelativeLayout controller;
    private LinearLayout functions;
    private LinearLayout specificInfo;
    private LinearLayout robotList;

    private ListView listView;
    private String[] robots = {
            "Robot1", "Robot2", "Robot3", "Robot4", "Robot5", "Robot6", "Robot7"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_control);

        showAllRobotsIcon = (Button) findViewById(R.id.show_all_robots);
        closeAllRobotsIcon = (Button) findViewById(R.id.close_all_robots);
        updateIcon = (Button) findViewById(R.id.update_icon);
        locateIcon = (Button) findViewById(R.id.locate_icon);
        stopIcon = (Button) findViewById(R.id.stop_icon);
        setIcon = (Button) findViewById(R.id.set_icon);
        functionIcon = (Button) findViewById(R.id.function_icon);
        specificIcon = (Button) findViewById(R.id.specific_icon);
        controllerIcon = (Button) findViewById(R.id.controller_icon);
        closeSpecificIcon = (Button) findViewById(R.id.close_specific_icon);
        closeFunctionBtn = (Button) findViewById(R.id.close_functions_btn);

        controller = (RelativeLayout) findViewById(R.id.controller);
        functions = (LinearLayout) findViewById(R.id.functions);
        specificInfo = (LinearLayout) findViewById(R.id.specific_info);
        robotList = (LinearLayout) findViewById(R.id.robot_list);


        ArrayAdapter<String> adapter = new
                ArrayAdapter<String>(ControlActivity.this, android.R.layout.simple_list_item_1, robots);
        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);


        setIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ControlActivity.this, Set.class);
                startActivity(intent);
            }
        });

        controllerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (controller.getVisibility() == View.GONE){
                    controller.setVisibility(View.VISIBLE);
                    specificInfo.setVisibility(View.GONE);
                    closeSpecificIcon.setVisibility(View.GONE);
                    specificIcon.setVisibility(View.VISIBLE);
                }
                else {
                    controller.setVisibility(View.GONE);
                }
            }
        });

        specificIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    specificIcon.setVisibility(View.GONE);
                    closeSpecificIcon.setVisibility(View.VISIBLE);
                    specificInfo.setVisibility(View.VISIBLE);
                    controller.setVisibility(View.GONE);
            };
        });

        closeSpecificIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSpecificIcon.setVisibility(View.GONE);
                specificIcon.setVisibility(View.VISIBLE);
                specificInfo.setVisibility(View.GONE);
            }
        });

        functionIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (functions.getVisibility() == View.GONE) {
                    functions.setVisibility(View.VISIBLE);
                    specificInfo.setVisibility(View.GONE);
                    controller.setVisibility(View.GONE);
                }
            }
        });

        closeFunctionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                functions.setVisibility(View.GONE);
            }
        });

        showAllRobotsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllRobotsIcon.setVisibility(View.GONE);
                closeAllRobotsIcon.setVisibility(View.VISIBLE);
                robotList.setVisibility(View.VISIBLE);
            }
        });

        closeAllRobotsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAllRobotsIcon.setVisibility(View.GONE);
                showAllRobotsIcon.setVisibility(View.VISIBLE);
                robotList.setVisibility(View.GONE);
            }
        });
    }
}
