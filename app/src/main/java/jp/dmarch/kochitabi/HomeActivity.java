package jp.dmarch.kochitabi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class HomeActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button sendButton = findViewById(R.id.button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), SpotDetailActivity.class);

                // キーの値を全てMapから取り出す
                String spotId = "aaa";
                String environmentId = "bbb";
                String spotName = "ソフ工の理想郷";
                String spotPhoname = "ソフコウオブアヴァロン";
                String streetAddress = "かなしみ";
                Integer postalCode = 100000;
                Double latitude = 100.0;
                Double longitude = 100.0;
                String photoFilePath = "dadaima";

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

                startActivity(intent);
            }
        });
    }


}
