package jp.enpitsu.paseri.syugo.Start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import jp.enpitsu.paseri.syugo.Lookfor.LookActivity;
import jp.enpitsu.paseri.syugo.R;
import jp.enpitsu.paseri.syugo.Registor.RegActivity;

/**
 * Created by owner on 2016/10/25.
 */
public class StartActivity extends Activity {

    LinearLayout btn_registmode;
    LinearLayout btn_searchmode;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btn_registmode = (LinearLayout) findViewById(R.id.button_regist);
        btn_searchmode = (LinearLayout) findViewById(R.id.button_search);

        btn_registmode.setOnClickListener(regListener);
        btn_searchmode.setOnClickListener(seaListener);
    }

    //ユーザ登録ボタンの処理
    private View.OnClickListener regListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent_reg = new Intent(StartActivity.this, RegActivity.class);
            try {
                    startActivity(intent_reg);
            } catch (Exception e){
                Log.d("StartActivity","intent error to RegActivity");
            }
        }
    };

    //検索モードへボタンの処理
    private View.OnClickListener seaListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent_sea = new Intent(StartActivity.this, LookActivity.class);
            try {
                startActivity(intent_sea);
            } catch (Exception e) {
                Log.d("StartActivity", "intent error to MainActivity");
            }
        }
    };
}
