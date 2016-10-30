package jp.enpitsu.paseri.syugo.Rader;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


// HttpUrlConnection
// http://yukimura1227.blog.fc2.com/blog-entry-36.html

// * AsyncTask<[1], [2], [3]>
// [1] doInBackgroundメソッドの引数の型
// [2] onProgressUpdateメソッドの引数の型
// [3] onPostExecuteメソッドの引数の型(doInBackgroundメソッドの戻り値)

public class HttpCommunication extends AsyncTask<Integer, Integer, LocationData>
{
    String myID, reqID;

    double lat, lon, acc;

    boolean TorF = false;

    MainActivity activity;

    TextView tv_response, tv_distance;

    // Activiyへのコールバック用interface
    public interface AsyncTaskCallback {
        void postExecute(LocationData result);
    }

    private AsyncTaskCallback callback = null;

    HttpCommunication( AsyncTaskCallback callback ) {
        this.callback = callback;
    }


    // 自分のid(コード)<id>
    @Override
    protected LocationData doInBackground(Integer... id) {

        StringBuilder uri = new StringBuilder(
                "http://ubermensch.noor.jp/enPiT/get_gps.php?" +
                        "code=" + myID + "&opponentcode=" + reqID + "&alt=30" +
                        "&lat=" + lat + "&lan=" + lon + "&accuracy=" + acc + "&etime=20" );

        Log.d("HttpURL", uri.toString());

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch( MalformedURLException e ) {
            Log.d("HttpRes", e.toString());
        }

        HttpURLConnection urlConnection = null;
        String result = "";
        try {
            // 接続用HttpURLConnectionオブジェクト作成
            urlConnection = (HttpURLConnection)url.openConnection();
            // リクエストメソッドの設定
            urlConnection.setRequestMethod("GET");

            urlConnection.setRequestProperty( "charset", "utf8" );

            // 結果の受信
            // レスポンスコードを受け取る
            final int responseCode = urlConnection.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK ) {
                throw new RuntimeException("invalid responce code : " + responseCode);
            }

            // 受信データ処理
            result = recieveResult( urlConnection.getInputStream() );


        } catch( IOException e ) {
            Log.d("HttpRes", e.toString());
        } finally {
            urlConnection.disconnect();
        }



        LocationData data = new LocationData( 30, 30, 30 );
        //if( HttpStatus.SC_OK == status ) {
        if( !result.equals("") ) { // データを受け取れている場合
            try {
                Log.d("Httpfhasuiogb", result);
                // ","で分割
                // items [0]名前, [1]高度, [2]緯度, [3]経度, [4]精度
                String[] items = result.split(",");

                // 結果をdataに格納
                data = new LocationData( Double.parseDouble(items[2]), Double.parseDouble(items[3]), Double.parseDouble(items[4]) );

            } catch( Exception e ) {
                Log.d("Http", e.toString());
            }
        }

        return data;
    }

    @Override
    protected void onPostExecute( LocationData result ) {
        callback.postExecute( result );

        Log.d( "Http", "onPostExecute" );
    }




    String recieveResult( InputStream in ) throws IOException
    {
        BufferedReader br = new BufferedReader( new InputStreamReader( in ) );

        // 先頭行が空行以外の場合はエラー
        boolean error_responce = false;
        String s = "";
        String out = "";

        // 2行目以降
        while( null != ( s = br.readLine() ) ) {
            if( 0 != out.length() ) {
                out += s;
            } else {
                out += "\n" + s;
            }
        }

        if( error_responce ) {
            throw new RuntimeException( "failuer of analyze: " + out );
        }

        return out;
    }

    void setID( String myID, String reqID ) {
        this.myID = myID;
        this.reqID = reqID;
    }

    void setLocation( double lat, double lon, double acc ) {
        this.lon = lon;
        this.lat = lat;
        this.acc = acc;
    }

}