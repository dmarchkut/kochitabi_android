
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

public class HomeActivity extends AppCompatActivity {

    private ArrayList<Map<String, Object>> spotsData; // メンバ変数spotsDataのインスタンス化
    private DataBaseHelper dataBaseHelper; // DataBaseHelperクラスのインスタンス化

    private Map<String, Object> spotData; // 乱数で決定した観光地データを保管するメンバ変数(仕様変更)
    boolean dataFlag = true; // 全ての観光地のデータが取得できたかの判定
    boolean spotFlag = false; // ランダムに観光地を決定したか判定する判定


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setTitle("こちたびAR"); // ウインドウタイトルを「こちたびAR」に変更

        dataBaseHelper = new DataBaseHelper(); // DataBaseHelperのインスタンス化

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
        Button sendDetailButton = (Button) findViewById(R.id.spotDetailButton); // 観光地詳細ボタン
        sendDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // もし観光地が無かった場合遷移させない
                if (spotData.get("photo_file_path")==null || dataFlag == false) return;

                Intent intent = new Intent(getApplication(), SpotDetailActivity.class);

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
                startActivity(intent);
            }
        });
    }

    protected void onResume() {
        super.onResume();

        // DataBaseHelperのgetSpotsDataから全観光地のデータを取得
        spotsData = dataBaseHelper.getSpotsData();
        if (spotsData == null) dataFlag = false;

        // スライドショーに表示される写真(観光地)を決定するメソッド
        if (dataFlag == true && spotFlag == false) decideSlideshowPhoto(spotsData);
        displaySlideshowPhoto(); // 写真スライドショーを表示するメソッド
    }

    // 画面上部に表示する観光地を決定するメソッド
    private void decideSlideshowPhoto(ArrayList<Map<String, Object>> spotsData) {

        int listSize = spotsData.size(); // ArrayListのサイズを取得
        Random random = new Random(); // Randomのインスタンス化
        int listNumber = random.nextInt(listSize); // 観光地の中から1つをランダムで選出
        spotFlag = true; // 真偽値を更新し、タスクキルするまで観光地を選ばないようにする
        spotData = spotsData.get(listNumber); // 要素を取得
    }

    // 画面上部に決定された観光地の情報を表示するメソッド
    private void displaySlideshowPhoto() {

        String spotName = "";
        String photoFilePath = "noimage";

        if (dataFlag == true) {
            spotName = spotData.get("spot_name").toString();
            if (spotData.get("photo_file_path") != null) {
                photoFilePath = spotData.get("photo_file_path").toString();
            }
        }

        TextView spotNameText = (TextView)findViewById(R.id.spotNameTextView); // 観光地名
        ImageView spotImage = (ImageView)findViewById(R.id.spotImageView); // 観光地写真

        spotNameText.setText(spotName); // 観光地名の表示

        // 写真の表示
        int imageId = getResources().getIdentifier(photoFilePath.toString(), "drawable", "jp.dmarch.kochitabi");
        spotImage.setImageResource(imageId); // 表示
    }
}
