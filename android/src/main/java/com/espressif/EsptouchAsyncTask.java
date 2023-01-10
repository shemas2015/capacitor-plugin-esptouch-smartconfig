package com.espressif;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.espressif.esptouch.EsptouchResult;
import com.espressif.esptouch.IEsptouchListener;
import com.espressif.protocol.EsptouchGenerator;
import com.espressif.task.EsptouchTaskParameter;
import com.espressif.task.IEsptouchTaskParameter;
import com.espressif.udp.UDPSocketClient;
import com.espressif.udp.UDPSocketServer;
import com.espressif.util.ByteUtil;
import com.espressif.util.TouchNetUtil;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EsptouchAsyncTask{

    private EsptouchTaskParameter mParameter = new EsptouchTaskParameter();
    private final UDPSocketServer mSocketServer;
    private byte[] ssid;
    private byte[] bssid;
    private byte[] password;
    private byte[] deviceCount;
    private byte[] broadcast;
    private EsptouchGenerator generator;
    private final UDPSocketClient mSocketClient;
    private static final int ONE_DATA_LEN = 3;
    private Thread mTask;
    private final List<EsptouchResult> mEsptouchResultList = new ArrayList<>();;
    private volatile Map<String, Integer> mBssidTaskSucCountMap  = new HashMap<>();
    private IEsptouchListener mEsptouchListener;
    private boolean isInterrupt = false;


    public EsptouchAsyncTask(byte[] ssid, byte[] bssid, byte[] password, byte[] deviceCount, byte[] broadcast, String innetAdrres, Context context) {
        this.ssid = ssid;
        this.bssid = bssid;
        this.password = password;
        this.deviceCount = deviceCount;
        this.broadcast = broadcast;
        this.mSocketClient = new UDPSocketClient();
        this.mSocketServer = new UDPSocketServer(mParameter.getPortListening(),
                mParameter.getWaitUdpTotalMillisecond(), context);

        InetAddress localInetAddress = null;
        try {
            localInetAddress = InetAddress.getByName(innetAdrres);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.generator = new EsptouchGenerator(ssid,bssid,password,localInetAddress,null);


    }

    public void execute() {


        long startTime = System.currentTimeMillis();
        long currentTime = startTime;
        long lastTime = currentTime - mParameter.getTimeoutTotalCodeMillisecond();


        byte[][] gcBytes2 = this.generator.getGCBytes2();
        byte[][] dcBytes2 = this.generator.getDCBytes2();

        int index = 0;

        while (!isInterrupt) {
            if (currentTime - lastTime >= mParameter.getTimeoutTotalCodeMillisecond()) {
                // send guide code
                while (System.currentTimeMillis() - currentTime < mParameter
                        .getTimeoutGuideCodeMillisecond() && !isInterrupt) {
                    mSocketClient.sendData(gcBytes2,
                            mParameter.getTargetHostname(),
                            mParameter.getTargetPort(),
                            mParameter.getIntervalGuideCodeMillisecond());
                    // check whether the udp is send enough time
                    if (System.currentTimeMillis() - startTime > mParameter.getWaitUdpSendingMillisecond()) {
                        break;
                    }
                }
                lastTime = currentTime;
            } else {
                mSocketClient.sendData(dcBytes2, index, ONE_DATA_LEN,
                        mParameter.getTargetHostname(),
                        mParameter.getTargetPort(),
                        mParameter.getIntervalDataCodeMillisecond());
                index = (index + ONE_DATA_LEN) % dcBytes2.length;
            }
            currentTime = System.currentTimeMillis();

            // check whether the udp is send enough time
            if (currentTime - startTime > mParameter.getWaitUdpSendingMillisecond()) {
                break;
            }
        }

    }

    public void __listenAsyn() {
        mTask = new Thread() {
            public void run() {
                long startTimestamp = System.currentTimeMillis();
                byte expectOneByte = (byte) (ssid.length + password.length + 9);

                byte receiveOneByte = -1;
                byte[] receiveBytes = null;
                while (mEsptouchResultList.size() < mParameter
                        .getExpectTaskResultCount() ) {
                    receiveBytes = mSocketServer
                            .receiveSpecLenBytes(mParameter.getEsptouchResultTotalLen());
                    if (receiveBytes != null) {
                        receiveOneByte = receiveBytes[0];
                    } else {
                        receiveOneByte = -1;
                    }
                    if (receiveOneByte == expectOneByte) {
                        // change the socket's timeout
                        long consume = System.currentTimeMillis()
                                - startTimestamp;
                        int timeout = (int) (mParameter
                                .getWaitUdpTotalMillisecond() - consume);
                        if (timeout < 0) {
                            break;
                        } else {
                            mSocketServer.setSoTimeout(timeout);
                            if (receiveBytes != null) {
                                String bssid = ByteUtil.parseBssid(
                                        receiveBytes,
                                        mParameter.getEsptouchResultOneLen(),
                                        mParameter.getEsptouchResultMacLen()
                                );
                                InetAddress inetAddress = TouchNetUtil.parseInetAddr(
                                        receiveBytes,
                                        mParameter.getEsptouchResultOneLen() + mParameter.getEsptouchResultMacLen(),
                                        mParameter.getEsptouchResultIpLen()
                                );
                                __putEsptouchResult(true, bssid, inetAddress);
                            }
                        }
                    }
                }
                isInterrupt = true;
            }
        };
        mTask.start();
    }

    private void __putEsptouchResult(boolean isSuc, String bssid, InetAddress inetAddress) {
        synchronized (mEsptouchResultList) {
            // check whether the result receive enough UDP response
            boolean isTaskSucCountEnough = false;
            Integer count = mBssidTaskSucCountMap.get(bssid);
            if (count == null) {
                count = 0;
            }
            ++count;

            mBssidTaskSucCountMap.put(bssid, count);
            isTaskSucCountEnough = count >= mParameter
                    .getThresholdSucBroadcastCount();
            if (!isTaskSucCountEnough) {
                return;
            }
            // check whether the result is in the mEsptouchResultList already
            boolean isExist = false;
            for (EsptouchResult esptouchResultInList : mEsptouchResultList) {
                if (esptouchResultInList.getBssid().equals(bssid)) {
                    isExist = true;
                    break;
                }
            }
            // only add the result who isn't in the mEsptouchResultList
            if (!isExist) {
                final EsptouchResult esptouchResult = new EsptouchResult(isSuc,
                        bssid, inetAddress);
                mEsptouchResultList.add(esptouchResult);
                if (mEsptouchListener != null) {
                    mEsptouchListener.onEsptouchResultAdded(esptouchResult);
                }
            }
        }
    }
}

