package jp.dmarch.kochitabi;

import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double[] currentLocation = locationAcquisition.getCurrentLocation();

                // 現在地を表示
                TextView textView_currentLocation = findViewById(R.id.textView_currentLocation);
                textView_currentLocation.setText("緯度:"+currentLocation[0].toString()
                        +"\n経度:"+currentLocation[1].toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Double[] currentLocation = locationAcquisition.getCurrentLocation();
        TextView textView_currentLocation = findViewById(R.id.textView_currentLocation);
        textView_currentLocation.setText("緯度:"+currentLocation[0].toString()
                +"\n経度:"+currentLocation[1].toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationAcquisition.endLocationAcquisition(); // 現在地計測終了
    }
}
