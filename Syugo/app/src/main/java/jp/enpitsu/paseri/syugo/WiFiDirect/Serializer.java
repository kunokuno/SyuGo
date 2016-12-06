package jp.enpitsu.paseri.syugo.WiFiDirect;

import android.location.Location;
import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;
import android.util.Pair;

import java.nio.ByteBuffer;

import jp.enpitsu.paseri.syugo.Rader.LocationData;

/**
 * Created by Prily on 2016/12/06.
 */


// Static Class
public final class Serializer {
    static final byte CHAT = 0x00;
    static final byte LOCATION = 0x01;

    /* ---------------
        Encoder
    -------------- */

    static public byte[] Encode(String str){
        byte code = CHAT;
        return construction(code,str.getBytes());
    }

    static public byte[] Encode(LocationData loc){
        byte code = LOCATION;
        return construction(code,loc.getBytes());
    }

    static private byte[] construction(byte code, byte[] bytes){
        int size = bytes.length + Character.SIZE;
        ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.clear();
        buffer.put(code);
        buffer.put(bytes);
        return buffer.array();
    };

    /* ---------------
        Decoder
    -------------- */

    static public Pair<Byte,Object> Decode(byte[] bytes) {
        int len = bytes.length;
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        byte code = buffer.get();
        switch (code) {
            case CHAT:
                byte[] str_bytes = new byte[len - 1];
                buffer.get(str_bytes, 1, len - 1);
                String str = new String(str_bytes);
                return new Pair<Byte, Object>(code, (Object) str);
            case LOCATION:
                byte[] loc_bytes = new byte[len - 1];
                buffer.get(loc_bytes, 1, len - 1);
                LocationData loc = new LocationData(loc_bytes);
                return new Pair<Byte, Object>(code, (Object) loc);
            default:
                Log.e("wifi_direct_serializer", "Unknown type error");
                return null;
        }
    }
}
