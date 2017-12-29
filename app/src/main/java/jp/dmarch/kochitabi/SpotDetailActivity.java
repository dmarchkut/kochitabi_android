package jp.dmarch.kochitabi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

public class SpotDetailActivity extends AppCompatActivity {

    private Map<String, Object> spotData;
    private LocationAcquisition locationAcquisition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotdetail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);      // バックボタン追加

        // 前画面(SpotActivity)からのspotDataを受け取る
        Intent intent = getIntent();

        spotData = new HashMap<String, Object>();

        //String spotId = intent.getStringExtra("spot_id");
        //String environmentId = intent.getStringExtra("environment id");
        String spotName = intent.getStringExtra("spot_name");
        //String spotPhoname = intent.getStringExtra("spot_phoname");
        //String streetAddress = intent.getStringExtra("street_address");
        //String postalCode = intent.getStringExtra("postal_code");
        //Double latitude = intent.getDoubleExtra("latitude", 0);
        //Double longitude = intent.getDoubleExtra("longitude", 0);
        String photoFilePath = intent.getStringExtra("photo_file_path");

        //spotData.put("spot_id", spotId);
        //spotData.put("environment id", environmentId);
        spotData.put("spot_name", spotName);
        //spotData.put("spot_phoname", spotPhoname);
        //spotData.put("street_address", streetAddress);
        //spotData.put("postal_code", postalCode);
        //spotData.put("latitude", latitude);
        //spotData.put("longitude", longitude);
        spotData.put("photo_file_path", photoFilePath);

        setTitle(spotName);

        ImageView imageView = findViewById(R.id.imageView);
        if (photoFilePath != null) {
            imageView.setImageResource(this.getResources().getIdentifier(spotData.get("photo_file_path").toString(), "drawable", "jp.dmarch.kochitabi"));
        }
        else {
            imageView.setImageResource(this.getResources().getIdentifier("noimage", "drawable", "jp.dmarch.kochitabi"));
        }

        Button button = findViewById(R.id.detail_map_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SpotDetailActivity.this, MapActivity.class);
                String spotName = spotData.get("spot_name").toString();
                intent.putExtra("spot_name", spotName);
                intent.putExtra("activity_name", "SpotDetailActivity");
                startActivity(intent);
            }
        });

    }

    // バックボタンタップ時処理
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
