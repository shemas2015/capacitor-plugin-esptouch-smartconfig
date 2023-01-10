package com.espressif.esptouch;

public interface IEsptouchListener {
    /**
     * when new esptouch result is added, the listener will call
     * onEsptouchResultAdded callback
     *
     * @param result the Esptouch result
     */
    void onEsptouchResultAdded(EsptouchResult result);
}
