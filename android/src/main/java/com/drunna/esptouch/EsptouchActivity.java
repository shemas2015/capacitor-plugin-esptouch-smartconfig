package com.drunna.esptouch;

import android.util.Log;

import com.espressif.EsptouchAsyncTask;
import com.espressif.util.ByteUtil;
import com.espressif.util.TouchNetUtil;

import org.json.JSONException;
import org.json.JSONObject;


public class EsptouchActivity {
    public String echo(String value) {
        Log.i("Echo", value);
        return value;
    }

    public JSONObject connect(String ip, String ssid, String password) {
        JSONObject conection = new JSONObject();
        try {
            conection.put("ip", ip);
            conection.put("ssid", ssid);
            conection.put("password", password);
            return conection;
        } catch (JSONException e) {
            return null;
        }

    }
}
