package jp.enpitsu.paseri.syugo.Registor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import jp.enpitsu.paseri.syugo.MainActivity;
import jp.enpitsu.paseri.syugo.R;

/**
 * Created by owner on 2016/09/25.
 */
public class RegActivity extends Activity {

    private Button btn_issue;       //ID発行ボタン
    private Button btn_share;       //ID共有ボタン
    private Button btn_findmode;   //検索モードに遷移するボタン
    private EditText id_box;        //ユーザ名を入力するbox
    private TextView text_idshow;   //サーバで発行されたIDを表示する領域
    private String user_name,myID;

    private String mac_add;         //Android端末のMacアドレスを保存

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        //xmlファイルとの紐づけ
        btn_issue = (Button)findViewById(R.id.button_issue);
        btn_share = (Button)findViewById(R.id.button_share);
        btn_findmode = (Button)findViewById(R.id.button_findmode);
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
            text_idshow.setText("r3uhr3");
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            mac_add = wifiInfo.getMacAddress();
            //mac address 確認用
            //text_idshow.setText(mac_add);


            HttpComOnRegistor httpComReg = null;


            httpComReg.setUserInfo(user_name,mac_add);
            myID = httpComReg.setCODE();

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
            Intent intent_find = new Intent(RegActivity.this, MainActivity.class);
            try {
                startActivity(intent_find);
            } catch (Exception e) {
                Log.d("RegActivity", "intent error MainActivity");
            }
        }
    };
}
