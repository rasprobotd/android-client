package com.seakg.rasprobotcli.Tasks;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.seakg.rasprobotcli.Controllers.RaspRobotDController;
import com.seakg.rasprobotcli.ControlsActivity;
import com.seakg.rasprobotcli.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchAndConnectTask extends AsyncTask<Void, Void, ArrayList<String>> {
    private static final String TAG = SearchAndConnectTask.class.getSimpleName();
    private static final Pattern IP_PATTERN = Pattern.compile("[^0-9]*(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}).*");

    MainActivity mMainActivity = null;
    public SearchAndConnectTask(MainActivity mainActivity){
        mMainActivity = mainActivity;
    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        ArrayList<String> ips = getIPs();
        ArrayList<String> found_ips = new ArrayList<>();
        for(int i = 0; i < ips.size(); i++){
            String ip = ips.get(i);
            Log.i(TAG, "Check ip: " + ip);
            try {
                Socket testClient;
                testClient = new Socket(ip, RaspRobotDController.PORT);
                if (testClient.isConnected()) {
                    if(!found_ips.contains(ip)){
                        Log.i(TAG, "Found: " + ip);
                        found_ips.add(ip);
                    }
                    testClient.close();
                }
            }catch(ConnectException e){
                Log.e(TAG, e.getMessage());
            }catch(IOException e){
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        }
        return found_ips;
    }

    @Override
    protected void onPostExecute(ArrayList<String> result) {
        super.onPostExecute(result);
        mMainActivity.hideProgress();

        if(result.size() == 1){
            RaspRobotDController rrd = RaspRobotDController.getInstance();
            rrd.setIp(result.get(0));
            Intent intent = new Intent(mMainActivity, ControlsActivity.class);
            mMainActivity.startActivity(intent);
        }else{
            Toast.makeText(mMainActivity.getApplicationContext(),"Could not find device", Toast.LENGTH_LONG).show();
        }
    }

    private ArrayList<String> getIPs(){
        // File sdcard = Environment.getExternalStorageDirectory();
        ArrayList<String> ips = new ArrayList<>();

        //Get the text file
        File file = new File("/proc/net/arp");

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                Matcher matcher = IP_PATTERN.matcher(line);
                if (matcher.matches()) {
                    String ip = matcher.group(1);
                    ips.add(ip);
                    Log.i(TAG, "updateRobotIP: " + ip);
                }
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }

        String ip = "192.168.1.3";
        if(!ips.contains(ip)){
            ips.add(ip);
        }


        return ips;
    }
}
