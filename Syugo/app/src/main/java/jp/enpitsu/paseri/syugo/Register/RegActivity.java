package jp.enpitsu.paseri.syugo.Register;

import android.os.Bundle;

import jp.enpitsu.paseri.syugo.R;

/**
 * Created by owner on 2016/09/25.
 */
public class RegActivity extends Activity{

    Button btn_issue;
    Button btn_share;
    Button btn_findmode;
    EditText id_box;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        btn_issue = (Button)findViewById(R.id.button_issue);
        btn_share = (Button)findViewById(R.id.button_share);
        btn_findmode = (Button)findviewById(R.id.button_findmode);
        id_box = (EditText)findViewById(R.id.text_id);

        btn_issue.setOnClickListener(issListener);
        btn_share.setOnClickListener(shaListener);
        btn_findmode.setOnClickListener(findListener);
    }
    //ID発行ボタンの処理
    private OnClickListener issListener = new OnClickListener() {
        public void onClick(View v) {

        }
    };

    //ID共有ボタンの処理
    private OnClickListener shaListener = new OnClickListener() {
        public void onClick(View v) {
            try {
                Intent intent_sha = new Intent();
                intent_sha.setAction(Intent.ACTION_SEND);
                intent_sha.setType("text/plain");
                intent_sha.putExtra(Intent.EXTRA_TEXT,id_box.getText().toString());
                startActivity(intent_sha);
            } catch (Exception e){
                Log.d("ActionSend","intent other app error");
            }
        }
    };

    //検索モードへボタンの処理
    private OnClickListener findListener = new OnClickListener() {
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
