package jp.enpitsu.paseri.syugo.WiFiDirect;

import java.util.EventListener;

import jp.enpitsu.paseri.syugo.Rader.LocationData;

/**
 * Created by Prily on 2016/12/06.
 */

public interface WiFiDirectEventListener extends EventListener {
    public void receiveChat(String str);
    public void receiveGPSLocation(LocationData loc);
}
