package jp.enpitsu.paseri.syugo.Rader;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.enpitsu.paseri.syugo.Rader.ARObjects.Graph.GraphView;
import jp.enpitsu.paseri.syugo.Rader.ARObjects.OpenGLES20.MyGLSurfaceView;
import jp.enpitsu.paseri.syugo.R;
import jp.enpitsu.paseri.syugo.WiFiDirect.WiFiDirect;

/**
 * Created by iyobe on 2016/09/26.
 */
public class RaderActivity extends Activity {
    private Camera2 mCamera;
    private float[] mCamAngle = null;
    private GraphView graphView;

    private MyGLSurfaceView glView;

    private PermissionManager permissionManager;

    ToggleButton button_AR, button_Vibration, button_WifiDirect;
    ImageView backgroundImageView;
    TextureView textureView;

    TextView textView_DistanceMessage;
    TextView textView_AccuracyMessage;

    //WiFiDirect
    WiFiDirect wfd;


    ////////////////////////////////////////////////////////////
    // コンパス用のセンサ関連
    private SensorManager mSensorManager = null;
    private SensorEventListener mSensorEventListener = null;

    private float[] fAccell = null;
    private float[] fMagnetic = null;
    ///////////////////////////////////////////////////////////

    // バイブレータ
    Vibrator vibrator;
    private boolean flag_vibrator = true; // 振動させるかさせないか


    //    String myID = "r3uhr3";
//    String reqID = "4hfeu";
    String myID = "4hfeu";
//    String reqID = "r3uhr3";
    String reqID; // TODO: 自分のIDは外部ファイルから読んでくる
    String macAdr, oppName; // 相手のMACアドレスとユーザ名

    private double lat = 30;
    private double lon = 30;

    /** 位置情報の更新を受信するためのリスナー。これを、ARchitectViewに通知して、ARchitect Worldの位置情報を更新します。*/
    protected LocationListener locationListener;

    /** 最も基本的なLocation戦略のサンプル実装（※「 http://goo.gl/pvkXV 」を参照）。LocationProvider.javaファイルのコードを自由にカスタマイズして処理を洗煉させてください。*/
    protected ILocationProvider locationProvider;

    /** 最新のユーザー位置情報。本サンプルでは位置情報が取得されているかどうかの判定で使われています（※本サンプルではコードはありますが実質的には使っていません）。*/
    protected Location lastKnownLocaton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // intent取得
        Intent intent = getIntent();
        // intentから文字列取得
        reqID  = intent.getStringExtra( "reqID" );      // 相手ID
//        macAdr = intent.getStringExtra( "macAdr" );     // 相手のMACアドレス
//        oppName= intent.getStringExtra( "oppName" );    // 相手のユーザ名

        glView = new MyGLSurfaceView( this );
        glView.setZOrderOnTop(true);

        permissionManager = new PermissionManager( this );

        final View view = this.getLayoutInflater().inflate(R.layout.activity_rader, null);
//        // [参考] http://language-and-engineering.hatenablog.jp/entry/20110908/p1

        // GLSurfaceViewを最初にセット
        this.setContentView( glView,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));

        // カメラプレビュー・コンパスのレイアウトをセット
        this.addContentView( view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT ));

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        backgroundImageView = (ImageView)findViewById( R.id.backgroundImageView );

        button_AR          = (ToggleButton)findViewById( R.id.button_AR );
        button_Vibration  = (ToggleButton)findViewById( R.id.button_Vibe );
        button_WifiDirect = (ToggleButton)findViewById( R.id.button_wifiDirect );

