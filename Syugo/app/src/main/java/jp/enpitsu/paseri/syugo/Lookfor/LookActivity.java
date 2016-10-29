package jp.enpitsu.paseri.syugo.Lookfor;

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

public class LookActivity extends Activity {

    TextView name;
    Button search ;
    Button find;
    EditText id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look);

        name = (TextView) findViewById(R.id.name_show);
        search = (Button) findViewById(R.id.button1);
        find = (Button) findViewById(R.id.button2);

        search.setOnClickListener(searchListener);
        find.setOnClickListener(findListener);
    }
    // 入力:r3uhr3, 出力: k
    private View.OnClickListener searchListener = new View.OnClickListener() {
        public void onClick(View v) {
            name.setText("k");

            }
        };

    //検索ボタン押してマップ画面へ
    private View.OnClickListener findListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(LookActivity.this, MainActivity.class);
            try {
                startActivity(intent);
            } catch (Exception e) {
                Log.d("LookActivity", "intent error MainActivity");
            }
        }
    };
}
