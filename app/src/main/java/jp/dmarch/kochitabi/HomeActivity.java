package jp.dmarch.kochitabi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<Map<String, Object>> spotsData; // メンバ変数spotsDataのインスタンス化
    private DataBaseHelper dataBaseHelper; // DataBaseHelperクラスのインスタンス化

    private Map<String, Object> spotData; // 乱数で決定した観光地データを保管するメンバ変数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setTitle("こちたびAR"); // ウインドウタイトルを「こちたびAR」に変更

        dataBaseHelper = new DataBaseHelper(); // DataBaseHelperのインスタンス化

        // ボタンの設定
        ImageButton sendSpotButton = (ImageButton) findViewById(R.id.imageSpotButton); // 観光地一覧ボタン
        ImageButton sendCameraButton = (ImageButton) findViewById(R.id.imageCameraButton); // ARカメラボタン
        ImageButton sendMapButton = (ImageButton) findViewById(R.id.imageMapButton); // マップボタン
        Button sendDetailButton = (Button) findViewById(R.id.spotDetailButton); // 観光地詳細ボタン

        sendSpotButton.setOnClickListener(this); // 観光地一覧ボタン
        sendCameraButton.setOnClickListener(this); // ARカメラボタン
        sendMapButton.setOnClickListener(this); // マップボタン
        sendDetailButton.setOnClickListener(this); // 観光地詳細ボタン
    }

    // メニューボタンが押された際の処理
    public void onClick(View v) {

        Intent intent = null; // 初期値

        switch (v.getId()) {
            case R.id.imageSpotButton: // SpotActivityに遷移
                intent = new Intent(getApplication(), SpotActivity.class);
                break;

            case R.id.imageCameraButton: // ARカメラに遷移
                intent = new Intent(getApplication(), CameraActivity.class);
                break;

            case R.id.imageMapButton: // MapActivityに遷移
                intent = new Intent(getApplication(), MapActivity.class);
                final String activityName = "HomeActivity"; // MapActivityでの遷移判断に使用する変数
                intent.putExtra("activity_name", activityName);
                break;

            case R.id.spotDetailButton: // SpotDetailActivityに遷移

                // 写真ファイルパスだけ先に取り出す
                final String photoFilePath = spotData.get("photo_file_path").toString();
                if (photoFilePath == null)  return ; // noimageの時に遷移させないようにする

                intent = new Intent(getApplication(), SpotDetailActivity.class);

                // キーの値を全てMapから取り出す
                final String spotId = spotData.get("spot_id").toString();
                final String environmentId = spotData.get("environment id").toString();
                final String spotName = spotData.get("spot_name").toString();
                final String spotPhoname = spotData.get("spot_phoname").toString();
                final String streetAddress = spotData.get("street_address").toString();
                final Integer postalCode = new Integer(spotData.get("postal_code").toString()).intValue();
                final Double latitude = new Double(spotData.get("latitude").toString()).doubleValue();
                final Double longitude = new Double(spotData.get("longitude").toString()).doubleValue();

                // SpotDetailActivityに渡す値を付与する
                intent.putExtra("spot_id", spotId);
                intent.putExtra("environment id", environmentId);
                intent.putExtra("spot_name", spotName);
                intent.putExtra("spot_phoname", spotPhoname);
                intent.putExtra("street_address", streetAddress);
                intent.putExtra("postal_code", postalCode);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("photo_file_path", photoFilePath);

                break;
        }
        
        startActivity(intent);
    }

    protected void onResume() {
        super.onResume();

        // DataBaseHelperのgetSpotsDataから全観光地のデータを取得
        spotsData = dataBaseHelper.getSpotsData();

        // スライドショーに表示される写真(観光地)を決定するメソッド
        decideSlideshowPhoto(spotsData);

        displaySlideshowPhoto(); // 写真スライドショーを表示するメソッド
    }

    private void decideSlideshowPhoto(ArrayList<Map<String, Object>> spotsData) {

        int listSize = spotsData.size(); // ArrayListのサイズを取得

        Random random = new Random(); // Randomのインスタンス化
        int listNumber = random.nextInt(listSize); // 観光地の中から1つをランダムで選出

        spotData = spotsData.get(listNumber); // 要素を取得
    }

    private void displaySlideshowPhoto() {

        // 表示する情報をspotDataから取り出す
        String spotName = spotData.get("spot_name").toString();
        String photoFilePath = spotData.get("photo_file_path").toString();
        if (photoFilePath == null) photoFilePath = "noimage"; // 写真が無い場合

        TextView spotNameText = (TextView)findViewById(R.id.spotNameTextView); // 観光地名
        ImageView spotImage = findViewById(R.id.spotImageView); // 観光地写真

        spotNameText.setText(spotName); // 観光地名の表示

        // 写真の表示
        int imageId = getResources().getIdentifier(photoFilePath.toString(), "drawable", "jp.dmarch.kochitabi"); // リソースのIDをdrawableから取得
        spotImage.setImageResource(imageId); // 表示
    }

}
