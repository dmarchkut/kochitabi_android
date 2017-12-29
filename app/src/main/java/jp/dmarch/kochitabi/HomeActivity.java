package jp.dmarch.kochitabi;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Map<String, Object>> spotsData;
    private DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setTitle("こちたびAR"); // ウインドウタイトルを「こちたびAR」に変更
        dataBaseHelper = new DataBaseHelper(); // DataBaseHelperのインスタンス化

        ImageButton sendSpotButton = (ImageButton) findViewById(R.id.imageSpotButton);
        ImageButton sendCameraButton = (ImageButton) findViewById(R.id.imageCameraButton);
        ImageButton sendMapButton = (ImageButton) findViewById(R.id.imageMapButton);

        sendSpotButton.setOnClickListener(this);
        sendCameraButton.setOnClickListener(this);
        sendMapButton.setOnClickListener(this);
    }

    public void onClick(View v) {

        Intent intent = null;

        switch (v.getId()) {
            case R.id.imageSpotButton:
                intent = new Intent(getApplication(), SpotActivity.class);
                break;
            case R.id.imageCameraButton:
                intent = new Intent(getApplication(), CameraActivity.class);
                break;
            case R.id.imageMapButton:
                intent = new Intent(getApplication(), MapActivity.class);
                final String activityName = "HomeActivity"; // MapActivityでの遷移判断に使用する変数
                intent.putExtra("activity_name", activityName);
                break;
        }
        
        startActivity(intent);
    }

/*
    protected void onResume() {
        super.onResume();

        // DataBaseHelperのgetSpotsDataから全観光地のデータを取得
        // spotsData = dataBaseHelper.getSpotsData();

        // スライドショーに表示される写真(観光地)を決定するメソッド
        // Map<String, Object> spotData = decideSlideshowPhoto(spotsData);

        // displaySlideshowPhoto(spotData); // 写真スライドショーを表示するメソッド
    }

    private Map decideSlideshowPhoto(ArrayList<Map<String, Object>> spotsData) {

        Map<String, Object> spotData = null;
        return spotData;
    }

    private void displaySlideshowPhoto(Map<String, Object> spotData) {

    }
*/
}
