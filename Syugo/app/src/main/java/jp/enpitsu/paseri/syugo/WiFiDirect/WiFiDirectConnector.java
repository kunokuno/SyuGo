package jp.enpitsu.paseri.syugo.WiFiDirect;

import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prily on 2016/11/25.
 */

public class WiFiDirectConnector implements WifiP2pManager.PeerListListener {
    static final String TAG = "wifi_direct_cnctr";
    WiFiDirectActivity activity;
    static boolean onConnecting = false;

    WiFiDirectConnector(WiFiDirectActivity activity){
        this.activity = activity;
    }

    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        peers.clear();
        peers.addAll(peerList.getDeviceList());
        //Log.d(TAG,peers.toString());

        // if Device on Inviting or Connected, terminate.
        if (onConnecting || activity.getStatus().equals("Invited") || activity.getStatus().equals("Connected")){
            return;
        }

        // Search Opponent Device in Peer List
        for(int i=0; i<peers.size(); ++i){
            if (peers.get(i).deviceName.equals(activity.getOpponentID())){
                activity.toast("Opponent Device Found !");
                onConnecting = true;

                WifiP2pDevice device = peers.get(i);
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;

                Log.d(TAG,"connect challenge");
                activity.connect(config);
                return;
            }
        }

        // Can't Found Opponent Device
        activity.toast("Can't found device");
    }

}
