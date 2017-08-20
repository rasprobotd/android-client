package com.seakg.sopovrobotics;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.seakg.sopovrobotics.Components.DriverControls;
import com.seakg.sopovrobotics.Components.DriverControlsListener;
import com.seakg.sopovrobotics.Controllers.SopovRoboticsController;
import com.seakg.sopovrobotics.Interfaces.SopovRoboticsListener;

import org.json.JSONException;
import org.json.JSONObject;

public class ControlsActivity extends AppCompatActivity implements SopovRoboticsListener, DriverControlsListener {
    private static final String TAG = ControlsActivity.class.getSimpleName();
    private TextView mStatus = null;
    private SopovRoboticsController mSopovRoboticsController = null;
    private TextView mName = null;
    private DriverControls mDriverControls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_controls);
        getSupportActionBar().hide();
        mSopovRoboticsController = SopovRoboticsController.getInstance();
        mSopovRoboticsController.setListener(this);

        Intent intent = getIntent();
        String ip = intent.getStringExtra("ip");
        mName = (TextView)findViewById(R.id.nameofrobot);
        mStatus = (TextView) findViewById(R.id.textView_status);
        mStatus.setText("Connecting to ws://" + ip + ":7528/");

        mDriverControls = (DriverControls) findViewById(R.id.driverControls);
        mDriverControls.setListener(this);
        mSopovRoboticsController.connect();
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
        mSopovRoboticsController.unsetListener();
        if(mSopovRoboticsController.isConnected()){
            mSopovRoboticsController.disconnect();
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
        updateStatus("Connected to ws://" + mSopovRoboticsController.getIp() + ":7528/");
    }

    @Override
    public void onDisconnected() {
        updateStatus("Closed connection");
        finish();
    }

    @Override
    public void driveCommand(String cmd) {
        mSopovRoboticsController.sendCommand(cmd);
    }
}
