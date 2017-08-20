package com.seakg.sopovrobotics.Interfaces;

import org.json.JSONObject;

public interface SopovRoboticsListener {
    void onGotInformation(JSONObject obj);
    void onConnected();
    void onDisconnected();
}
