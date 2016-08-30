package jp.enpitsu.paseri.syugo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class GraphView extends View {

    private Paint mPaint = new Paint();

    private double rotation = 0;
    private double deviceDirection = 0, locationDirection = 0;

    // [コンストラクタ]
    public GraphView(Context context, AttributeSet attr) {
        super( context, attr );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float diameter; // 円の直径
        float centerX, centerY; // 円の中心

        Log.d( "OOKISA", this.getWidth() + ", " + this.getHeight() );
        // Viewの高さと幅、小さいほうを円の直径[diameter]とする
        if( this.getWidth() < this.getHeight() ) diameter = this.getWidth();
        else diameter = this.getWidth();

        ViewGroup.LayoutParams params = this.getLayoutParams();
        // 縦幅に合わせる
        params.width = (int)diameter;
        params.height = (int)diameter;
        this.setLayoutParams(params);

        // 円の線の太さだけ引く
        diameter = diameter - 40;

        // 円の中心の座標を求める
        centerX = this.getWidth()/2;
        centerY = this.getHeight()/2;

        // 背景、透明
        canvas.drawColor(Color.argb(0, 0, 0, 0));

        // とむ君デザイン再現
        // 円
        mPaint.setColor(Color.argb( 255, 50, 255, 219 ));
        mPaint.setStrokeWidth(40);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        // (x1,y1,r,paint) 中心x1座標, 中心y1座標, r半径
        canvas.drawCircle( centerX, centerY, diameter/2, mPaint );


        // 円弧
        float top, bottom, right, left;
        float diff = 50;
        top = (this.getHeight() - diameter- diff/2)/2;
        left = (this.getWidth() - diameter-diff)/2;
        bottom = top + diameter-diff/2;
        right = left + diameter-diff;

        mPaint.setColor(Color.argb( 255, 255, 255, 255));
        // キャンバスを回転　（-120は円弧を扱いやすい初期位置にするための調整）
        canvas.rotate( -120 + (float)rotation, centerX, centerY );
        // 「矩形に内接する円」の円弧を描く
        RectF rectf = new RectF( left, top, right, bottom );
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawArc( rectf, 0, 60, true, mPaint );

    }

    public void onLocationChanged( double direction ) {
        // 角度更新
        this.locationDirection = direction;

        // rotationを更新
        getRotate();
    }

    // 端末の向きを取得し、rotationを更新
    public void onDeviceDirectionChanged( double direction ) {
        // 角度更新
        this.deviceDirection = direction;

        // rotation更新
        getRotate();
    }

    private void getRotate() {
        // - [端末の向き] + [相手のいる方角]
        rotation = -deviceDirection + locationDirection;
        this.invalidate();
    }
}
