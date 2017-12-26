package jp.dmarch.kochitabi;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class SpotDetailActivity extends AppCompatActivity {

    private Map<String, Object> spotData;
    private LocationAcquisition locationAcquisition;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotdetail);

        // 前画面に戻る機能をつける
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);      // バックボタン追加

        // 前画面(SpotActivity)からのspotDataを受け取る
        Intent intent = getIntent();
        final String spotId = intent.getStringExtra("spot_id");
        final String environmentId = intent.getStringExtra("environment id");
        final String spotName = intent.getStringExtra("spot_name");
        final String spotPhoname = intent.getStringExtra("spot_phoname");
        final String streetAddress = intent.getStringExtra("street_address");
        final Integer postalCode = (Integer) intent.getIntExtra("postal_code", 0);
        final Double latitude = intent.getDoubleExtra("latitude", 0);
        final Double longitude = intent.getDoubleExtra("longitude", 0);
        final String photoFilePath = intent.getStringExtra("photo_file_path");

        spotData = new HashMap<String, Object>();

        // spotDataにデータを挿入
        spotData.put("spot_id", spotId);
        spotData.put("environment id", environmentId);
        spotData.put("spot_name", spotName);
        spotData.put("spot_phoname", spotPhoname);
        spotData.put("street_address", streetAddress);
        spotData.put("postal_code", postalCode);
        spotData.put("latitude", latitude);
        spotData.put("longitude", longitude);
        spotData.put("photo_file_path", photoFilePath);

        setTitle(spotName); // ウインドウタイトルを観光地名に変更

        locationAcquisition = new LocationAcquisition(this); // LocationAcquisitionのインスタンス化
        locationAcquisition.beginLocationAcquisition(); // LocationAcquisitionのbeginLocationAcquisitionを呼び出し

        // MapActivityに飛ぶボタンを実装
        ImageButton sendButton = findViewById(R.id.imageButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), MapActivity.class); // 遷移先のMapActivityを設定する

                // キーの値を全てMapから取り出す
                final String spotId = spotData.get("spot_id").toString();
                final String environmentId = spotData.get("environment id").toString();
                final String spotName = spotData.get("spot_name").toString();
                final String spotPhoname = spotData.get("spot_phoname").toString();
                final String streetAddress = spotData.get("street_address").toString();
                final Integer postalCode = new Integer(spotData.get("postal_code").toString()).intValue();
                final Double latitude = new Double(spotData.get("latitude").toString()).doubleValue();
                final Double longitude = new Double(spotData.get("longitude").toString()).doubleValue();
                final String photoFilePath = spotData.get("photo_file_path").toString();

                // MapActivityに渡す値を付与する
                intent.putExtra("spot_id", spotId);
                intent.putExtra("environment id", environmentId);
                intent.putExtra("spot_name", spotName);
                intent.putExtra("spot_phoname", spotPhoname);
                intent.putExtra("street_address", streetAddress);
                intent.putExtra("postal_code", postalCode);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("photo_file_path", photoFilePath);

                startActivity(intent); // MapActivityに遷移する
            }
        });
    }

    protected void onResume(){
        super.onResume();

        // Mapから観光地ID・環境データを取り出す
        final String spotId = spotData.get("spot_id").toString();
        final String environmentId = spotData.get("environment id").toString();
        final String spotName = spotData.get("spot_name").toString();
        final Double latitude = new Double(spotData.get("latitude").toString()).doubleValue();
        final Double longitude = new Double(spotData.get("longitude").toString()).doubleValue();

        DataBaseHelper dataBaseHelper = new DataBaseHelper(); // DataBaseHelperをインスタンス化

        // DataBaseHelperのgetEnvironmentDataから環境IDに対応した環境データセットを取得する
        Map<String, Object> environmentData = dataBaseHelper.getEnvironmentData(environmentId);

        // DataBaseHelperのgetSpotTextから観光地IDに対応した観光地案内テキストを取得する
        String spotText = dataBaseHelper.getSpotText(spotId);

        // LocationAcquisitionのgetCurrentLocationを呼び出し、現在地の緯度・経度を取得する
        Double[] currentLocation = locationAcquisition.getCurrentLocation();

        // Mapから取り出した緯度・経度を配列に格納する
        Double[] spotLocation = {latitude, longitude};

        // LocationAcquisitionのgetDistanceから現在地～観光地の距離を取得する
        Double distance = locationAcquisition.getDistance(currentLocation, spotLocation);

        // 取得したデータを引数として、displaySpotDetailを呼び出し、情報を画面に表示する
        displaySpotDetail(spotData, environmentData, spotText, distance);
    }

    // 引数の情報を画面に表示するメソッド
    private void displaySpotDetail (Map<String, Object> spotData, Map<String, Object> environmentData, String spotText, Double distance) {

        // spotDataから表示を行うデータを取得する
        final String spotName = spotData.get("spot_name").toString(); // 観光地名
        final String photoFilePath = spotData.get("photo_file_path").toString(); // 観光地写真
        final String weather = environmentData.get("weather").toString(); // 天気
        final Double temperature = new Double(environmentData.get("temperature").toString()).doubleValue(); // 気温

        // XMLとの対応付けを行う
        TextView spotNameText = (TextView)findViewById(R.id.spotNameTextView); // 観光地名
        TextView distanceText = (TextView)findViewById(R.id.distanceTextView); // 距離
        TextView weatherText = (TextView)findViewById(R.id.weatherTextView);  // 天気
        TextView temperatureText = (TextView)findViewById(R.id.temperatureTextView); // 気温
        TextView spotDetailText = (TextView)findViewById(R.id.spotDetailTextView); // 観光地案内テキスト
        ImageView spotImage = findViewById(R.id.spotImageView); // 観光地写真

        // 表示する内容をセットする
        spotNameText.setText(spotName); // 観光地名
        distanceText.setText("距離: " + String.valueOf(distance) + " km"); // 距離
        weatherText.setText("天気: "+ weather); // 天気
        temperatureText.setText("気温: " + String.valueOf(temperature) + " 度"); // 気温
        spotDetailText.setMovementMethod(ScrollingMovementMethod.getInstance()); // 観光地案内テキスト(スクロール)
        spotDetailText.setText(spotText); //　観光地案内テキスト

        // プレビュの表示
        Bitmap bitmap = BitmapFactory.decodeFile(photoFilePath);
        spotImage.setImageBitmap(bitmap);
    }

    protected void onDestroy() {
        super.onDestroy();
        // LocationAcquisitionのendLocationAcquisitionを呼び出す
        locationAcquisition.endLocationAcquisition();
    }

    // 戻るボタンをタップした時に実行される
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
