/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.enpitsu.paseri.syugo.WiFiDirect;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import jp.enpitsu.paseri.syugo.R;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;


/**
 * An activity that uses WiFi Direct APIs to discover and connect with available
 * devices. WiFi Direct APIs are asynchronous and rely on callback mechanism
 * using interfaces to notify the application of operation success or failure.
 * The application should also register a BroadcastReceiver for notification of
 * WiFi state related events.
 */
public class WiFiDirectActivity extends Activity {

    // For Debug
    public static final String TAG = "wifi_direct";

    // Instances
    private WifiP2pManager manager;
    private Channel channel;
    private BroadcastReceiver receiver = null;

    // Status
    private boolean isWifiP2pEnabled = false;
    private String connectionStatus = "unknown";
    public boolean onConnecting = false;

    // Intent Filter
    private final IntentFilter intentFilter = new IntentFilter();

    // UI Objects
    Switch sw_p2p_enable;
    TextView txt_self_device_name,txt_opponent_device_name,txt_device_status;
    Button btn_connect, btn_device_discover, btn_open_settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifidirect);

        // Find UI Objects
        sw_p2p_enable = (Switch) findViewById(R.id.wd_p2p_enable);
        txt_self_device_name = (TextView) findViewById(R.id.wd_self_device_name);
        txt_opponent_device_name = (TextView) findViewById(R.id.wd_opponent_device_name);
        txt_device_status = (TextView) findViewById(R.id.wd_device_status);
        btn_connect = (Button) findViewById(R.id.wd_connect);
        btn_device_discover = (Button) findViewById(R.id.wd_discover);
        btn_open_settings = (Button) findViewById(R.id.wd_wdsetting);

        // Initialize UI Objects
        sw_p2p_enable.setChecked(false);
        txt_self_device_name.setText( getSelfDeviceAddress() );
        txt_opponent_device_name.setText("Opponent Device Name is unknown");
        txt_device_status.setText("Not Connected");
        btn_connect.setOnClickListener(connectClickListner);
        btn_device_discover.setOnClickListener(discoverClickListner);
        btn_open_settings.setOnClickListener(opensettingsClickListner);

        // Register the Intent Filter
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        // Get manager & channel Instance
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        Log.d(TAG,"hello");
    }

    @Override
    public void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }


    /* -----------------------------------------------------------

    Getter & Setter of Status

    -------------------------------------------------------------- */

    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
        sw_p2p_enable.setChecked(isWifiP2pEnabled);
    }

    public void setSelfDeviceName(String name){
        setDeviceName(name);
        txt_self_device_name.setText("This Device Name : "+name);
    }

    public void setStatus(String status){
        connectionStatus = status;
        txt_device_status.setText("Status : "+status);
    }

    public String getStatus(){
        return connectionStatus;
    }

    public void setOpponentDeviceInformation(String information_str){
        txt_opponent_device_name.setText("Opponent Device Status : \n"+information_str);
    }

    public void updateThisDevice(WifiP2pDevice device) {
        String status = getDeviceStatus(device.status);
        setStatus(status);
    }

    public void toast(String str){
        Toast.makeText(WiFiDirectActivity.this, str, Toast.LENGTH_SHORT).show();
    }




    /* -----------------------------------------------------------

    Connection Managers

    -------------------------------------------------------------- */

    public void resetData() {

    }

    public void connect(WifiP2pConfig config) {
        manager.connect(channel, config, new ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                onConnecting = false;
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(WiFiDirectActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
                onConnecting = false;
            }
        });
    }

    public void disconnect(){

    }


    /* -----------------------------------------------------------

    Methods for Management Device Information

    -------------------------------------------------------------- */

    private String getSelfDeviceAddress(){
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String macAddress = wifiInfo.getMacAddress();
        return macAddress;
    }

    public void setDeviceName(String devName) {
        try {
            Class[] paramTypes = new Class[3];
            paramTypes[0] = Channel.class;
            paramTypes[1] = String.class;
            paramTypes[2] = ActionListener.class;
            Method setDeviceName = manager.getClass().getMethod(
                    "setDeviceName", paramTypes);
            setDeviceName.setAccessible(true);

            Object arglist[] = new Object[3];
            arglist[0] = channel;
            arglist[1] = devName;
            arglist[2] = new ActionListener() {

                @Override
                public void onSuccess() {
                    Log.d(TAG,"setDeviceName succeeded");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d(TAG,"setDeviceName failed");
                }
            };

            setDeviceName.invoke(manager, arglist);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static String getDeviceStatus(int deviceStatus) {
        Log.d(TAG, "Peer status :" + deviceStatus);
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";
        }
    }

    /* -----------------------------------------------------------

    Functions for UI Objects

    -------------------------------------------------------------- */

    View.OnClickListener opensettingsClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (manager != null && channel != null) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            } else {
                Log.e(TAG, "Activity : channel or manager is null");
            }
        }
    };

    View.OnClickListener discoverClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isWifiP2pEnabled) {
                Toast.makeText(WiFiDirectActivity.this, "Warning : P2P is OFF",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            onConnecting = false;

            manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(WiFiDirectActivity.this, "Discovery Initiated",
                            Toast.LENGTH_SHORT).show();
                    setStatus("Searching...");
                }

                @Override
                public void onFailure(int reasonCode) {
                    Toast.makeText(WiFiDirectActivity.this, "Discovery End : " + reasonCode,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    View.OnClickListener connectClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(WiFiDirectActivity.this, "null",Toast.LENGTH_SHORT).show();
        }
    };











    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_items, menu);
        return true;
    }
    */


//    @Override
//    public void showDetails(WifiP2pDevice device) {
//        DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
//                .findFragmentById(R.id.frag_detail);
//        fragment.showDetails(device);
//
//    }
//

//
//    @Override
//    public void disconnect() {
//        final DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
//                .findFragmentById(R.id.frag_detail);
//        fragment.resetViews();
//        manager.removeGroup(channel, new ActionListener() {
//
//            @Override
//            public void onFailure(int reasonCode) {
//                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
//
//            }
//
//            @Override
//            public void onSuccess() {
//                fragment.getView().setVisibility(View.GONE);
//            }
//
//        });
//    }
//
//    @Override
//    public void onChannelDisconnected() {
//        // we will try once more
//        if (manager != null && !retryChannel) {
//            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
//            resetData();
//            retryChannel = true;
//            manager.initialize(this, getMainLooper(), this);
//        } else {
//            Toast.makeText(this,
//                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
//                    Toast.LENGTH_LONG).show();
//        }
//    }
//
//    @Override
//    public void cancelDisconnect() {
//
//        /*
//         * A cancel abort request by user. Disconnect i.e. removeGroup if
//         * already connected. Else, request WifiP2pManager to abort the ongoing
//         * request
//         */
//        if (manager != null) {
//            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
//                    .findFragmentById(R.id.frag_list);
//            if (fragment.getDevice() == null
//                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
//                disconnect();
//            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
//                    || fragment.getDevice().status == WifiP2pDevice.INVITED) {
//
//                manager.cancelConnect(channel, new ActionListener() {
//
//                    @Override
//                    public void onSuccess() {
//                        Toast.makeText(WiFiDirectActivity.this, "Aborting connection",
//                                Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onFailure(int reasonCode) {
//                        Toast.makeText(WiFiDirectActivity.this,
//                                "Connect abort request failed. Reason Code: " + reasonCode,
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        }
//
//    }
}
