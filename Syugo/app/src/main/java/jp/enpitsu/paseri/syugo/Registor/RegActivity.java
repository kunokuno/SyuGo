package jp.enpitsu.paseri.syugo.Registor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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

    private String wifi_key = "paselow_cathy";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        //xmlファイルとの紐づけ
        btn_issue = (Button)findViewById(R.id.button_issue);
        btn_share = (ImageButton)findViewById(R.id.button_share);
        btn_findmode = (ImageButton)findViewById(R.id.button_findmode);
        id_box = (EditText)findViewById(R.id.text_id);
        text_idshow = (TextView)findViewById(R.id.id_show);

        //各ボタンのClickListenerの宣言
        btn_issue.setOnClickListener(issListener);
        btn_share.setOnClickListener(shaListener);
        btn_findmode.setOnClickListener(findListener);
    }

    //ID発行ボタンの処理
    private View.OnClickListener issListener = new View.OnClickListener() {
        public void onClick(View v) {
            user_name = id_box.getText().toString();

            HttpComOnRegistor httpComReg = new HttpComOnRegistor(
                    new HttpComOnRegistor.AsyncTaskCallback() {
                        @Override
                        public void postExecute(String result) {
                            myID = result;
                            text_idshow.setText(myID);
                        }
                    }
            );
            httpComReg.setUserInfo(user_name,wifi_key);
            httpComReg.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR );


        }
    };

    //ID共有ボタンの処理
    private View.OnClickListener shaListener = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                Intent intent_sha = new Intent();
                intent_sha.setAction(Intent.ACTION_SEND);
                intent_sha.setType("text/plain");
                intent_sha.putExtra(Intent.EXTRA_TEXT,"集GO!しよう(*・・)ノ 私のIDは "+text_idshow.getText().toString()+" です．");
                startActivity(intent_sha);
            } catch (Exception e){
                Log.d("ActionSend","intent other app error");
            }
        }
    };

    //検索モードへボタンの処理
    private View.OnClickListener findListener = new View.OnClickListener() {
        public void onClick(View v) {
            //登録画面からAR画面への遷移
            try {
                Intent intent_find = new Intent(RegActivity.this, LookActivity.class);
                intent_find.putExtra("myID",myID);
                startActivity(intent_find);
            } catch (Exception e) {
                Log.d("RegActivity", "intent error MainActivity");
            }
        }
    };
}
