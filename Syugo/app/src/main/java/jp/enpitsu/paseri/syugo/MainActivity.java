/*
    円グラフレーダーとか位置情報取得とかであっちゃこっちゃしてるキメラプログラム
    もうちょっときれいに書けると思う
 */

package jp.enpitsu.paseri.syugo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;


public class MainActivity extends Activity {

    ///////////////////////////////////////////////////////////////////////////
    String myID = "r3uhr3";
    String reqID = "4hfeu";
//    String myID = "4hfeu";
//    String reqID = "r3uhr3";

    private double lat = 30;
    private double lon = 30;

    private Button button;
    private boolean flag_vibrator = true;

    GraphView graphView;

    TextView textView_DistanceMessage;
    TextView textView_AccuracyMessage;
    TextView textView_Message;

    ////////////////////////////////////////////////////////////
    // コンパス用のセンサ関連
    private SensorManager mSensorManager = null;
    private SensorEventListener mSensorEventListener = null;

    private float[] fAccell = null;
    private float[] fMagnetic = null;
    ///////////////////////////////////////////////////////////

    /** 位置情報の更新を受信するためのリスナー。これを、ARchitectViewに通知して、ARchitect Worldの位置情報を更新します。*/
    protected LocationListener locationListener;

    /** 最も基本的なLocation戦略のサンプル実装（※「 http://goo.gl/pvkXV 」を参照）。LocationProvider.javaファイルのコードを自由にカスタマイズして処理を洗煉させてください。*/
    protected ILocationProvider locationProvider;

    /** 最新のユーザー位置情報。本サンプルでは位置情報が取得されているかどうかの判定で使われています（※本サンプルではコードはありますが実質的には使っていません）。*/
    protected Location lastKnownLocaton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        graphView = (GraphView)findViewById(R.id.graph_view);

        textView_Message = (TextView)findViewById( R.id.textView_Message );
        textView_DistanceMessage = (TextView)findViewById( R.id.textView_DistanceMessage );
        textView_AccuracyMessage = (TextView)findViewById( R.id.textView_AccuracyMessage );
        button = (Button)findViewById(R.id.button_vibration);

        // フォント設定
        textView_Message.setTypeface( Typeface.createFromAsset( getAssets(), "irohamaru-Regular.ttf" ), Typeface.NORMAL );
        textView_AccuracyMessage.setTypeface( Typeface.createFromAsset( getAssets(), "irohamaru-Regular.ttf" ), Typeface.NORMAL );
        textView_DistanceMessage.setTypeface( Typeface.createFromAsset( getAssets(), "irohamaru-Regular.ttf" ), Typeface.BOLD );
        button.setTypeface( Typeface.createFromAsset( getAssets(), "irohamaru-Regular.ttf" ), Typeface.NORMAL );

        ////////////////////////////////////////////////////////////////////////////////////////////
        // センサのコピペ //////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        mSensorManager = (SensorManager) getSystemService( Context.SENSOR_SERVICE );

        mSensorEventListener = new SensorEventListener()
        {
            public void onSensorChanged (SensorEvent event) {
                // センサの取得値をそれぞれ保存しておく
                switch( event.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        fAccell = event.values.clone();
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        fMagnetic = event.values.clone();
                        break;
                }

                // fAccell と fMagnetic から傾きと方位角を計算する
                if( fAccell != null && fMagnetic != null ) {
                    // 回転行列を得る
                    float[] inR = new float[9];
                    SensorManager.getRotationMatrix(
                            inR,
                            null,
                            fAccell,
                            fMagnetic );
                    // ワールド座標とデバイス座標のマッピングを変換する
                    float[] outR = new float[9];
                    SensorManager.remapCoordinateSystem(
                            inR,
                            SensorManager.AXIS_X, SensorManager.AXIS_Y,
                            outR );
                    // 姿勢を得る
                    float[] fAttitude = new float[3];
                    SensorManager.getOrientation(
                            outR,
                            fAttitude );

//                    String buf =
//                            "---------- Orientation --------\n" +
//                                    String.format( "方位角\n\t%f\n", rad2deg( fAttitude[0] )) +
//                                    String.format( "前後の傾斜\n\t%f\n", rad2deg( fAttitude[1] )) +
//                                    String.format( "左右の傾斜\n\t%f\n", rad2deg( fAttitude[2] ));
//                    textView_DistanceMessage.setText( buf );

                    graphView.onDeviceDirectionChanged( rad2deg( fAttitude[0] ) );
                }
            }
            public void onAccuracyChanged (Sensor sensor, int accuracy) {}
        };
        ////////////////////////////////////////////////////////////////////////////////////////////


