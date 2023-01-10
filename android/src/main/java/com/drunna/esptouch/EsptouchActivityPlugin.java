package com.drunna.esptouch;

import android.content.Context;

import com.espressif.EsptouchAsyncTask;
import com.espressif.util.ByteUtil;
import com.espressif.util.TouchNetUtil;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "EsptouchActivity")
public class EsptouchActivityPlugin extends Plugin {

    private EsptouchActivity implementation = new EsptouchActivity();
    private byte[] mSsidBytes;

    @PluginMethod
    public void echo(PluginCall call) {

        String mSsid = "Delfina";
        String pwdStr = "51597541";
        String mBssid = "d0:79:80:88:02:81";
        String ip = "192.168.1.10";
        byte[] ssid = mSsidBytes == null ? ByteUtil.getBytesByString(mSsid)
                : mSsidBytes;
        byte[] password =  ByteUtil.getBytesByString(pwdStr);
        byte[] bssid = TouchNetUtil.parseBssid2bytes(mBssid);
        byte[] deviceCount = "1".getBytes();
        byte[] broadcast = {(byte) 1};
        Context context = this.getActivity().getApplicationContext();




        EsptouchAsyncTask mTask = new EsptouchAsyncTask(ssid, bssid, password, deviceCount, broadcast,ip , context);
        mTask.__listenAsyn();
        mTask.execute();

        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }
}
