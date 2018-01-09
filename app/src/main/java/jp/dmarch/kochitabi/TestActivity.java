package jp.dmarch.kochitabi;

import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by monki on 2018/01/10.
 */

public class TestActivity extends AppCompatActivity {

    LocationAcquisition locationAcquisition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        locationAcquisition = new LocationAcquisition(this);
        locationAcquisition.beginLocationAcquisition(); // 現在地計測開始
    }

    @Override
    protected void onResume() {
        super.onResume();
        Double[] currentLocation = locationAcquisition.getCurrentLocation(); // 現在地取得

        // 現在地を表示
        TextView textView_currentLocation = findViewById(R.id.textView_currentLocation);
        textView_currentLocation.setText("現在の緯度:"+currentLocation[0].toString()
                                +"\n現在の経度:"+currentLocation[1].toString());

        Double currentLocation2[] = {5.0, 5.0}; // 現在地座標
        Double spotLocation[] = {0.0, 0.0}; // 観光地座標
        Double distance = locationAcquisition.getDistance(currentLocation2, spotLocation); // 距離計算

        // 距離を表示
        TextView textView_distance = findViewById(R.id.textView_distance);
        textView_distance.setText("距離："+ String.format("%1$.0f",distance));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationAcquisition.endLocationAcquisition(); // 現在地計測終了
    }
}
