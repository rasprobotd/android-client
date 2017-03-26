package com.seakg.rasprobotcli.Controllers;

import android.util.Log;

import com.firebase.tubesock.WebSocket;
import com.firebase.tubesock.WebSocketEventHandler;
import com.firebase.tubesock.WebSocketException;
import com.firebase.tubesock.WebSocketMessage;
import com.seakg.rasprobotcli.Interfaces.RaspRobotDListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class RaspRobotDController implements WebSocketEventHandler {
    private final static String TAG = RaspRobotDController.class.getSimpleName();
    private static RaspRobotDController mInstance = null;
    public static int PORT = 1234;
    private String mIP = "";
    private WebSocket mWebSocket = null;
    private boolean mOpened = false;
    private RaspRobotDListener mListener = null;

    public static RaspRobotDController getInstance(){
        if(mInstance == null){
            mInstance = new RaspRobotDController();
        }
        return mInstance;
    }

    public void setIp(String ip){
        mIP = ip;
    }

    public String getIp(){
        return mIP;
    }

    public void connect(){
        String url1 = "ws://" + mIP + ":1234/";
        Log.i(TAG, "Try connect to " + url1);
        URI uri = null;
        try {
            uri = new URI(url1);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mWebSocket = new WebSocket(uri);
        mWebSocket.setEventHandler(this);
        mWebSocket.connect();
    }

    public void disconnect(){
        if(mOpened){
            mWebSocket.close();
        }
    }

    public boolean isConnected(){
        return mOpened;
    }

    public void setListener(RaspRobotDListener listener){
        mListener = listener;
    }

    public void unsetListener(){
        mListener = null;
    }

    public void sendCommand(String cmd){
        Log.i(TAG, "send_command " + cmd);
        JSONObject jsonCmd = new JSONObject();
        try {
            jsonCmd.put("cmd", cmd);
            if(mOpened)
                mWebSocket.send(jsonCmd.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* WebSocketEventHandler */
    @Override
    public void onOpen() {
        mOpened = true;
        if(mListener != null){
            mListener.onConnected();
        }
    }

    @Override
    public void onMessage(WebSocketMessage message) {
        Log.i(TAG, "Message: " + message.getText());
        try {
            JSONObject obj = new JSONObject(message.getText());
            if(obj.has("msg") && obj.getString("msg").equals("info")){
                if(mListener != null){
                    mListener.onGotInformation(obj);
                }
            }
        }catch(JSONException e){
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }


    }

    @Override
    public void onClose() {
        mOpened = false;
        if(mListener != null){
            mListener.onDisconnected();
        }
    }

    @Override
    public void onError(WebSocketException e) {
        Log.e(TAG, "ErrorMsg:" + e.getMessage());
    }

    @Override
    public void onLogMessage(String msg) {
        Log.i(TAG, "LogMsg:" + msg);
        if(mListener != null){
            mListener.onDisconnected();
        }
    }
}
