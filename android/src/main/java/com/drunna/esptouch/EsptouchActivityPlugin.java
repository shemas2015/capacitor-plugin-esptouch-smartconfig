package com.drunna.esptouch;

import android.content.Context;
import android.net.InetAddresses;
import android.util.Log;

import com.espressif.EsptouchAsyncTask;
import com.espressif.esptouch.EsptouchResult;
import com.espressif.util.ByteUtil;
import com.espressif.util.TouchNetUtil;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@CapacitorPlugin(name = "EsptouchActivity")
public class EsptouchActivityPlugin extends Plugin {

    private EsptouchActivity implementation = new EsptouchActivity();
    private byte[] mSsidBytes;

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");
        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

    @PluginMethod
    public void connect(PluginCall call) {
        JSObject ret = new JSObject();

        String mSsid = call.getString("ssid");
        String pwdStr = call.getString("password");
        String ip = call.getString("ip");
        String mBssid = "00:00:00:00:00:00";


        Log.d("debug" , mSsid);

        boolean isValid = this.checkIPv4(ip);
        if( !isValid ){
            call.errorCallback("Ip address is not valid!");
            return;
        }
        if( mSsid.equals("") ){
            call.errorCallback("Ssid can not be null");
            return;
        }
        //check ip
        //Check ssid

        byte[] ssid = mSsidBytes == null ? ByteUtil.getBytesByString(mSsid)
                : mSsidBytes;
        byte[] password =  ByteUtil.getBytesByString(pwdStr);
        byte[] bssid = TouchNetUtil.parseBssid2bytes(mBssid);
        byte[] deviceCount = "1".getBytes();
        byte[] broadcast = {(byte) 1};
        Context context = this.getActivity().getApplicationContext();
        EsptouchAsyncTask mTask = new EsptouchAsyncTask(ssid, bssid, password, deviceCount, broadcast,ip , context);
        mTask.__listenAsyn();
        List<EsptouchResult> results =  mTask.execute();
        if(results.size() < 1){
            call.errorCallback("invalid configuration!");
            return;
        }
        ret.put("mac" , results.get(0).getBssid());
        call.resolve(ret);
    }
    public static final boolean checkIPv4(final String ip) {
        boolean isIPv4;
        try {
            final InetAddress inet = InetAddress.getByName(ip);
            isIPv4 = inet.getHostAddress().equals(ip)
                    && inet instanceof Inet4Address;
        } catch (final UnknownHostException e) {
            isIPv4 = false;
        }
        return isIPv4;
    }
}
