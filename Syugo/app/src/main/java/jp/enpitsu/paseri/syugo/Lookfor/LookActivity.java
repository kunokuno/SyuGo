package jp.enpitsu.paseri.syugo.Lookfor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import jp.enpitsu.paseri.syugo.R;
import jp.enpitsu.paseri.syugo.Rader.RaderActivity;

public class LookActivity extends Activity {

    private Button find,search;
    private TextView name,id2;
    private EditText id;
    private String your_id, str, oppName, macAdr, reqID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look);

        id2 = (TextView) findViewById(R.id.id_show);
        name = (TextView) findViewById(R.id. name_show);
        search = (Button) findViewById(R.id.button1);
        find = (Button) findViewById(R.id.button2);
        id = (EditText) findViewById(R.id.id_enter);
        search.setOnClickListener(searchListener);
        find.setOnClickListener(findListener);
    }
    // コードでユーザー検索"name,mac"
    private View.OnClickListener searchListener = new View.OnClickListener() {
        public void onClick(View v) {
            reqID = id.getText().toString(); // 相手ID入力テキストボックスから相手のID取得
            id2.setText( reqID ); // 入力された相手IDをメッセージ部分に表示

            HttpComLookFor httpComLookFor = new HttpComLookFor(
                    new HttpComLookFor.AsyncTaskCallback() {
                        @Override
                        public void postExecute(String result) {
                            // resultは「相手のユーザ名,MACアドレス」の形で返ってくる
                            oppName  = result.substring( 1, result.indexOf(",")+0 );
                                // 最初から","が現れるまでの部分文字列(なんか先頭文字に改行が入ってるっぽいのでインデックス1～を指定)
                            macAdr   = result.substring( result.indexOf(",")+1, result.length() );
                                // ","の次の文字から最後までの部分文字列
                            name.setText( oppName ); // [検索結果]相手のユーザ名を表示
                        }
                    }
            );
            httpComLookFor.setUserInfo( reqID );
            httpComLookFor.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR );
        }
    };

    //検索ボタン押してマップ画面へ
    private View.OnClickListener findListener = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                Intent intent_find = new Intent(LookActivity.this, RaderActivity.class);
                intent_find.putExtra( "reqID", reqID );         // 相手ID
                intent_find.putExtra( "macAdr", macAdr );       // MACアドレス
                intent_find.putExtra( "oppName", oppName );     // 相手のユーザ名
                startActivity(intent_find);
            } catch (Exception e) {
                Log.d("LookActivity", "intent error RaderActivity");
            }
        }

    };
}
