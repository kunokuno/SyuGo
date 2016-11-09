package jp.enpitsu.paseri.syugo.Lookfor;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import jp.enpitsu.paseri.syugo.MainActivity;
import jp.enpitsu.paseri.syugo.R;
import jp.enpitsu.paseri.syugo.Rader.RaderActivity;

public class LookActivity extends Activity {

    TextView name, id2;
    Button search ;
    Button find;
    EditText id;
    SharedPreferences name_data;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look);

        id2 = (TextView) findViewById(R.id.id_show);
        name = (TextView) findViewById(R.id.name_show);
        search = (Button) findViewById(R.id.button1);
        find = (Button) findViewById(R.id.button2);
        id = (EditText) findViewById(R.id.id_enter);
        name_data = getSharedPreferences("DataStore", MODE_PRIVATE);
        search.setOnClickListener(searchListener);
        find.setOnClickListener(findListener);
    }
    // 入力:r3uhr3, 出力: k
    private View.OnClickListener searchListener = new View.OnClickListener() {
        public void onClick(View v) {
            name.setText("k");
            // エディットテキストのテキストを取得
            String text = id.getText().toString();
            id2.setText(text);
            // 入力文字列を"input"に書き込む
            SharedPreferences.Editor editor = name_data.edit();
            editor.putString("input", text);
            editor.commit();

            }
        };

    //検索ボタン押してマップ画面へ
    private View.OnClickListener findListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(LookActivity.this, RaderActivity.class);
            try {
                startActivity(intent);
            } catch (Exception e) {
                Log.d("LookActivity", "intent error MainActivity");
            }
        }
    };
}
