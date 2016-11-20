package jp.enpitsu.paseri.syugo.Lookfor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
    private String your_id,str,reqID, mac_add;

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
            str = id.getText().toString();
            id2.setText(str);
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            mac_add = wifiInfo.getMacAddress();
            your_id = id.getText().toString();
            HttpComLookFor httpComLookFor = new HttpComLookFor(
                    new HttpComLookFor.AsyncTaskCallback() {
                        @Override
                        public void postExecute(String result) {
                            reqID = result;
                            name.setText(reqID);
                        }
                    }
            );
            httpComLookFor.setUserInfo(your_id, mac_add);
            httpComLookFor.executeOnExecutor( AsyncTask.THREAD_POOL_EXECUTOR );
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
