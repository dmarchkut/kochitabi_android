package jp.dmarch.kochitabi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Double.NaN;

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
        final String environmentId = intent.getStringExtra("environment_id");
        final String spotName = intent.getStringExtra("spot_name");
        final String spotPhoname = intent.getStringExtra("spot_phoname");
        final String streetAddress = intent.getStringExtra("street_address");
        final Integer postalCode = (Integer) intent.getIntExtra("postal_code", 0);
        final Double latitude = intent.getDoubleExtra("latitude", 0);
        final Double longitude = intent.getDoubleExtra("longitude", 0);
        final String photoFilePath = intent.getStringExtra("photo_file_path");

        spotData = new HashMap<String, Object>(); // spotDataのインスタンス化

        // spotDataにデータを挿入
        spotData.put("spot_id", spotId);
        spotData.put("environment_id", environmentId);
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
                final String spotId = (String) spotData.get("spot_id");
                final String environmentId = (String) spotData.get("environment_id");
                final String spotName = (String) spotData.get("spot_name");
                final String spotPhoname = (String) spotData.get("spot_phoname");
                final String streetAddress = (String) spotData.get("street_address");
                final Integer postalCode = (Integer) spotData.get("postal_code");
                final Double latitude = (Double) spotData.get("latitude");
                final Double longitude = (Double) spotData.get("longitude");
                String photoFilePath = null;
                if (spotData.get("photo_file_path") != null) photoFilePath = (String) spotData.get("photo_file_path");
                final String activityName = "SpotDetailActivity"; // MapActivityでの遷移判断に使用する変数

                // MapActivityに渡す値を付与する
                intent.putExtra("spot_id", spotId);
                intent.putExtra("environment_id", environmentId);
                intent.putExtra("spot_name", spotName);
                intent.putExtra("spot_phoname", spotPhoname);
                intent.putExtra("street_address", streetAddress);
                intent.putExtra("postal_code", postalCode);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("photo_file_path", photoFilePath);
                intent.putExtra("activity_name", activityName);

                startActivity(intent); // MapActivityに遷移する
            }
        });
    }

    protected void onResume(){
        super.onResume();

        // Mapから観光地ID・環境データを取り出す
        final String spotId = (String) spotData.get("spot_id");
        final String environmentId = (String) spotData.get("environment_id");
        final String spotName = (String) spotData.get("spot_name");
        final Double latitude = (Double) spotData.get("latitude");
        final Double longitude = (Double) spotData.get("longitude");

        DataBaseHelper dataBaseHelper = new DataBaseHelper(this); // DataBaseHelperをインスタンス化

        // DataBaseHelperのgetEnvironmentDataから環境IDに対応した環境データセットを取得する
        Map<String, Object> environmentData = dataBaseHelper.getEnvironmentData(environmentId);

        // DataBaseHelperのgetSpotTextから観光地IDに対応した観光地案内テキストを取得する
        String spotText = dataBaseHelper.getSpotText(spotId);
        if (spotText == null) spotText = "データがありません。";

        // LocationAcquisitionのgetCurrentLocationを呼び出し、現在地の緯度・経度を取得する
        Double[] currentLocation = locationAcquisition.getCurrentLocation();

        // Mapから取り出した緯度・経度を配列に格納する
        Double[] spotLocation = {latitude, longitude};

        // LocationAcquisitionのgetDistanceから現在地～観光地の距離を取得する
        Double distance = locationAcquisition.getDistance(currentLocation, spotLocation);
        if (distance == null || distance.equals(NaN)) distance = 0.0;
        BigDecimal distanceBi = new BigDecimal(String.valueOf(distance));
        //小数第2位で四捨五入
        distance = distanceBi.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();

        // 取得したデータを引数として、displaySpotDetailを呼び出し、情報を画面に表示する
        displaySpotDetail(spotData, environmentData, spotText, distance);
    }

    // 引数の情報を画面に表示するメソッド
    private void displaySpotDetail (Map<String, Object> spotData, Map<String, Object> environmentData, String spotText, Double distance) {

        // spotDataから表示を行うデータを取得する
        String spotName = (String) spotData.get("spot_name"); // 観光地名
        if (spotName == null) spotName = "データがありません。";

        String streetAddress = (String) spotData.get("street_address"); // 住所
        if (streetAddress == null) streetAddress = "データがありません。";

        Integer postalCode = (Integer) spotData.get("postal_code"); // 郵便番号

        String weather = (String) environmentData.get("weather"); // 天気
        if (weather == null) weather = "不明";

        Double temperature = (Double) environmentData.get("temperature"); // 気温
        BigDecimal temperatureBi = new BigDecimal(String.valueOf(temperature));
        temperature = temperatureBi.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();        //小数第2位で四捨五入

        String sep = System.getProperty("line.separator");
        String spotIntroduce = spotText + sep + sep + "〒 " + postalCode + sep + streetAddress;


        // XMLとの対応付けを行う
        TextView spotNameText = (TextView)findViewById(R.id.spotNameTextView); // 観光地名
        TextView distanceText = (TextView)findViewById(R.id.distanceTextView); // 距離
        TextView weatherText = (TextView)findViewById(R.id.weatherTextView);  // 天気
        TextView temperatureText = (TextView)findViewById(R.id.temperatureTextView); // 気温
        TextView spotDetailText = (TextView)findViewById(R.id.spotDetailTextView); // 観光地案内テキスト
        ImageView spotImage = (ImageView) findViewById(R.id.spotImageView); // 観光地写真

        // 表示する内容をセットする
        spotNameText.setText(spotName); // 観光地名
        distanceText.setText("距離: " + String.valueOf(distance) + " km"); // 距離
        weatherText.setText("天気: "+ weather); // 天気
        temperatureText.setText("気温: " + String.valueOf(temperature) + " 度"); // 気温
        spotDetailText.setMovementMethod(ScrollingMovementMethod.getInstance()); // 観光地案内テキスト(スクロール)
        spotDetailText.setText(spotIntroduce); //　観光地案内テキスト

        // 写真の表示
        String photoFilePath = "noimage";
        if (spotData.get("photo_file_path") != null) photoFilePath = (String) spotData.get("photo_file_path");
        int imageId = getResources().getIdentifier(photoFilePath.toString(), "drawable", "jp.dmarch.kochitabi");
        if (imageId == 0) imageId = getResources().getIdentifier("noimage", "drawable", "jp.dmarch.kochitabi");
        spotImage.setImageResource(imageId); // 表示
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
