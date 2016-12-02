package jp.enpitsu.paseri.syugo.Registor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import jp.enpitsu.paseri.syugo.Global.SyugoApp;
import jp.enpitsu.paseri.syugo.Lookfor.LookActivity;
import jp.enpitsu.paseri.syugo.R;

/*
 * Created by owner on 2016/09/25.
 */

public class RegActivity extends Activity {

    private Button btn_issue;       //ID発行ボタン
    private ImageButton btn_share;       //ID共有ボタン
    private ImageButton btn_findmode;   //検索モードに遷移するボタン
    private EditText id_box;        //ユーザ名を入力するbox
    private TextView text_idshow;   //サーバで発行されたIDを表示する領域
    private String user_name,myID;
    private TextView message,btnmsg_sh,btnmsg_se,error_message;

    private String wifi_key = "paselow_cathy";
    private SyugoApp app;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        //xmlファイルとの紐づけ
        btn_issue = (Button)findViewById(R.id.button_issue);
        btn_share = (ImageButton)findViewById(R.id.button_share);
        btn_findmode = (ImageButton)findViewById(R.id.button_findmode);
        id_box = (EditText)findViewById(R.id.text_id);
        text_idshow = (TextView)findViewById(R.id.id_show);
        message = (TextView)findViewById(R.id.front_msg);
        btnmsg_sh = (TextView)findViewById(R.id.text_share);
        btnmsg_se = (TextView)findViewById(R.id.text_search);
        error_message = (TextView)findViewById(R.id.error_msg);
        app = (SyugoApp)this.getApplication();

        app.loadUserInfo();
        user_name = app.getSelfUserName();
        myID = app.getSelfUserId();
        id_box.setText(user_name, TextView.BufferType.NORMAL);
        text_idshow.setText(myID);

        //フォント設定
        btn_issue.setTypeface( Typeface.createFromAsset( getAssets(), "FLOPDesignFont.ttf" ), Typeface.NORMAL );
        id_box.setTypeface( Typeface.createFromAsset( getAssets(), "FLOPDesignFont.ttf" ), Typeface.NORMAL );
        text_idshow.setTypeface( Typeface.createFromAsset( getAssets(), "FLOPDesignFont.ttf" ), Typeface.NORMAL );
        message.setTypeface( Typeface.createFromAsset( getAssets(), "FLOPDesignFont.ttf" ), Typeface.NORMAL );
        btnmsg_sh.setTypeface( Typeface.createFromAsset( getAssets(), "FLOPDesignFont.ttf" ), Typeface.NORMAL );
        btnmsg_se.setTypeface( Typeface.createFromAsset( getAssets(), "FLOPDesignFont.ttf" ), Typeface.NORMAL );
        error_message.setTypeface( Typeface.createFromAsset( getAssets(), "FLOPDesignFont.ttf" ), Typeface.NORMAL );

        error_message.setGravity(Gravity.CENTER);


        //各ボタンのClickListenerの宣言
        btn_issue.setOnClickListener(issListener);
        btn_share.setOnClickListener(shaListener);
        btn_findmode.setOnClickListener(findListener);

        // ボタンの幅，高さが決定してから幅=高さに揃える
        // ViewTreeObserverを利用
        // 参考 : http://tech.admax.ninja/2014/09/17/how-to-get-the-height-and-width-of-the-view/
        //        https://anz-note.tumblr.com/post/96096731156/androidで動的に縦幅あるいは横幅に合わせて正方形のviewを作成したい
        final ViewTreeObserver observer = btn_share.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // ボタンの幅=高さにする
//                        Log.d("btn", btn_share.getWidth() + ", " + btn_share.getHeight());
                        ViewGroup.LayoutParams params = btn_share.getLayoutParams();
                        // 短辺の長さに長辺を揃える
                        if (btn_share.getWidth() < btn_share.getHeight())
                            params.height = btn_share.getWidth();
                        else params.width = btn_share.getHeight();

                        btn_share.setLayoutParams( params );
                        btn_findmode.setLayoutParams( params );

                        removeOnGlobalLayoutListener(btn_share.getViewTreeObserver(), this);
                    }
                });
    }

    // onGlobalLayout()が1回目以降呼ばれないようにリス名を外す
    private static void removeOnGlobalLayoutListener(ViewTreeObserver observer, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (observer == null) {
            return ;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            observer.removeGlobalOnLayoutListener(listener);
        } else {
            observer.removeOnGlobalLayoutListener(listener);
        }
    }




    //ID発行ボタンの処理
    private View.OnClickListener issListener = new View.OnClickListener() {
        public void onClick(View v) {
            user_name = id_box.getText().toString();
            if(TextUtils.isEmpty(user_name) == false && myID == null) {
                HttpComOnRegistor httpComReg = new HttpComOnRegistor(
                        new HttpComOnRegistor.AsyncTaskCallback() {
                            @Override
                            public void postExecute(String result) {
                                myID = result.replaceAll("\n", "");
                                text_idshow.setText(myID);
                                app.setSelfUserInfo(user_name,myID);
                                app.saveUserInfo();
                                Log.d("PrilyClass_name",app.getSelfUserName());
                                Log.d("PrilyClass_id",app.getSelfUserId());
                            }
                        }
                );
                httpComReg.setUserInfo(user_name, wifi_key);
                httpComReg.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            else {
                if (TextUtils.isEmpty(user_name) == true) {
                    error_message.setText("YourNameが入力されていません．");
                    Log.d("RegActivity", "UserName is null.");
                }
                else if(myID != null) {
                    error_message.setText("IDはすでに発行されています．");
                    Log.d("RegActivity", "UserID is showed already.");
                }
                else {
                    Log.d("RegActivity", "mystery error.");
                }
            }
        }
    };

    //ID共有ボタンの処理
    private View.OnClickListener shaListener = new View.OnClickListener() {
        public void onClick(View v) {
            if(myID!=null) {
                try {
                    Intent intent_sha = new Intent();
                    intent_sha.setAction(Intent.ACTION_SEND);
                    intent_sha.setType("text/plain");
                    intent_sha.putExtra(Intent.EXTRA_TEXT, "集GO!しよう(*・・)ノ\n 私( "+ user_name +" )のIDは \n" + text_idshow.getText().toString() + " \nです．");
                    startActivity(intent_sha);
                } catch (Exception e) {
                    Log.d("ActionSend", "intent other app error");
                }
            }
            else {
                error_message.setText("あなたのIDを発行してください．");
                Log.d("RegActivity", "UserID is null.");
            }
        }
    };

    //検索モードへボタンの処理
    private View.OnClickListener findListener = new View.OnClickListener() {
        public void onClick(View v) {
            //登録画面からAR画面への遷移
            if(myID != null) {
                try {
                    Intent intent_find = new Intent(RegActivity.this, LookActivity.class);
                    intent_find.putExtra("myID", myID);
                    startActivity(intent_find);
                } catch (Exception e) {
                    Log.d("RegActivity", "intent error MainActivity");
                }
            }
            else{
                error_message.setText("あなたのIDを発行してください．");
                Log.d("RegActivity","UserID is null.");
            }
        }
    };
}
