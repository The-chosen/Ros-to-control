package com.example.yy.robotcontrol;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.ros.android.RosActivity;
import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Publisher;

import java.net.URI;
import java.util.ArrayList;

import geometry_msgs.Twist;

public class ControlActivity extends RosActivity {
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

    private java.lang.String[] robots = {
            "Robot1", "Robot2", "Robot3", "Robot4", "Robot5", "Robot6", "Robot7"
    };

//    在onCreate之前就执行了
    protected ControlActivity() {
        super("ros_test", "ros_test", URI.create("http://192.168.43.90:11311")); // 这里是ROS_MASTER_URI
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        测试Shell的！！！！！！！！！！
        Shell shell = new Shell("", "", "");
        shell.execute("ls");
        ArrayList<java.lang.String> stdout = shell.getStandardOutput();
        for (String str : stdout) {
            Log.d("MainActivity", str);
        }


//        super.nodeMainExecutorService.setMasterUri(IP);
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


        ArrayAdapter<java.lang.String> adapter = new
                ArrayAdapter<java.lang.String>(ControlActivity.this, android.R.layout.simple_list_item_1, robots);
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
                if (controller.getVisibility() == View.GONE) {
                    controller.setVisibility(View.VISIBLE);
                    specificInfo.setVisibility(View.GONE);
                    closeSpecificIcon.setVisibility(View.GONE);
                    specificIcon.setVisibility(View.VISIBLE);
                } else {
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
            }

            ;
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


    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
//        URI IP = (URI) getIntent().getSerializableExtra("IP");
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(getRosHostname());
//        nodeConfiguration.setMasterUri(IP);
        nodeConfiguration.setMasterUri(getMasterUri());
        nodeMainExecutor.execute(new NodeMain() {
            @Override
            public GraphName getDefaultNodeName() {
                return GraphName.of("ros_test");
            }

            @Override
            public void onStart(ConnectedNode connectedNode) {
                final Publisher<geometry_msgs.Twist> pub = connectedNode.newPublisher("/turtle1/cmd_vel", geometry_msgs.Twist._TYPE);
                connectedNode.executeCancellableLoop(new CancellableLoop() {
                    private int sequenceNumber;

                    @Override
                    protected void setup() {
                        sequenceNumber = 0;
                    }


                    @Override
                    protected void loop() throws InterruptedException {
//                        std_msgs.String msg = pub.newMessage();
//                        msg.setData("Hello world!");
//                        pub.publish(msg);
//                        Thread.sleep(1000);
                        geometry_msgs.Twist twist = pub.newMessage(); // Init a msg variable that of the publisher type
//                        走拐角
//                        sequenceNumber++;
//
//                        if (sequenceNumber % 3 == 0) {          // Every 3 executions of the loop (aprox. 3*1000ms = 3 sec)
//                            twist.getAngular().setZ(Math.PI/2);   // Steer the turtle left
//                        }
//                        else{
//                            twist.getLinear().setX(2);            // In the meantime keeps going foward
//                        }
//                        走圆形：
                        twist.getLinear().setX(2);
                        twist.getLinear().setY(0);
                        twist.getLinear().setZ(0);
                        twist.getAngular().setX(0);
                        twist.getAngular().setY(0);
                        twist.getAngular().setZ(1.8);
                        pub.publish(twist);       // Publish the message (if running use rostopic list to see the message)

                        Thread.sleep(1000);             // Sleep for 1000 ms = 1 sec
                    }
                });
            }

            @Override
            public void onShutdown(Node node) {

            }

            @Override
            public void onShutdownComplete(Node node) {

            }

            @Override
            public void onError(Node node, Throwable throwable) {

            }
        }, nodeConfiguration);
    }
}
