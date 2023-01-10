package com.drunna.esptouch;

import android.util.Log;

import com.espressif.EsptouchAsyncTask;
import com.espressif.util.ByteUtil;
import com.espressif.util.TouchNetUtil;


public class EsptouchActivity {
    private byte[] mSsidBytes;



    public String echo(String value) {








        Log.i("Echo", value);
        return value;
    }
}
