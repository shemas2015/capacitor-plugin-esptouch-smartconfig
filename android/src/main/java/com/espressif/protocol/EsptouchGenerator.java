package com.espressif.protocol;


import com.espressif.security.TouchAES;
import com.espressif.util.ByteUtil;

import java.net.InetAddress;

public class EsptouchGenerator {

    private final byte[][] mGcBytes2;
    private final byte[][] mDcBytes2;


    /**
     * Constructor of EsptouchGenerator, it will cost some time(maybe a bit
     * much)
     */
    public EsptouchGenerator(byte[] apSsid, byte[] apBssid, byte[] apPassword, InetAddress inetAddress,
                             TouchAES encryptor) {
        // generate guide code
        GuideCode gc = new GuideCode();
        char[] gcU81 = gc.getU8s();
        mGcBytes2 = new byte[gcU81.length][];

        for (int i = 0; i < mGcBytes2.length; i++) {
            mGcBytes2[i] = ByteUtil.genSpecBytes(gcU81[i]);
        }

        // generate data code
        DatumCode dc = new DatumCode(apSsid, apBssid, apPassword, inetAddress, encryptor);
        char[] dcU81 = dc.getU8s();
        mDcBytes2 = new byte[dcU81.length][];

        for (int i = 0; i < mDcBytes2.length; i++) {
            mDcBytes2[i] = ByteUtil.genSpecBytes(dcU81[i]);
        }


    }

    public byte[][] getGCBytes2() {
        return mGcBytes2;
    }
    public byte[][] getDCBytes2() {
        return mDcBytes2;
    }

}
