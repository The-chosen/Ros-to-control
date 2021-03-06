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

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

import org.ros.android.RosActivity;
import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Publisher;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import geometry_msgs.Twist;

import static java.nio.charset.StandardCharsets.UTF_8;

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
    private Button controller_up;
    private Button controller_down;
    private Button controller_left;
    private Button controller_right;

    private RelativeLayout controller;
    private LinearLayout functions;
    private LinearLayout specificInfo;
    private LinearLayout robotList;


    private ListView listView;

    private Channel channel;
    private java.lang.String[] robots = {
            "Robot1", "Robot2", "Robot3", "Robot4", "Robot5", "Robot6", "Robot7"
    };

//    在onCreate之前就执行了
    protected ControlActivity() {
        super("ros_test", "ros_test", URI.create("http://" + Collections.IP + ":11311")); // 这里是ROS_MASTER_URI
        System.out.println("after: " + Collections.IP);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        controller_up = (Button) findViewById(R.id.controller_up);
        controller_down = (Button) findViewById(R.id.controller_down);
        controller_left = (Button) findViewById(R.id.controller_left);
        controller_right = (Button) findViewById(R.id.controller_right);

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

//        控制器
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

        //        测试Shell的！！！！！！！！！！
//        Shell shell = new Shell("192.168.43.90", "yygx", "681609qg");
//        shell.execute("ls");
//        ArrayList<java.lang.String> stdout = shell.getStandardOutput();
//        for (String str : stdout) {
//            Log.d("MainActivity", str + "gggxxx");
//            System.out.println(str + "gggxxx");
//        }
//
//此处自启动roscore
//        try {
//            JSch jSch = new JSch();
//            String host = null;
//
//            Session session = jSch.getSession("yygx", "192.168.43.90", 11311);
//            session.setPassword("681609qg");
//
//            UserInfo ui = new MyUserInfo() {
//                public void showMessage(String message) {
////                    JOptionPane.showMessageDialog(null, message);
//                }
//
//                public boolean promptYesNo(String message) {
//                    Object[] options = {"yes", "no"};
////                    int foo=JOptionPane.showOptionDialog(null,
////                            message,
////                            "Warning",
////                            JOptionPane.DEFAULT_OPTION,
////                            JOptionPane.WARNING_MESSAGE,
////                            null, options, options[0]);
//                    return true;
//                }
//            };
//
//            session.connect(30000);   // making a connection with timeout.
//
//            Channel channel=session.openChannel("shell");
//            InputStream stream = new ByteArrayInputStream("roscore\n".getBytes());
//            channel.setInputStream(stream);
//            channel.setOutputStream(System.out);
//            channel.connect(3*1000);
//
//        } catch (Exception e) {
//            System.out.println(e);
//        }
        Intent intent = getIntent();
        String username = Collections.USERNAME;
        String password = Collections.PASSWORD;

        try{
            JSch jsch=new JSch();

            String host=null;

            System.out.println("Collection!!!: " + Collections.IP);
            Session session=jsch.getSession(username, Collections.IP, 22);
            session.setPassword(password);
            UserInfo ui = new MyUserInfo(){
                public void showMessage(String message){
                }
                public boolean promptYesNo(String message){

                    return true;
                }

            };

            session.setUserInfo(ui);

            session.connect(30000);   // making a connection with timeout.

            channel=session.openChannel("shell");
            InputStream stream = new ByteArrayInputStream("roscore\n".getBytes());
            channel.setInputStream(stream);

            channel.setOutputStream(System.out);

            channel.connect(3*1000);
        }
        catch(Exception e){
            System.out.println(e);
        }



        nodeMainExecutor.execute(new NodeMain() {
            @Override
            public GraphName getDefaultNodeName() {
                return GraphName.of("ros_test");
            }

            @Override
            public void onStart(ConnectedNode connectedNode) {
                class Holder {
                    public double lx;
                    public double ax;
                    public int cnt;
                    public int tempCnt;
                }
                final Holder h = new Holder();
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
                        final geometry_msgs.Twist twist = pub.newMessage(); // Init a msg variable that of the publisher type
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
//                        twist.getLinear().setX(2);
//                        twist.getLinear().setY(0);
//                        twist.getLinear().setZ(0);
//                        twist.getAngular().setX(0);
//                        twist.getAngular().setY(0);
//                        twist.getAngular().setZ(1.8);


                        h.cnt++;
                        final int TIME = 7;
                        twist.getLinear().setX(h.lx);
                        twist.getAngular().setZ(h.ax);
                        if ((h.cnt - h.tempCnt) / TIME == 0 && h.cnt > h.tempCnt) {
                            h.lx = 0;
                            h.ax = 0;
                        }

                        controller_up.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                twist.getLinear().setX(2);
                                h.lx = 2;
                                h.tempCnt = h.cnt;
                            }
                        });
                        controller_down.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                twist.getLinear().setX(-2);
                                h.lx = -2;
                                h.tempCnt = h.cnt;
                            }
                        });
                        controller_left.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                twist.getAngular().setZ(2);
                                h.ax = 1.5;
                                h.tempCnt = h.cnt;
                            }
                        });
                        controller_right.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                twist.getAngular().setZ(-2);
                                h.ax = -1.5;
                                h.tempCnt = h.cnt;
                            }
                        });


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

//工具类
    public static abstract class MyUserInfo
            implements UserInfo, UIKeyboardInteractive {
        public String getPassword(){ return null; }
        public boolean promptYesNo(String str){ return false; }
        public String getPassphrase(){ return null; }
        public boolean promptPassphrase(String message){ return false; }
        public boolean promptPassword(String message){ return false; }
        public void showMessage(String message){ }
        public String[] promptKeyboardInteractive(String destination,
                                                  String name,
                                                  String instruction,
                                                  String[] prompt,
                                                  boolean[] echo){
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        channel.disconnect();
    }
}


