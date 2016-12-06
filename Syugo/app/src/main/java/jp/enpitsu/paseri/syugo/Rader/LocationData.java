package jp.enpitsu.paseri.syugo.Rader;

import java.nio.ByteBuffer;

/**
 * Created by soniyama on 2016/08/29.
 */
public class LocationData {
    public double lat, lon, acc;
    public LocationData( double lat, double lon, double acc ) {
        this.lat = lat;
        this.lon = lon;
        this.acc = acc;
    }

    public LocationData( byte[] bytes ) {
        int size = Double.SIZE / 8 * 3;
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        this.lat = buffer.getDouble();
        this.lon = buffer.getDouble();
        this.acc = buffer.getDouble();
    }
    public byte[] getBytes() {
        int size = Double.SIZE / 8 * 3;
        ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.clear();
        buffer.putDouble(lat);
        buffer.putDouble(lon);
        buffer.putDouble(acc);
        return buffer.array();
    }
    public String dump() {
        return String.valueOf(lat) + " , " + String.valueOf(lon) + " , " + String.valueOf(acc);
    }
}
