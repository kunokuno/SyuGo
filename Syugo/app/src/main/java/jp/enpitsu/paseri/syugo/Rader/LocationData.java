package jp.enpitsu.paseri.syugo.Rader;

/**
 * Created by soniyama on 2016/08/29.
 */
public class LocationData {
    double lat, lon, acc;
    public LocationData( double lat, double lon, double acc ) {
        this.lat = lat;
        this.lon = lon;
        this.acc = acc;
    }
}
