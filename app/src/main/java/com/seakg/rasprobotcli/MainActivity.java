package com.seakg.rasprobotcli;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.seakg.rasprobotcli.Controllers.RaspRobotDController;
import com.seakg.rasprobotcli.Tasks.SearchAndConnectTask;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private View mSearchingProgress = null;
    private Button mBtnSearchAndConnect = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        mSearchingProgress = findViewById(R.id.searching_progress);

        mBtnSearchAndConnect = (Button)findViewById(R.id.btn_search_and_connect);
        mBtnSearchAndConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                SearchAndConnectTask task = new SearchAndConnectTask(MainActivity.this);
                task.execute();
            }
        });
    }

    public void showProgress(){
        mSearchingProgress.setVisibility(View.VISIBLE);
        mBtnSearchAndConnect.setVisibility(View.GONE);
    }

    public void hideProgress(){
        mSearchingProgress.setVisibility(View.GONE);
        mBtnSearchAndConnect.setVisibility(View.VISIBLE);
    }

}
