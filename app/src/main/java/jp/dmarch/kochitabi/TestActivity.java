package jp.dmarch.kochitabi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by monki on 2018/01/10.
 */

public class TestActivity extends AppCompatActivity {

    BluetoothAcquisition bluetoothAcquisition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        bluetoothAcquisition = new BluetoothAcquisition(this);
        //bluetoothAcquisition.beginSearchDevice(); // Bluetooth対応端末検索開始
    }

    @Override
    protected void onResume() {
        super.onResume();

        final TextView textView_deviceList = findViewById(R.id.textView_deviceList);
        final TextView textView_intensity = findViewById(R.id.textView_intensity);
        final TextView textView_accessPointDevice = findViewById(R.id.textView_accessPointDevice);

        // beginボタンを設定
        Button button_begin = findViewById(R.id.button_begin);
        button_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothAcquisition.beginSearchDevice();
            }
        });

        // endボタンを設定
        Button button_end = findViewById(R.id.button_end);
        button_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothAcquisition.endSearchDevice();
            }
        });

        // アップデートボタンを設定
        Button button_update = findViewById(R.id.button_update);
        button_update.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // Bluetooth接続可能なRaspberryPi端末のリストを取得
                ArrayList<String> deviceList = bluetoothAcquisition.getDeviceList();
                textView_deviceList.setText("端末リスト:\n"+deviceList.toString());

                if (deviceList.size() != 0) {
                    String deviceName = deviceList.get(0); // 端末名

                    // 電波強度を取得
                    int intensity = bluetoothAcquisition.getIntensity(deviceName);

                    textView_intensity.setText("端末名:"+deviceName
                                                +"\n電波強度:"+intensity);
                }

                // アクセスポイント内であれば端末を取得
                String deviceName2 = bluetoothAcquisition.checkAccessPoint();
                textView_accessPointDevice.setText("AP内端末名:"+deviceName2);
            }

        });





    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //bluetoothAcquisition.endSearchDevice(); // Bluetooth対応端末検索終了
    }

}
