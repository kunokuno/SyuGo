
package jp.enpitsu.paseri.syugo.WiFiDirect;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientSocketHandler extends Thread {

    private static final String TAG = "wifi_direct_c_handler";
    private Handler handler;
    private GPSCommManager manager;
    private InetAddress mAddress;

    public ClientSocketHandler(Handler handler, InetAddress groupOwnerAddress) {
        this.handler = handler;
        this.mAddress = groupOwnerAddress;
    }

    @Override
    public void run() {
        Socket socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(mAddress.getHostAddress(),WiFiDirectCommunicator.PORT), WiFiDirectCommunicator.TIMEOUT);
            Log.d(TAG, "Launching the I/O handler");
            manager = new GPSCommManager(socket, handler);
            new Thread(manager).start();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
    }
}