//        textView_Message = (TextView)findViewById( R.id.textView_Message );
        textView_DistanceMessage = (TextView)findViewById( R.id.textView_DistanceMessage );
        textView_AccuracyMessage = (TextView)findViewById( R.id.textView_AccuracyMessage );

        // デバッグ用
        button_info = (Button)findViewById( R.id.button_info );
        textView_info = (TextView)findViewById( R.id.textView_info );

        // フォント設定
        button_AR.setTypeface( Typeface.createFromAsset( getAssets(), "FLOPDesignFont.ttf" ), Typeface.NORMAL );
        button_WifiDirect.setTypeface( Typeface.createFromAsset( getAssets(), "FLOPDesignFont.ttf" ), Typeface.NORMAL );
        button_Vibration.setTypeface( Typeface.createFromAsset( getAssets(), "FLOPDesignFont.ttf" ), Typeface.NORMAL );
        textView_DistanceMessage.setTypeface( Typeface.createFromAsset( getAssets(), "FLOPDesignFont.ttf" ), Typeface.NORMAL );
        textView_AccuracyMessage.setTypeface( Typeface.createFromAsset( getAssets(), "FLOPDesignFont.ttf" ), Typeface.NORMAL );

        //WiFiDirectクラスのインスタンス作成とボタンの登録
        wfd = new WiFiDirect(RaderActivity.this);
        wfd.setCompoundButton(button_WifiDirect);

        textureView = (TextureView) findViewById( R.id.texture_view );
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                // 通常はここでカメラ起動
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                // mCamAnge[0] : 横の画角
                //         [1] : 縦の画角
//                mCamAngle = mCamera.getAngle();
//                glView.setCameraAngle( mCamAngle );
            }
        });
