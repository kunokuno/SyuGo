package jp.enpitsu.paseri.syugo.Rader.ARObjects.OpenGLES20;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.util.Log;

/**
 * Created by soniyama on 2016/09/28.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    private static final int OPENGL_ES_VERSION = 2;
    private MyRenderer mRenderer;

    ////////////////////////////////////////////////////////////
    // コンパス用のセンサ関連
    private SensorManager mSensorManager = null;
    private SensorEventListener mSensorEventListener = null;

    private float[] fAccell = null;
    private float[] fMagnetic = null;
    ///////////////////////////////////////////////////////////

    //センサー
    private SensorManager sensorManager = null;
    //センサーのフラグ
    private boolean sensorMRegisted = false;
    private boolean sensorARegisted = false;

    //回転行列
    private static final int MATRIX_SIZE = 16;
    float[] inR = new float[MATRIX_SIZE];
    float[] outR = new float[MATRIX_SIZE];
    float[] I = new float[MATRIX_SIZE];

    //センサーの値保持
    private float[] orientationValues = new float[3];
    private float[] magneticValues = new float[3];
    private float[] accelerometerValues = new float[3];

    //傾きの角度
    private int[] orientDgrees = new int[3];
    Context context;

    public MyGLSurfaceView(Context context) {
        super(context);
        this.context = context;

        mRenderer = new MyRenderer( this );

        setEGLConfigChooser(8, 8, 8, 8, 0, 0); // setRendererする前にやらんとerror吐く
        setEGLContextClientVersion( OPENGL_ES_VERSION );
        setRenderer( mRenderer );
//        setRenderMode( RENDERMODE_WHEN_DIRTY );
        setRenderMode( RENDERMODE_CONTINUOUSLY );

        setZOrderOnTop(true);                            // 最前面に描画
        getHolder().setFormat(PixelFormat.TRANSLUCENT); // 透明部分を透過
    }

    // ARActivityからRendere→RaderObjectへ中継貿易
    public void invalidateRader( String tag, float direction ) {
        mRenderer.invalidateRader( tag, direction );
    }
    public void invalidateRader( String tag, float direction, float distance ) {
        mRenderer.invalidateRader( tag, direction, distance );
        Log.d( "DISTANCE", "distance@MyGLSurfaceView = " + distance );
    }

//    // ARActivityからRendere→EffectObjectへ中継貿易
//    public void invalidateElevation( double elevation ) {
//        mRenderer.invalidateElevation( elevation );
//    }

    // ARActivityからRendererへカメラアングルを横流し
    public void setCameraAngle( float[] angle ) {
//        mRenderer.setCameraAngle( angle );
    }

    public void switchModeAR( boolean isModeAR ) {
        mRenderer.switchModeAR( isModeAR );
    }
}
