package com.seakg.rasprobotcli.Interfaces;

import org.json.JSONObject;

public interface RaspRobotDListener {
    void onGotInformation(JSONObject obj);
    void onConnected();
    void onDisconnected();
}
