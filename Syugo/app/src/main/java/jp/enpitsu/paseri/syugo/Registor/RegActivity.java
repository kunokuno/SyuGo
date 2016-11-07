package jp.enpitsu.paseri.syugo.Registor;

import android.app.Activity;
import android.content.Intent;
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

    Button btn_issue;
    Button btn_share;
    Button btn_findmode;
    EditText id_box;
    TextView text_idshow;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        btn_issue = (Button)findViewById(R.id.button_issue);
        btn_share = (Button)findViewById(R.id.button_share);
        btn_findmode = (Button)findViewById(R.id.button_findmode);
        id_box = (EditText)findViewById(R.id.text_id);
        text_idshow = (TextView)findViewById(R.id.id_show);

        btn_issue.setOnClickListener(issListener);
        btn_share.setOnClickListener(shaListener);
        btn_findmode.setOnClickListener(findListener);
    }
    //ID発行ボタンの処理
    private View.OnClickListener issListener = new View.OnClickListener() {
        public void onClick(View v) {
            text_idshow.setText("r3uhr3");

        }
    };

    //ID共有ボタンの処理
    private View.OnClickListener shaListener = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                Intent intent_sha = new Intent();
                intent_sha.setAction(Intent.ACTION_SEND);
                intent_sha.setType("text/plain");
                intent_sha.putExtra(Intent.EXTRA_TEXT,text_idshow.getText().toString());
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
