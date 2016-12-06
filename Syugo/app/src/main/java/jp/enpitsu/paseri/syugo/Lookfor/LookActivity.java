package jp.enpitsu.paseri.syugo.Lookfor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import jp.enpitsu.paseri.syugo.R;
import jp.enpitsu.paseri.syugo.Rader.RaderActivity;

public class LookActivity extends Activity {

    private Button search;
    private ImageButton find;
    private TextView name,id2, e_message;
    private EditText id;
    private String oppName, macAdr, reqID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look);

        id2 = (TextView) findViewById(R.id.id_show);
        name = (TextView) findViewById(R.id. name_show);
        e_message = (TextView) findViewById(R.id.error);
        search = (Button) findViewById(R.id.button1);
        find = (ImageButton) findViewById(R.id.button2);
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

//                            // 検索結果として"0"が返ってきた場合，ふつうに出力すると"0"だけどbyteとかlengthとか見ると別のものもくっついてるっぽい
//                            // のでID検索一致0の場合の判定で妙なことしてます
//                            Log.d("result byte  ", result.getBytes() + ", " + "0".getBytes() );
//                            Log.d("result length", "" + result.length() );
//                            Log.d("result char  ", (int)result.charAt(0) + ", " + (int)result.charAt(1) + " : " + (int)'0' );

                            if( "error".equals( result ) ) { // サーバ側の不具合で検索に失敗した場合"error"が入ってる
                                name.setText( "接続エラー" );
                            }
                            else if( '0' == result.charAt(1) ) { // reqIDに一致するIDのユーザ名が見つからなかった場合
                                name.setText( "失敗" );
                            }
                            else {
                                try {
                                    // resultは「相手のユーザ名,MACアドレス」の形で返ってくる
                                    oppName = result.substring(1, result.indexOf(",") + 0);
                                    // 最初から","が現れるまでの部分文字列(なんか先頭文字に改行が入ってるっぽいのでインデックス1～を指定)
                                    macAdr = result.substring(result.indexOf(",") + 1, result.length());
                                    // ","の次の文字から最後までの部分文字列
                                    name.setText(oppName); // [検索結果]相手のユーザ名を表示
                                } catch ( Exception e ) {
                                    name.setText( result );
                                    Log.d("@LookActivity", "postExecute -> error:" + e.toString() );
                                }
                            }
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
            if(TextUtils.isEmpty(oppName) == false) {
            try {
                Intent intent_find = new Intent(LookActivity.this, RaderActivity.class);
                intent_find.putExtra("reqID", reqID);         // 相手ID
                intent_find.putExtra("macAdr", macAdr);       // MACアドレス
                intent_find.putExtra("oppName", oppName);     // 相手のユーザ名
                startActivity(intent_find);
            } catch (Exception e) {
                Log.d("LookActivity", "intent error RaderActivity");
            }
        }
        else{
            e_message.setText("正しいIDを入力して下さい");
                Log.d("LookActivity","UserName is null.");
        }
        }

    };
}
