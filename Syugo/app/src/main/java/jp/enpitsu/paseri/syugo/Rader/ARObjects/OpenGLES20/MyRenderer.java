package jp.enpitsu.paseri.syugo.Rader.ARObjects.OpenGLES20;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

//レンダラー
public class MyRenderer implements GLSurfaceView.Renderer {
    //システム
    private float aspect;//アスペクト比

    private  MyGLSurfaceView glView;
    private  RaderObject_UI raderObject;
    private TargetObject targetObject;

    private float rotation;
    private double locationDirection, deviceDirection;

    private boolean isModeAR =false;

    // コンストラクタ
    MyRenderer( MyGLSurfaceView glView, Context context ) {
        this.glView = glView;
        GLES.context = context;

        rotation = 0f;
        locationDirection = 0;
        deviceDirection = 0;

//        mCamAngle = new float[] { 60f, 60f }; // カメラアングルを60度に初期化
    }


    // サーフェイス生成時に呼ばれる
    @Override
    public void onSurfaceCreated(GL10 gl10,EGLConfig eglConfig) {
        // プログラムの生成
        GLES.makeProgram();

        // 頂点配列の有効化
        GLES20.glEnableVertexAttribArray(GLES.positionHandle);
        GLES20.glEnableVertexAttribArray(GLES.normalHandle);

        // デプスバッファの有効化
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        // アルファブレンド有効化
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);

        //光源色の指定
        GLES20.glUniform4f(GLES.lightAmbientHandle,0.2f,0.2f,0.2f,1.0f);
        GLES20.glUniform4f(GLES.lightDiffuseHandle,0.7f,0.7f,0.7f,1.0f);
        GLES20.glUniform4f(GLES.lightSpecularHandle,0.0f,0.0f,0.0f,1.0f);

        raderObject = new RaderObject_UI();
        targetObject = new TargetObject();
    }

    //画面サイズ変更時に呼ばれる
    @Override
    public void onSurfaceChanged(GL10 gl10,int w,int h) {
        //ビューポート変換
        GLES20.glViewport(0,0,w,h);
        aspect=(float)w/(float)h;
    }

    //毎フレーム描画時に呼ばれる
    @Override
    public void onDrawFrame(GL10 gl10) {
        // 画面をglClearColorで指定した色で初期化
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|
                GLES20.GL_DEPTH_BUFFER_BIT);

        //射影変換
        Matrix.setIdentityM(GLES.pMatrix,0);
        GLES.gluPerspective(GLES.pMatrix,
                60.0f,  //Y方向の画角
                aspect, //アスペクト比
                0.1f,   //ニアクリップ
                100.0f);//ファークリップ

        //光源位置の指定
        GLES20.glUniform4f(GLES.lightPosHandle,0f,0f,0f,1.0f);


        Matrix.setIdentityM(GLES.mMatrix,0);
        //ビュー変換
        GLES.gluLookAt(GLES.mMatrix,
                0.0f,0.0f,0.0f, //カメラの視点
                0.0f,0.0f,-0.01f, //カメラの焦点
                0.0f,1.0f,0.0f);//カメラの上方向

        // ARのターゲット描画
        targetObject.draw( isModeAR );
        // レーダー描画
        raderObject.draw( isModeAR );

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
//    // MyGLSurfaceViewからRaderObjectへ中継貿易
//    // 端末の傾きが変わった時
//    public void invalidateRader( String tag, float direction ) {
//        Log.d( "RaderObject", "invalidateRader" );
//        if( raderObject != null ) {
//            // 角度更新
//            this.deviceDirection = direction;
//            Log.d("RaderObject", "onDeviceDirectionChanged");
//
//            // rotation更新
//            getRotate();
//        }
////        if( tag.equals("Location Changed") ) this.onLocationChanged( direction );
////        else if( tag.equals("Device Direction Changed") ) this.onDeviceDirectionChanged( direction );
//    }
//    // ロケーションが変わった時
//    public void invalidateRader( String tag, float direction, float distance ) {
//        Log.d( "RaderObject", "invalidateRader" );
//        if( raderObject != null || targetObject != null ) {
//            // 角度更新
//            this.locationDirection = direction;
//            // rotationを更新
//            getRotate();
//            Log.d( "DISTANCE", "distance@MyRenderer = " + distance );
//            // 距離更新
//            try {
//                raderObject.invalidateDistance(distance);
//                targetObject.invalidateDistance(distance);
//            } catch( Exception e ) {
//                e.toString();
//            }
//        }
//    }

//    public void onLocationChanged( float direction ) {
//        // 角度更新
//        this.locationDirection = direction;
//
//        // rotationを更新
//        getRotate();
//    }
//
//    // 端末の向きを取得し、rotationを更新
//    public void onDeviceDirectionChanged( double direction ) {
//        // 角度更新
//        this.deviceDirection = direction;
//        Log.d( "RaderObject", "onDeviceDirectionChanged" );
//
//        // rotation更新
//        getRotate();
//    }
//
//    private void getRotate() {
//        // - [端末の向き] + [相手のいる方角]
//        rotation = (float)(-deviceDirection + locationDirection);
//        Log.d( "RaderObject", "getRotate" );
//        glView.requestRender();
//        Log.d( "RaderObject", "RequestRender" );
//
//        if ( raderObject != null || targetObject != null ) {
//            try {
//                raderObject.invalidateNorthDirection( (float) deviceDirection );
//                raderObject.invalidateRotation( (float) locationDirection );
//
//                targetObject.invalidateNorthDirection( (float) deviceDirection );
//                targetObject.invalidateRotation( (float) locationDirection );
//            } catch ( Exception e ) {
//                Log.d( "getRotate@MyRenderer", e.toString() );
//            }
//        }
//    }
//
//    // 仰角更新
//    public void invalidateElevation( double elevation ) {
//        if (targetObject != null) {
//            try {
//                targetObject.invalidateElevation((float) elevation);
//            } catch ( Exception e ) {
//                Log.d( "invalidElev@MyRenderer", e.toString() );
//            }
//        }
//    }


    public void switchModeAR( boolean isModeAR ) {
        this.isModeAR = isModeAR;
    }

}
