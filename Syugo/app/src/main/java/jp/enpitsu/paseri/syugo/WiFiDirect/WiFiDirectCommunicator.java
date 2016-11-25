package jp.enpitsu.paseri.syugo.WiFiDirect;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Prily on 2016/11/24.
 */

public class WiFiDirectCommunicator implements WifiP2pManager.ConnectionInfoListener, Handler.Callback {

    public static final String TAG = "wifi_direct_comnktor";

    static final int PORT = 8898;
    static final int TIMEOUT = 5000;
    public static final int MESSAGE_READ = 0x400 + 1;
    public static final int MY_HANDLE = 0x400 + 2;

    WiFiDirectActivity activity;
    ChatManager chatManager;
    TextView chatLine;

    WiFiDirectCommunicator(WiFiDirectActivity activity){
        this.activity = activity;
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
        activity.setOpponentDeviceInformation(p2pInfo.toString());

        Thread handler = null;
        /*
         * The group owner accepts connections using a server socket and then spawns a
         * client socket for every client. This is handled by {@code
         * GroupOwnerSocketHandler}
         */

        if (p2pInfo.isGroupOwner) {
            Log.d(TAG, "Connected as group owner");
            try {
                handler = new GroupOwnerSocketHandler(new Handler());
                handler.start();
            } catch (IOException e) {
                Log.d(TAG,
                        "Failed to create a server thread - " + e.getMessage());
                return;
            }
        } else {
            Log.d(TAG, "Connected as peer");
            handler = new ClientSocketHandler(
                    new Handler(),
                    p2pInfo.groupOwnerAddress);
            handler.start();
        }
    }


    // receive message
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                Log.d(TAG, readMessage);
                //activity.pushMessage("Buddy: " + readMessage);
                break;

            case MY_HANDLE:
                Object obj = msg.obj;
                chatManager = (ChatManager) obj;

        }
        return true;
    }

    // send message
    View.OnClickListener chatsendClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (chatManager != null) {
                chatManager.write(chatLine.getText().toString().getBytes());
                //pushMessage("Me: " + chatLine.getText().toString());
                chatLine.setText("");
                chatLine.clearFocus();
            }
        }
    };

}
