package jp.enpitsu.paseri.syugo.Global;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import jp.enpitsu.paseri.syugo.Rader.LocationData;

/**
 * Created by Prily on 2016/11/24.
 */

class UserInfo {
    public String name,id;
    public LocationData locationData;
    public UserInfo(String name, String id, LocationData loc){
        this.name = name;
        this.id = id;
        this.locationData = loc;
    }

}

public class SyugoApp extends android.app.Application {
    private final String TAG = "SyuGoApp";

    private UserInfo self = new UserInfo("self","init",new LocationData(0,0,0));
    private UserInfo opponent = new UserInfo("opp","init",new LocationData(0,0,0));

    /* -----------------------------------------------------

    life cycle

    ------------------------------------------------------- */

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"SyuGo!");
        loadUserInfo();
    }

    @Override
    public void onTerminate() {
        //呼ばれません（かなしい）
        Log.d(TAG,"kaisan...");
        super.onTerminate();
    }

    /* -----------------------------------------------------

    save & load

    ------------------------------------------------------- */

    public void saveUserInfo (){
        SharedPreferences data = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putString("self_name",self.name);
        editor.putString("self_id",self.id);
        editor.putString("opp_name",opponent.name);
        editor.putString("opp_id",opponent.id);
        editor.apply();
        Log.d(TAG,"data save");
    }

    private void loadUserInfo(){
        SharedPreferences data = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        self.name = data.getString("self_name","null");
        self.id = data.getString("self_id","null");
        self.name = data.getString("opp_name","null");
        self.id = data.getString("opp_id","null");
        Log.d(TAG,"data load");
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

    public void dump(){
        Log.d(TAG,self.name);
        Log.d(TAG,self.id);
        Log.d(TAG,opponent.name);
        Log.d(TAG,opponent.id);
    }
}
