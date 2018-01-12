package jp.dmarch.kochitabi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        // activity_mainで用意したボタンの使用（Listenerの設定）
        Button mapBtn = (Button) findViewById(R.id.camera_button);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // クリック時の処理
                Intent intent = new Intent(getApplication(), CameraActivity.class); // 切り替え準備
                startActivity(intent); // Activity切り替え
            }
        });
    }
}
