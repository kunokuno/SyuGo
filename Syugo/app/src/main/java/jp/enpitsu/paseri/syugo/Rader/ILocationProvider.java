package jp.enpitsu.paseri.syugo.Rader;


public interface ILocationProvider {

    /**
     * ［ホストアクティビティのライフサイクル］アクティビティがユーザー操作可能になる時に呼び出されます。
     */
    public void onResume();

    /**
     * ［ホストアクティビティのライフサイクル］アクティビティがユーザー操作不可能になる時に呼び出されます。
     */
    public void onPause();

}
