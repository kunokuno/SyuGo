package jp.enpitsu.paseri.syugo.Rader;

/**
 * Created by iyobe on 2016/09/29.
 */
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

public class SensorFilter {

    private ArrayList<Float> mFirst     = new ArrayList<Float>();
    private ArrayList<Float> mSecond    = new ArrayList<Float>();
    private ArrayList<Float> mThrad     = new ArrayList<Float>();

    private ArrayList<Float> mFirst_diff360     = new ArrayList<Float>();
    private ArrayList<Float> mSecond_diff360    = new ArrayList<Float>();
    private ArrayList<Float> mThrad_diff360     = new ArrayList<Float>();

    public int sampleCount=9;//サンプリング数
    public int sampleNum = 5;//サンプリングした値の使用値のインデックス

    private float[] mParam = new float[3];// フィルタをかけた後の値
    private float[] lastParam = new float[3];// 前回フィルタをかけた結果の値

    private boolean mSampleEnable=false;//規定のサンプリング数に達したか

    /**
     * フィルタをかけた値を返す
     * @return
     */
    public float[] getParam()
    {
        return mParam;
    }

    /**
     * サンプリングする値を追加
     * @param sample 要素３のfloatの配列オブジェクト
     */
    public void addSample(float[] sample)
    {
        //サンプリング数の追加
        mFirst.add(sample[0]);
        mSecond.add(sample[1]);
        mThrad.add(sample[2]);

        mFirst_diff360.add(sample[0] -360f);
        mSecond_diff360.add(sample[1]-360f);
        mThrad_diff360.add(sample[2]-360f);



        for( int i = 0; i < sample.length; ++i ) {
            if( sample[i] < 0 ) sample[i] = sample[i] + 360;
        }

        if( sample[0] < 0 || sample[1] < 0 || sample[2] < 0 )
            Log.d( "Sensor minus", sample[0] + "," + sample[1] + "," + sample[2] );


        //必要なサンプリング数に達したら
        if(mFirst.size() == sampleCount)
        {
            // TODO: 0と360の境界をなくしたい

            //メディアンフィルタ(サンプリング数をソートして中央値を使用)かけて値を取得
            //その値にさらにローパスフィルタをかける

            ArrayList<Float> lst = (ArrayList<Float>) mFirst.clone();
            ArrayList<Float> lst_diff = (ArrayList<Float>) mFirst_diff360.clone();
            Collections.sort(lst);
            Collections.sort(lst_diff);
            float tmp0 =  (mParam[0]*0.9f) + lst.get(sampleNum)*0.1f;
            float tmp0_diff =(mParam[0]*0.9f) + lst_diff.get(sampleNum)*0.1f;

            if ( Math.abs(mParam[0] - tmp0) > Math.abs(mParam[0] - tmp0_diff)  ) {
                mParam[0] = tmp0_diff;
            } else mParam[0] = tmp0;


            lst = (ArrayList<Float>) mSecond.clone();
            lst_diff = (ArrayList<Float>) mSecond_diff360.clone();
            Collections.sort(lst);
            Collections.sort(lst_diff);
            float tmp1 =  (mParam[1]*0.9f) + lst.get(sampleNum)*0.1f;
            float tmp1_diff =(mParam[1]*0.9f) + lst_diff.get(sampleNum)*0.1f;

            if ( Math.abs(mParam[1] - tmp0) > Math.abs(mParam[1] - tmp0_diff)  ) {
                mParam[1] = tmp0_diff;
            } else mParam[1] = tmp0;


            lst = (ArrayList<Float>) mThrad.clone();
            lst_diff = (ArrayList<Float>) mThrad_diff360.clone();
            Collections.sort(lst);
            Collections.sort(lst_diff);
            float tmp2 =  (mParam[2]*0.9f) + lst.get(sampleNum)*0.1f;
            float tmp2_diff =(mParam[2]*0.9f) + lst_diff.get(sampleNum)*0.1f;

            if ( Math.abs(mParam[2] - tmp0) > Math.abs(mParam[2] - tmp0_diff)  ) {
                mParam[2] = tmp0_diff;
            } else mParam[2] = tmp0;


            mSampleEnable = true;

            //一番最初の値を削除
            mFirst.remove(0);
            mSecond.remove(0);
            mThrad.remove(0);

            mFirst_diff360.remove(0);
            mSecond_diff360.remove(0);
            mThrad_diff360.remove(0);

        }
    }

    /**
     * 規定のサンプリング数に達したか
     * @return
     */
    public boolean isSampleEnable()
    {
        return mSampleEnable;
    }

}
