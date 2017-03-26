package com.seakg.rasprobotcli;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.seakg.rasprobotcli.Controllers.RaspRobotDController;
import com.seakg.rasprobotcli.Interfaces.RaspRobotDListener;

import org.json.JSONException;
import org.json.JSONObject;

public class ControlsActivity extends AppCompatActivity implements RaspRobotDListener {
    private static final String TAG = ControlsActivity.class.getSimpleName();
    private String mIP = "";
    private TextView mStatus = null;
    private Button mBtnForward;
    private Button mBtnBackward;
    private Button mBtnLeft;
    private Button mBtnRight;
    private RaspRobotDController mRaspRobotDController = null;
    private TextView mName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_controls);
        getSupportActionBar().hide();
        mRaspRobotDController = RaspRobotDController.getInstance();
        mRaspRobotDController.setListener(this);

        Intent intent = getIntent();
        mIP = intent.getStringExtra("ip");
        mName = (TextView)findViewById(R.id.nameofrobot);
        mStatus = (TextView) findViewById(R.id.textView_status);
        mStatus.setText("Connecting to ws://" + mIP + ":1234/");

        mBtnForward = (Button) findViewById(R.id.btn_forward);
        mBtnBackward = (Button) findViewById(R.id.btn_backward);
        mBtnLeft = (Button) findViewById(R.id.btn_left);
        mBtnRight = (Button) findViewById(R.id.btn_right);

        mBtnForward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int eventaction = event.getAction();
                if(eventaction == MotionEvent.ACTION_DOWN) {
                    mRaspRobotDController.sendCommand("forward");
                    return true;
                }else if(eventaction == MotionEvent.ACTION_UP){
                    mRaspRobotDController.sendCommand("stop");
                }
                return false;
            }
        });

        mBtnBackward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int eventaction = event.getAction();
                if(eventaction == MotionEvent.ACTION_DOWN) {
                    mRaspRobotDController.sendCommand("backward");
                    return true;
                }else if(eventaction == MotionEvent.ACTION_UP){
                    mRaspRobotDController.sendCommand("stop");
                }
                return false;
            }
        });

        mBtnLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int eventaction = event.getAction();
                if(eventaction == MotionEvent.ACTION_DOWN) {
                    mRaspRobotDController.sendCommand("turnleft");
                    return true;
                }else if(eventaction == MotionEvent.ACTION_UP){
                    mRaspRobotDController.sendCommand("stop");
                }
                return false;
            }
        });

        mBtnRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int eventaction = event.getAction();
                if(eventaction == MotionEvent.ACTION_DOWN) {
                    mRaspRobotDController.sendCommand("turnright");
                    return true;
                }else if(eventaction == MotionEvent.ACTION_UP){
                    mRaspRobotDController.sendCommand("stop");
                }
                return false;
            }
        });

        mRaspRobotDController.connect();

    }

    public void updateStatus(final String s){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatus.setText(s);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mRaspRobotDController.unsetListener();
        if(mRaspRobotDController.isConnected()){
            mRaspRobotDController.disconnect();
        }
    }

    @Override
    public void onGotInformation(final JSONObject obj) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String version = "";
                String name = "";
                int firmware = 0;
                try {
                    if (obj.has("version")) {
                        version = obj.getString("version");
                    }

                    if (obj.has("name")) {
                        name = obj.getString("name");
                    }

                    if (obj.has("firmware")) {
                        firmware = obj.getInt("firmware");
                    }

                }catch(JSONException e){
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                }
                mName.setText(name + " " + version + " (f: " + firmware + ")");
            }
        });
    }

    @Override
    public void onConnected() {
        updateStatus("Connected to ws://" + mRaspRobotDController.getIp() + ":1234/");
    }

    @Override
    public void onDisconnected() {
        updateStatus("Closed connection");
        finish();
    }
}
