package jp.enpitsu.paseri.syugo.Develop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import jp.enpitsu.paseri.syugo.Global.SyugoApp;
import jp.enpitsu.paseri.syugo.R;
import jp.enpitsu.paseri.syugo.Rader.LocationData;
import jp.enpitsu.paseri.syugo.Start.StartActivity;
import jp.enpitsu.paseri.syugo.WiFiDirect.WiFiDirect;

/**
 * Created by Prily on 2016/12/01.
 */

public class SecretActivity extends Activity {

    public static final String TAG = "secret_activity";
    WiFiDirect wfd;

    // app
    SyugoApp app;

    // ui
    Button ping_button;
    CompoundButton wfd_button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret);
        app = (SyugoApp) getApplication();
        wfd = new WiFiDirect(SecretActivity.this);

        //reset savedata
        ping_button = (Button) findViewById(R.id.sc_ping);
        ping_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wfd.sendMessage("hoge");
                wfd.sendGPSLocation(new LocationData(1.2,1.4,1.5));
            }
        });

        //for wifi direct
        wfd_button = (CompoundButton) findViewById(R.id.sc_wifi_direct);
        wfd.setCompoundButton(wfd_button);
    }

    @Override
    public void onResume(){
        super.onResume();
        wfd.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        wfd.onPause();
    }

}
