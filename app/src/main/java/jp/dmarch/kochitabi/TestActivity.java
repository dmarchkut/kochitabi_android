package jp.dmarch.kochitabi;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Map;

/**
 * Created by monki on 2018/01/15.
 */

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        TextView textView = findViewById(R.id.textView);
        final TextView textView2 = findViewById(R.id.textView2);
        Button buttonIn = findViewById(R.id.button_in);
        Button buttonOut = findViewById(R.id.button_out);

        Handler updateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                Log.d("Test", "updateHandler");
                // データを受け取る
                /*Bundle bundle = msg.getData();
                String raspberrypiNumber = bundle.getString("raspberrypiNumber");*/

            }
        };

        AccessPointReceiver receiver = new AccessPointReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("UPDATE_ACTION");
        registerReceiver(receiver, intentFilter);
        receiver.registerHandler (updateHandler);

        // サービスクラスを開始する
        Intent intent = new Intent(getApplication(), AccessPointService.class);
        startService(intent);

        buttonIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        buttonOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // サービスクラスを終了する
        Intent intent = new Intent(getApplication(), AccessPointService.class);
        stopService(intent);
    }
}
