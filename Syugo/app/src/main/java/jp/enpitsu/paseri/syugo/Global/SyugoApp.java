package jp.enpitsu.paseri.syugo.Global;

import android.util.Log;

import jp.enpitsu.paseri.syugo.Rader.LocationData;

/**
 * Created by Prily on 2016/11/24.
 */

class UserInfo {
    public String name;
    public String id;
    public LocationData locationData;
}

public class SyugoApp extends android.app.Application {
    private final String TAG = "SyuGoApp";

    private UserInfo opponent;
    private UserInfo self;

    /* -----------------------------------------------------

    life cycle

    ------------------------------------------------------- */

    @Override
    public void onCreate() {
        Log.d(TAG,"SyuGo!");
    }

    @Override
    public void onTerminate() {
        Log.d(TAG,"解散...");
    }


    /* -----------------------------------------------------

    getter & setter

    ------------------------------------------------------- */

    public void setSelfUserInfo(String name, String id){
        self.name = name;
        self.id = id;
    };

    public String getSelfUserName(){
        return self.name;
    }

    public String getSelfUserId(){
        return self.id;
    }

    public void setOpponentUserInfo(String name, String id){
        opponent.name = name;
        opponent.id = id;
    };

    public String getOpponentUserName(){
        return opponent.name;
    }

    public String getOpponentUserId(){
        return opponent.id;
    }

}
