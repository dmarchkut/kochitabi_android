
package jp.dmarch.kochitabi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class HomeActivity extends AppCompatActivity {

    private ArrayList<Map<String, Object>> spotsData; // メンバ変数spotsDataのインスタンス化
    private DataBaseHelper dataBaseHelper; // DataBaseHelperクラスのインスタンス化

    private Map<String, Object> spotData; // 乱数で決定した観光地データを保管するメンバ変数(仕様変更)
    private ImageView spotImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setTitle("こちたびAR"); // ウインドウタイトルを「こちたびAR」に変更

        dataBaseHelper = new DataBaseHelper(this); // DataBaseHelperのインスタンス化
        dataBaseHelper.setRegisterData();

        // 観光地一覧(SpotActivity)に飛ぶボタンの設定
        ImageButton sendSpotButton = (ImageButton) findViewById(R.id.imageSpotButton); // 観光地一覧ボタン
        sendSpotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), SpotActivity.class); // 遷移先のSpotActivityを設定する
                startActivity(intent);
            }
        });

        // カメラ(CameraActivity)に飛ぶボタンの設定
        ImageButton sendCameraButton = (ImageButton) findViewById(R.id.imageCameraButton); // ARカメラボタン
        sendCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), CameraActivity.class); // 遷移先のCameraActivityを設定する
                startActivity(intent);
            }
        });

        // マップ(MapActivity)に飛ぶボタンの設定
        ImageButton sendMapButton = (ImageButton) findViewById(R.id.imageMapButton); // マップボタン
        sendMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), MapActivity.class); // 遷移先のMapActivityを設定する
                final String activityName = "HomeActivity"; // MapActivityでの遷移判断に使用する変数
                intent.putExtra("activity_name", activityName);
                startActivity(intent);
            }
        });

        // 画面上部の観光地の写真をタップした際に観光地詳細(SpotDetailActivity)に飛ぶボタン
        spotImage = (ImageView)findViewById(R.id.spotImageView);
        spotImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // もし観光地が無かった場合遷移させない
                if (spotsData == null) return;

                Intent intent = new Intent(getApplication(), SpotDetailActivity.class);

                // キーの値を全てMapから取り出す
                final String spotId = (String) spotData.get("spot_id");
                final String environmentId = (String) spotData.get("environment_id");
                final String spotName = (String) spotData.get("spot_name");
                final String spotPhoname = (String) spotData.get("spot_phoname");
                final String streetAddress = (String) spotData.get("street_address");
                final Integer postalCode = (Integer) spotData.get("postal_code");
                final Double latitude = (Double) spotData.get("latitude");
                final Double longitude = (Double) spotData.get("longitude");
                final String photoFilePath = (String) spotData.get("photo_file_path");

                // SpotDetailActivityに渡す値を付与する
                intent.putExtra("spot_id", spotId);
                intent.putExtra("environment_id", environmentId);
                intent.putExtra("spot_name", spotName);
                intent.putExtra("spot_phoname", spotPhoname);
                intent.putExtra("street_address", streetAddress);
                intent.putExtra("postal_code", postalCode);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("photo_file_path", photoFilePath);
                startActivity(intent);
            }
        });
    }

    protected void onResume() {
        super.onResume();

        // DataBaseHelperのgetSpotsDataから全観光地のデータを取得
        spotsData = dataBaseHelper.getSpotsData();

        // スライドショーに表示される写真(観光地)を決定するメソッド
        if (spotsData != null) decideSlideshowPhoto(spotsData);
        displaySlideshowPhoto(); // 写真スライドショーを表示するメソッド
    }

    // 画面上部に表示する観光地を決定するメソッド
    private void decideSlideshowPhoto(ArrayList<Map<String, Object>> spotsData) {

        // ArrayListの整形
        ArrayList<Map<String, Object>> reSpotsData = new ArrayList<Map<String, Object>>();

        // 観光地の名前が入っていなかった場合の処理
        int insert = 0;
        for (int size = 0; size < spotsData.size(); size++) {
            if ((spotsData.get(size)).get("spot_name") != null) {
                reSpotsData.add(spotsData.get(size));
                insert++;
            }
        }

        int listSize = reSpotsData.size(); // ArrayListのサイズを取得
        Random random = new Random(); // Randomのインスタンス
        int listNumber = random.nextInt(listSize); // 観光地の中から1つをランダムで選出
        spotData = reSpotsData.get(listNumber); // 要素を取得
    }

    // 画面上部に決定された観光地の情報を表示するメソッド
    private void displaySlideshowPhoto() {

        String spotName = "";
        String photoFilePath = "noimage";

        if (spotsData != null) {
            spotName = (String) spotData.get("spot_name");
            if (spotData.get("photo_file_path") != null) {
                photoFilePath = (String) spotData.get("photo_file_path");
            }
        }

        TextView spotNameText = (TextView)findViewById(R.id.spotNameTextView); // 観光地名
        spotNameText.setText(spotName); // 観光地名の表示

        // 写真の表示
        int imageId = getResources().getIdentifier(photoFilePath.toString(), "drawable", "jp.dmarch.kochitabi");
        if (imageId == 0) imageId = getResources().getIdentifier("noimage", "drawable", "jp.dmarch.kochitabi");
        spotImage.setImageResource(imageId); // 表示
    }

}