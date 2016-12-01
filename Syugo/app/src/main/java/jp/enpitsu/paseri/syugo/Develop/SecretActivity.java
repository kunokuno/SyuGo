package jp.enpitsu.paseri.syugo.Develop;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import jp.enpitsu.paseri.syugo.Global.SyugoApp;
import jp.enpitsu.paseri.syugo.R;

/**
 * Created by Prily on 2016/12/01.
 */

public class SecretActivity extends Activity {

    public static final String TAG = "secret_activity";

    // app
    SyugoApp app;

    // ui
    Button reset_button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (SyugoApp) getApplication();
        reset_button = (Button) findViewById(R.id.sc_data_reset);
        reset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.resetUserInfo();
            }
        });
    }

}