//
////        Log.d( "mew Camera", "start" );
//        mCamera = new Camera(textureView, this);
////        Log.d( "mew Camera", "end" );

        ////////////////////////////////////////////////////////////////////////////////////////////
        // センサのコピペ //////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        mSensorManager = (SensorManager) getSystemService( Context.SENSOR_SERVICE );    // SensorManager取得

        // SensorManagerに加速度センサと自機センサについてSensorEventListenerを登録
        mSensorEventListener = new SensorEventListener()
        {
            SensorFilter sensorFilter = new SensorFilter();
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

                    // fAttitude[0] : 方位角（北が0, 時計回りに値増加）
                    //          [1] : 前後の傾斜
                    //          [2] : 左右の傾斜
                    fAttitude[0] = (float)rad2deg( fAttitude[0] );  // 方位角を変換(ラジアン→度)
                    if( fAttitude[0] < 0 ) {
                        // 0～360度の値にする
                        fAttitude[0] = 360f + fAttitude[0];
                    }
                    // フィルタを掛ける
                    sensorFilter.addSample( fAttitude );

                    // サンプルが必要数溜まったら
                    if( sensorFilter.isSampleEnable() ) {
                        fAttitude = sensorFilter.getParam();

                        Log.d("ARActivity", "rotation : " + fAttitude[0] + ", " + fAttitude[1] + ", " + fAttitude[2]);

                        double direction =  fAttitude[0];           // 端末の向いてる方向
                        double elevation = rad2deg( fAttitude[1] ); // 端末の前後の傾き
                        if( direction < 0 ) {
                            // 0～360度の値にする
                            direction = 360f + direction;
                        }
                        // レーダー更新
//                        graphView.onDeviceDirectionChanged( direction );
                        glView.invalidateRader( "Device Direction Changed", (float)direction );
//                        glView.invalidateElevation( elevation );
                    }
                }
            }
            public void onAccuracyChanged (Sensor sensor, int accuracy) {}
        };
        ////////////////////////////////////////////////////////////////////////////////////////////


        ////////////////////////////////////////////////////////////////////////////////////////////
        // 位置情報関連のコピペ ////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        permissionManager.requestLocationInfoPermission();
        if (this.isFinishing()) return;

        //  位置情報のリスナーを登録します。全ての位置情報更新はここで処理され、ここから本アプリ内で一元的に位置情報を管理するプロバイダー「locationProvider」に引き渡されます。
        this.locationListener = new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("Location", "onStatusChanged");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("Location", "onProviderEnabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("Location", "onProviderDisabled");
            }

            @Override
            public void onLocationChanged(final Location location) {
                Log.d("Location", "onLcationChanged");
                lat = location.getLatitude();
                lon = location.getLongitude();


                HttpCommunication httpCommunication = new HttpCommunication(
                        new HttpCommunication.AsyncTaskCallback() {
                            @Override
                            public void postExecute(LocationData result) {
                                getDistance( result );

//                                glView.invalidateRader( "Location Changed", direction );
                            }
                        }
                );
                httpCommunication.setID( myID, reqID );
                httpCommunication.setLocation( location.getLatitude(), location.getLongitude(), location.getAccuracy() );
                httpCommunication.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR );

                Log.d( "MyLocation", location.getLatitude() + ", " + location.getLongitude() + " ( " + location.getAccuracy() + " )" );

            }
        };

        // 位置情報を収集するために使うLocationProviderに、位置情報リスナー（locationListener）を指定してインスタンスを生成・取得
        this.locationProvider = getLocationProvider(this.locationListener);
        Log.d("Location", "LocationProviderにリスナ指定");

        ////////////////////////////////////////////////////////////////////////////////////////////
    }


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

    private double rad2deg(double radian) {
        return radian * (180f / Math.PI);
    }

    public double deg2rad(double degrees) {
        return degrees * (Math.PI / 180f);
    }


    // アクティビティがユーザー操作可能になる時
    @Override
    protected void onResume() {
        super.onResume();
        wfd.onResume();

        // LocationProviderのライフサイクルメソッド「onResume」を呼び出す必要があります。通常、Resumeが通知されると位置情報の収集が再開され、ステータスバーのGPSインジケーターが点灯します。
        if (this.locationProvider != null) {
            try {
                this.locationProvider.onResume();
            } catch (Exception e ) {
                ;
            }
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        wfd.onPause();
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // region 位置情報更新の管理（※LocationProviderに一任しており、ここではそのプロバイダーを生成するのみです）
    /**
     * LocationProviderを取得します。
     * @param locationListener システムの位置情報リスナーを指定してください。
     * @return
     */
    public ILocationProvider getLocationProvider(final LocationListener locationListener) {
        return new LocationProvider(this, locationListener);
    }


    void getDistance( LocationData data ) {
        float[] results = new float[3];
        // 距離を計算 ///////////////////////////
        // results[0] : 距離（メートル）
        //        [1] : 始点から終点までの方位角
        //        [2] : 終点から始点までの方位角
        Location.distanceBetween( lat, lon, data.lat, data.lon, results);
//        Location.distanceBetween( lat, lon, 36.56815810607431, 140.6476289042621, results);
        Log.d( "DISTANCE", "distance`getDistance = " + results[0] );

        if( results[1] < 0 ) {
            // 0～360度の値にする
            results[1] = 360f + results[1];
        }

        // 円グラフを回転
        glView.invalidateRader( "Location Changed", results[1], results[0] );
//        graphView.onLocationChanged( results[1] );

        // 距離メッセージ変更
        if( results[0] <= 20 ) textView_DistanceMessage.setText("近いよ");
        else if( results[0] == 0 ) textView_DistanceMessage.setText("やばいよ");
        else textView_DistanceMessage.setText("遠いよ");

        // 精度メッセージ変更
        if( data.acc <= 3 ) textView_AccuracyMessage.setText("精度良好かも");
        else if( data.acc > 3 && data.acc <= 10 ) textView_AccuracyMessage.setText("ふつうの精度");
        else if ( data.acc >= 15 ) textView_AccuracyMessage.setText("精度ひどいよ");
//        else if ( data.acc >= 15 ) textView_AccuracyMessage.setText("不安な精度");
        else textView_AccuracyMessage.setText( "" );


        // 現在の時刻を取得
        Date date = new Date();
        // 表示形式を設定
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy'年'MM'月'dd'日'　kk'時'mm'分'ss'秒'");

        String stringInfo = "【 自分( "+ myID + " ) 】\n" +
                "lat : " + this.lat + "\n" +
                "lon : " + this.lon + "\n" +
                "acc : " + data.acc + "\n" +
                "【 相手( "+ reqID + " ) 】\n" +
                "lat : " + data.lat + "\n" +
                "lon : " + data.lon + "\n" +
                "acc : " + data.acc + "\n" +
                "【 距離 】 " + results[0] + "m\n" +
                "【 自 → 相 】 " + results[1] + "\n" +
                "【 相 → 自 】 " + results[2] + "\n" +
                "【 "+ sdf.format( date ) +" 】";    // 更新日時
        textView_info.setText( stringInfo );


        if( results[0] <= 40 && flag_vibrator == true ) {
            // ここでバイブレーション///////////////////////////////////////////
            // 振動
            viberation( results[0] );
        }

    }

    // PermissionMangerちゃんに任せましょうね～
    @Override
    public void onRequestPermissionsResult( int requestCode,
                                            String permissions[], int[] grantResults ) {
        permissionManager.onRequestPermissionsResult( requestCode, permissions, grantResults );
    }

    // ARモードのon/off切り替えボタンがクリックされたとき
    public void onARSwitchButtonClicked(View v) {
        if( button_AR.isChecked() == true ) { // OFF → ONのとき

            // パーミッションを持っているか確認する
            if (PermissionChecker.checkSelfPermission(
                    RaderActivity.this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                // パーミッションをリクエストする
                permissionManager.requestCameraPermission();
                return;
            }
            Log.d( "REQUEST PERMISSION", "パーミッション取得済み" );
            // ARモード開始
            startARMode();
        }
        else { // ON → OFFのとき
            // ARモード終了
            glView.switchModeAR( false );
            // カメラ開放
            mCamera.close();
            mCamera = null;
            // 背景差し替え(imageView表示)
            backgroundImageView.setVisibility( backgroundImageView.VISIBLE );
        }
    }

    public void startARMode() {
        glView.switchModeAR( true );
        // カメラ起動
        if ( textureView.isAvailable() == true ) {
            mCamera = new Camera2(textureView, this);
            mCamera.open();
        }

        // 背景差し替え（imageView非表示）
        backgroundImageView.setVisibility( backgroundImageView.INVISIBLE );
    }


    // [振動止める/つける]ボタン押下
    public void onVibeSwitchClicked( View v ) {
        Log.d("onButtonClick", "onButtonClick");
        if( button_Vibration.isChecked() == true ) { // OFF → ONのとき
            flag_vibrator = true;
        }
        else { // ON → OFFのとき
            flag_vibrator = false;
            // 現在動作中の振動も止める
            vibrator.cancel();
        }
    }

    // 距離を受け取って、距離に応じて振動させるメソッド
    private void viberation( double distance ) {

//        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        // 振動パターンいくつか（数字が大きくなるにつれて間隔短めに）
        long[] pattern1 = { 0, 500, 2000, 500, 2000, 500 }; // OFF/ON/OFF/ON...
        long[] pattern2 = { 0, 500, 1000, 500, 1000, 500 }; // OFF/ON/OFF/ON...
        long[] pattern3 = { 0, 500, 700, 500, 700, 500 }; // OFF/ON/OFF/ON...
        long[] pattern4 = { 0, 500, 500, 500, 500, 500 }; // OFF/ON/OFF/ON...
        long[] pattern5 = { 0, 500, 300, 500, 300, 500 }; // OFF/ON/OFF/ON...
        long[] pattern6 = { 0, 500, 100, 500, 100, 500 }; // OFF/ON/OFF/ON...

        // ここでバイブレーション///////////////////////////////////////////
        vibrator.cancel();      // 現在動作中の振動止める
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


    // デバッグ用情報を表示するTextviewの表示、非表示切り替え
    public void onClickButtonInfo( View view ) {
        if( isInfoVisible == false ) {
            textView_info.setVisibility(View.VISIBLE);
            isInfoVisible = true;
        } else {
            textView_info.setVisibility(View.GONE);
            isInfoVisible = false;
        }
    }
}