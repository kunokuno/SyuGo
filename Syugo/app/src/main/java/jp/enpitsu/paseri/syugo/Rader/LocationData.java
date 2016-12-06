package jp.enpitsu.paseri.syugo.Rader;


/**
 * Created by soniyama on 2016/08/29.
 */
public class LocationData {
    double lat, lon, acc;
    long gettime; // 情報取得時間
    public LocationData( double lat, double lon, double acc ) {
        this.lat = lat;
        this.lon = lon;
        this.acc = acc;

        // 現在の時刻を取得
        this.gettime = System.currentTimeMillis();
    }

    public LocationData( double lat, double lon, double acc, long gettime ) {
        this.lat  = lat;
        this.lon  = lon;
        this.acc  = acc;
        this.gettime = gettime;
    }

}
