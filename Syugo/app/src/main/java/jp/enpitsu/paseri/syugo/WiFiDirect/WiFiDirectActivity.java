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

    public static final String TAG = "wifi_direct_activity";
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;

    // ui objects
    Switch sw_p2p_enable;
    TextView txt_self_device_addr,txt_ap_device_addr,txt_device_status;
    Button btn_connect, btn_device_discover, btn_open_settings;

    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
        sw_p2p_enable.setChecked(isWifiP2pEnabled);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifidirect);

        // Initialize UI Objects
        sw_p2p_enable = (Switch) findViewById(R.id.wd_p2p_enable);
        txt_self_device_addr = (TextView) findViewById(R.id.wd_self_device_addr);
        txt_ap_device_addr = (TextView) findViewById(R.id.wd_ap_device_addr);
        txt_device_status = (TextView) findViewById(R.id.wd_device_status);
        btn_connect = (Button) findViewById(R.id.wd_connect);
        btn_device_discover = (Button) findViewById(R.id.wd_discover);
        btn_open_settings = (Button) findViewById(R.id.wd_wdsetting);

        sw_p2p_enable.setChecked(false);
        txt_self_device_addr.setText( getSelfDeviceAddress() );
        txt_ap_device_addr.setText("AP Device Addr is unknown");
        txt_device_status.setText("Not Connected");
        btn_connect.setOnClickListener(connectClickListner);
        btn_device_discover.setOnClickListener(discoverClickListner);
        btn_open_settings.setOnClickListener(opensettingsClickListner);


        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        Log.d("wd_notice","hello");
    }

    /** register the BroadcastReceiver with the intent values to be matched */
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

    /**
     * Remove all peers and clear all fields. This is called on
     * BroadcastReceiver receiving a state change event.
     */
    public void resetData() {
        /*
        DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager()
                .findFragmentById(R.id.frag_list);
        DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);
        if (fragmentList != null) {
            fragmentList.clearPeers();
        }
        if (fragmentDetails != null) {
            fragmentDetails.resetViews();
        }
        */
    }

    private String getSelfDeviceAddress(){
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String macAddress = wifiInfo.getMacAddress();
        return macAddress;
    }

        /*
    CompoundButton.OnCheckedChangeListener wdenableClickListner = new CompoundButton.OnCheckedChangeListener() {

    };
    */


    /*
    Click Listner for Buttons
     */

    View.OnClickListener opensettingsClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (manager != null && channel != null) {

                // Since this is the system wireless settings activity, it's
                // not going to send us a result. We will be notified by
                // WiFiDeviceBroadcastReceiver instead.

                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            } else {
                Log.e("wd_notice", "Activity : channel or manager is null");
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
        /*
        final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
                .findFragmentById(R.id.frag_list);
        fragment.onInitiateDiscovery();
        */
            manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    Toast.makeText(WiFiDirectActivity.this, "Discovery Initiated",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int reasonCode) {
                    Toast.makeText(WiFiDirectActivity.this, "Discovery Failed : " + reasonCode,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    View.OnClickListener connectClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(WiFiDirectActivity.this, "null",Toast.LENGTH_SHORT).show();
            setDeviceName("hogehoge");
        }
    };

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
                    Log.d("wd_notice","setDeviceName succeeded");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d("wd_notice","setDeviceName failed");
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
    public void connect(WifiP2pConfig config) {
        manager.connect(channel, config, new ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(WiFiDirectActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
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
