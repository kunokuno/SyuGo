package jp.enpitsu.paseri.syugo.Rader;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by iyobe on 2016/12/05.
 * カメラとGPSの
 * パーミッション取得とかしてけろ
 */
public class PermissionManager extends RaderActivity {

    private int REQUEST_CODE_CAMERA_PERMISSION = 0x01;
    RaderActivity raderActivity;

    PermissionManager( RaderActivity raderActivity ) {
        this.raderActivity = raderActivity;
    }

    // Permission handling for Android 6.0
    public void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale( raderActivity,
                Manifest.permission.CAMERA)) {

            Log.d( "REQUEST PERMISSION", "shouldShowRequestPermissionRationale:追加説明");
            // 権限チェックした結果、持っていない場合はダイアログを出す
            new AlertDialog.Builder( raderActivity )
                    .setTitle("パーミッションの追加説明")
                    .setMessage("このアプリで写真を撮るにはパーミッションが必要です")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions( raderActivity,
                                    new String[]{ Manifest.permission.CAMERA },
                                    REQUEST_CODE_CAMERA_PERMISSION );
                        }
                    })
                    .create()
                    .show();
            return;
        }

        // 権限を取得する
        ActivityCompat.requestPermissions( raderActivity, new String[]{
                        Manifest.permission.CAMERA
                },
                REQUEST_CODE_CAMERA_PERMISSION);
        return;
    }

    public void onRequestPermissionsResult( int requestCode,
                                           String permissions[], int[] grantResults ) {

        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults.length != 1 ||
                    grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Log.d("REQUEST PERMISSION", "onRequestPermissionsResult:DENYED");

                if (ActivityCompat.shouldShowRequestPermissionRationale( raderActivity,
                        Manifest.permission.CAMERA)) {
                    Log.d("REQUEST PERMISSION", "[show error]");
                    new AlertDialog.Builder( raderActivity )
                            .setTitle("パーミッション取得エラー")
                            .setMessage("再試行する場合は、再度[AR ON/OFF]ボタンを押してください")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // 許可されなかった場合
                                    // ARモード終了
                                    raderActivity.button_AR.setChecked( false );
                                }
                            })
                            .create()
                            .show();

                } else {
                    Log.d("REQUEST PERMISSION", "[show app settings guide]");
                    new AlertDialog.Builder( raderActivity )
                            .setTitle("パーミッション取得エラー")
                            .setMessage("今後は許可しないが選択されました。アプリ設定＞権限をチェックしてください（権限をON/OFFすることで状態はリセットされます）")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    openSettings();
                                }
                            })
                            .create()
                            .show();
                    // 許可されなかった場合②
                    raderActivity.button_AR.setChecked( false );
                }
            } else {
                Log.d("REQUEST PERMISSION", "onRequestPermissionsResult:GRANTED");
                // 許可されたのでカメラにアクセス(AR起動)
                raderActivity.startARMode();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        //Fragmentの場合はgetContext().getPackageName()
        Uri uri = Uri.fromParts( "package", raderActivity.getPackageName(), null );
        intent.setData( uri );
        raderActivity.startActivity( intent );
    }
}
