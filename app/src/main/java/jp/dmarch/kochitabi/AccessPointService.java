package jp.dmarch.kochitabi;

import android.os.IBinder;
import android.os.Handler;
import android.app.Service;
import android.content.Intent;
import java.util.Timer;
import java.util.TimerTask;

public class AccessPointService extends Service {

    private Timer timer = null;
    private Handler handler = new Handler();
    private String raspberrypiNumber;

    @Override
    public void onCreate() {
        raspberrypiNumber = "pi0001";
    }

    /* メインの処理 */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // タイマーの設定 1秒毎にループ
        timer = new Timer(true);
        timer.schedule( new TimerTask(){
            @Override
            public void run(){
                handler.post(new Runnable() {
                    public void run(){
                            sendBroadCast(raspberrypiNumber);
                    }
                });
            }
        }, 1000, 1000); // delayミリ秒あとにperiodミリ秒間隔でタスク実行
        return START_STICKY;
    }

    /* レシーバーに値を渡す */
    private void sendBroadCast(String raspberrypiNumber) {
        Intent intent = new Intent();
        intent.putExtra("raspberrypiNumber", raspberrypiNumber);
        intent.setAction("UPDATE_ACTION");
        getBaseContext().sendBroadcast(intent);
    }

    /* 処理終了 */
    @Override
    public void onDestroy() {
        // タイマー停止
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}

