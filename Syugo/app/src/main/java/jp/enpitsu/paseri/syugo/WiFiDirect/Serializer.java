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


/*
    1 byte : type(code) (0x00 = String , 0x01 = LocationData)
    4 byte : byte length
    n byte : data

 */


// Static Class
public final class Serializer {
    static final byte CHAT = 0x00;
    static final byte LOCATION = 0x01;
    static final byte HEARTBEAT = 0x0F;
    static final byte[] ping = {HEARTBEAT};


    /* ---------------
        Encoder
    -------------- */

    static public final byte[] Encode(String str){
        byte code = CHAT;
        return construction(code,str.getBytes());
    }

    static public final byte[] Encode(LocationData loc){
        byte code = LOCATION;
        return construction(code,loc.getBytes());
    }

    static private final byte[] construction(byte code, byte[] bytes){
        int size = Byte.SIZE/8 + Integer.SIZE/8 + bytes.length;
        ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.clear();
        buffer.put(code);
        buffer.putInt(bytes.length);
        buffer.put(bytes);
        return buffer.array();
    };

    /* ---------------
        Decoder
    -------------- */

    static public final Pair<Byte,Object> Decode(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int len;
        byte[] data;
        byte code = buffer.get();
        switch (code) {
            case CHAT:
                len = buffer.getInt();
                data = new byte[len];
                buffer.get(data, 0, len);
                String str = new String(data);
                return new Pair<Byte, Object>(code, (Object) str);
            case LOCATION:
                len = buffer.getInt();
                data = new byte[len];
                buffer.get(data, 0, len);
                LocationData loc = new LocationData(data);
                return new Pair<Byte, Object>(code, (Object) loc);
            case HEARTBEAT:
                return new Pair<Byte, Object>(code, null);
            default:
                Log.e("wifi_direct_serializer", "Unknown type error");
                return null;
        }
    }
}
