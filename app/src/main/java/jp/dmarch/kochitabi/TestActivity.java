package jp.dmarch.kochitabi;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.Map;

/**
 * Created by monki on 2018/01/16.
 */

public class TestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceStatus) {
        super.onCreate(savedInstanceStatus);
        setContentView(R.layout.activity_test);

    /* サービスから値を受け取ったら動かしたい内容を書く */
        Handler updateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                // データを受け取る
                Bundle bundle = msg.getData();
                String raspberrypiNumber = bundle.getString("raspberrypiNumber");

                Log.d("updateHandler", raspberrypiNumber);
            }
        };

        AccessPointReceiver receiver = new AccessPointReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("UPDATE_ACTION");
        registerReceiver(receiver, intentFilter);
        receiver.registerHandler (updateHandler);

        // サービス開始
        Intent intent = new Intent(getApplication(), AccessPointService.class);
        startService(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // サービス終了
        Intent intent = new Intent(getApplication(), AccessPointService.class);
        stopService(intent);
    }
}