        ////////////////////////////////////////////////////////////////////////////////////////////
        // 位置情報関連のコピペ ////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        RequestPermission();
        if (this.isFinishing()) return;

        //  位置情報のリスナーを登録します。全ての位置情報更新はここで処理され、ここから本アプリ内で一元的に位置情報を管理するプロバイダー「locationProvider」に引き渡されます。
        this.locationListener = new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            @Override
            public void onLocationChanged(final Location location) {

                lat = location.getLatitude();
                lon = location.getLongitude();


                HttpCommunication httpCommunication = new HttpCommunication(
                        new HttpCommunication.AsyncTaskCallback() {
                            @Override
                            public void postExecute(LocationData result) {
                                getDistance( result );
                            }
                        }
                );
                httpCommunication.setID( myID, reqID );
                httpCommunication.setLocation( location.getLatitude(), location.getLongitude(), location.getAccuracy() );
                httpCommunication.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR );
            }
        };

        // 位置情報を収集するために使うLocationProviderに、位置情報リスナー（locationListener）を指定してインスタンスを生成・取得
        this.locationProvider = getLocationProvider(this.locationListener);

        ////////////////////////////////////////////////////////////////////////////////////////////
    }


    // [振動止める/つける]ボタン押下
    public void onBottonClick( View v ) {
        Log.d("onButtonClick", "onButtonClick");
        if( flag_vibrator == true ) {
            flag_vibrator = false;
            button.setText("振動つける");
        }
        else {
            flag_vibrator = true;
            button.setText("振動止める");
        }
    }



    /**
     * ［アクティビティのライフサイクル］アクティビティがユーザー操作可能になる時に呼び出されます。
     */
    @Override
    protected void onResume() {
        super.onResume();


        // LocationProviderのライフサイクルメソッド「onResume」を呼び出す必要があります。通常、Resumeが通知されると位置情報の収集が再開され、ステータスバーのGPSインジケーターが点灯します。
        if (this.locationProvider != null) {
            this.locationProvider.onResume();
        }
    }

    void getDistance( LocationData data ) {
        float[] results = new float[3];
        // 距離を計算 ///////////////////////////
        // results[0] : 距離（メートル）
        //        [1] : 始点から終点までの方位角
        //        [2] : 終点から始点までの方位角
        Location.distanceBetween( lat, lon, data.lat, data.lon, results);


        // 円グラフを回転
        graphView.onLocationChanged( results[1] );

      // 距離メッセージ変更
        if( results[0] <= 20 ) textView_DistanceMessage.setText("近いよ");
        else if( results[0] == 0 ) textView_DistanceMessage.setText("やばいよ");
        else textView_DistanceMessage.setText("遠いよ");

        Log.d("httpppppp", "acc"+ data.acc );
        // 精度メッセージ変更
        if( data.acc <= 3 ) textView_AccuracyMessage.setText("精度良好かも");
        else if( data.acc > 3 && data.acc <= 10 ) textView_AccuracyMessage.setText("ふつうの精度");
        else if ( data.acc >= 15 ) textView_AccuracyMessage.setText("精度ひどいよ");
//        else if ( data.acc >= 15 ) textView_AccuracyMessage.setText("不安な精度");
        else textView_AccuracyMessage.setText( "" );


        if( results[0] <= 40 && flag_vibrator == true ) {
            // ここでバイブレーション///////////////////////////////////////////
            // 振動
            viberation( results[0] );
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    double getDistance_kuno( double lat1, double lon1, double lat2, double lon2 ) {
        double lat_average = deg2rad( lat1 + ((lat2 - lat1) / 2) );//２点の緯度の平均
        double lat_difference = deg2rad( lat1 - lat2 );//２点の緯度差
        double lon_difference = deg2rad( lon1 - lon2 );//２点の経度差
        double curvature_radius_tmp = 1 - 0.00669438 * Math.pow(Math.sin(lat_average), 2);
        double meridian_curvature_radius = 6335439.327 / Math.sqrt(Math.pow(curvature_radius_tmp, 3));//子午線曲率半径
        double prime_vertical_circle_curvature_radius = 6378137 / Math.sqrt(curvature_radius_tmp);//卯酉線曲率半径

        //２点間の距離
        double distance = Math.pow(meridian_curvature_radius * lat_difference, 2) + Math.pow(prime_vertical_circle_curvature_radius * Math.cos(lat_average) * lon_difference, 2);
        distance = Math.sqrt(distance);

        return distance;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private double rad2deg(double radian) {
        return radian * (180f / Math.PI);
    }

    public double deg2rad(double degrees) {
        return degrees * (Math.PI / 180f);
    }


    /*
 * 2点間の距離を取得
 * 第五引数に設定するキー（unit）で単位別で取得できる
 */
    private double getDistance_Qiita(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) +  Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        double miles = dist * 60 * 1.1515;
        switch (unit) {
            case 'K': // キロメートル
                return (miles * 1.609344);
            case 'N': // ノット
                return (miles * 0.8684);
            case 'M': // マイル
            default:
                return miles;
        }
    }


    private double getDirection( double lat1, double lon1, double lat2, double lon2 ) {
        double diff_x = lat2 - lat1;
        double direction = 90 - Math.atan2( Math.sin( diff_x ), Math.cos( lon1 )*Math.tan( lon2 ) - Math.sin( lon1 )*Math.cos( diff_x ) );

        return direction;
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // region 各センサーへのアクセス権限に関する処理

    /**
     * アプリの実行に必要な権限をチェックして、不足していればユーザーに要求します。
     */
    private void RequestPermission() {

        List<String> permissionList = new ArrayList<String>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            Toast.makeText(this, "位置情報（GPS）が使えないと起動できません。", Toast.LENGTH_LONG).show();
        }
        if (permissionList.size() > 0) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            int REQUEST_CODE_NONE = 0;  // onRequestPermissionResultオーバーライドメソッド内では何も処理しないので、特に意味の無い数値を指定しています。
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_NONE);

            // 30秒ほど、権限設定をチェックしながら待つ
            for (int i = 0; i < 300; i++)
            {
                if (isFinishing()) return;
                try {
                    Thread.sleep(100);
                    Thread.yield();
                } catch (InterruptedException e) {
                    break;
                }

                if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                        (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                    return;
                }
            }

            // いったんアプリを終了します。
            Toast.makeText(this, "権限設定後に、もう一度アプリを起動し直してください。", Toast.LENGTH_LONG).show();
            this.finish();
        }
    }


    // region 位置情報更新の管理（※LocationProviderに一任しており、ここではそのプロバイダーを生成するのみです）

    /**
     * LocationProviderを取得します。
     * @param locationListener システムの位置情報リスナーを指定してください。
     * @return
     */
    public ILocationProvider getLocationProvider(final LocationListener locationListener) {
        return new LocationProvider(this, locationListener);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // センサ関連
    protected void onStart() { // ⇔ onStop
        super.onStart();

        mSensorManager.registerListener(
                mSensorEventListener,
                mSensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER ),
                SensorManager.SENSOR_DELAY_UI );
        mSensorManager.registerListener(
                mSensorEventListener,
                mSensorManager.getDefaultSensor( Sensor.TYPE_MAGNETIC_FIELD ),
                SensorManager.SENSOR_DELAY_UI );
    }

    protected void onStop() { // ⇔ onStart
        super.onStop();

        mSensorManager.unregisterListener( mSensorEventListener );

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////

    // 距離を受け取って、距離に応じて振動させるメソッド
    private void viberation( double distance ) {

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        // 振動パターンいくつか（数字が大きくなるにつれて間隔短めに）
        long[] pattern1 = { 0, 500, 2000, 500, 2000, 500 }; // OFF/ON/OFF/ON...
        long[] pattern2 = { 0, 500, 1000, 500, 1000, 500 }; // OFF/ON/OFF/ON...
        long[] pattern3 = { 0, 500, 700, 500, 700, 500 }; // OFF/ON/OFF/ON...
        long[] pattern4 = { 0, 500, 500, 500, 500, 500 }; // OFF/ON/OFF/ON...
        long[] pattern5 = { 0, 500, 300, 500, 300, 500 }; // OFF/ON/OFF/ON...
        long[] pattern6 = { 0, 500, 100, 500, 100, 500 }; // OFF/ON/OFF/ON...

        // ここでバイブレーション///////////////////////////////////////////
        // 振動
        if( distance <= 3 ) {
            vibrator.vibrate(pattern6, -1);
            Log.d("viberation", "pattern6");
            Toast.makeText( this, "pattern6", Toast.LENGTH_SHORT ).show();
        }
        else if( distance <= 5 ) {
            vibrator.vibrate(pattern4, -1);
            Log.d("viberation", "pattern4");
            Toast.makeText( this, "pattern4", Toast.LENGTH_SHORT ).show();
        }
        else if( distance <= 10 ) {
            vibrator.vibrate(pattern2, -1);
            Log.d("viberation", "pattern2");
            Toast.makeText( this, "pattern2", Toast.LENGTH_SHORT ).show();
        }
        else {
            vibrator.vibrate(pattern1, -1);
            Log.d("viberation", "pattern1");
            Toast.makeText( this, "pattern1", Toast.LENGTH_SHORT ).show();
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
}
