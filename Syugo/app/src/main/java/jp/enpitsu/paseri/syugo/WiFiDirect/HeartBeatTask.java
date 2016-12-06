package jp.enpitsu.paseri.syugo.WiFiDirect;

import java.util.TimerTask;

/**
 * Created by Prily on 2016/12/06.
 */

public class HeartBeatTask extends TimerTask {
    static boolean respond = true;
    WiFiDirectCommunicator communicator;

    HeartBeatTask(WiFiDirectCommunicator communicator){
        this.communicator = communicator;
    }

    @Override
    public void run() {
        // send Heartbeat
        if (respond){
            communicator.sendHeartBeat();
            respond = false;
        }else{
            this.cancel();
        }
    }
}
