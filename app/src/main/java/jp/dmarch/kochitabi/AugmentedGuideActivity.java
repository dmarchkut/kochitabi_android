package jp.dmarch.kochitabi;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.TextView;

import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.architect.ArchitectView;

public class AugmentedGuideActivity extends AppCompatActivity {
    private ArchitectView architectView;
    private TextView name;
    private TextView message;
    private int textNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // xmlファイルと紐付け
        setContentView(R.layout.activity_augmentedguide);

        /* 前の画面(CameraActivity)からのデータを受け取る */
        Intent intent = getIntent();
        final String accessPointId = (String)intent.getStringExtra("access_point_id");
        final String characterName = (String)intent.getStringExtra("character_name");
        final String characterFilePath = (String)intent.getStringExtra("character_file_path");
        final String textData = (String)intent.getStringExtra("text_data");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // バックボタンを追加

        // ArchitectViewと紐付け
        this.architectView = (ArchitectView)this.findViewById(R.id.architectView);
        final ArchitectStartupConfiguration config = new ArchitectStartupConfiguration();
        config.setLicenseKey(WikitudeContentsFragment.getWikitudeSDKLicenseKey());  // ライセンスキーの読み込み
        config.setCameraPosition(WikitudeContentsFragment.getCameraPosition());     // バックカメラを使用する
        try {
            // ArchitectViewのonCreateメソッドを実行
            this.architectView.onCreate(config);
        } catch (RuntimeException ex) {
            this.architectView = null;
            // エラー文を表示
            Toast.makeText(getApplicationContext(), "ArchitectViewを作成できません", Toast.LENGTH_SHORT).show();
        }

        // カメラボタンと紐付け
        ImageButton cameraButton = (ImageButton)this.findViewById(R.id.camera_button);
        /* カメラボタンがクリックされた時の処理 */
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 名前テキストと紐付け
        name = (TextView)this.findViewById(R.id.name);
        name.setText(characterName);

        // 案内テキストと紐付け
        message = (TextView)this.findViewById(R.id.message);
        // テキストを表示単位に分割
        final String[] cutTextData = cutSentence(textData);
        // 最初の表示テキスト
        message.setText(cutTextData[0]);
        // 次のテキストの番号指定
        textNumber = 1;
        // クリックイベントを有効にする
        architectView.setClickable(true);
        architectView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateSentence(cutTextData);
            }
        });

        // レシーバーの設定を行う
        setReceiver();
    }

    /* バックボタンタップ時処理 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // ホーム画面に戻る
        Intent intent = new Intent(getApplication(), HomeActivity.class); // 切り替え準備
        // 呼び出すActivity以外のActivityをクリアして起動させる
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent); // Activity切り替え
        return true;
    }

    private String[] cutSentence(String textData) {
        String[] cutTextData = textData.split("。", 0);
        return cutTextData;
    }

    private void updateSentence(String[] cutTextData) {
        if(textNumber == 0) {
            name.setVisibility(View.VISIBLE);
            message.setVisibility(View.VISIBLE);
            message.setText(cutTextData[textNumber]);
            textNumber++;
        } else if(textNumber < cutTextData.length) {
            message.setText(cutTextData[textNumber]);
            textNumber++;
        } else if(textNumber == cutTextData.length) {
            name.setVisibility(View.GONE);
            message.setVisibility(View.GONE);
            textNumber = 0;
        }
    }

    /* AR案内画面から強制退場 */
    private void outScreen() {
        Toast.makeText(getApplicationContext(), "アクセスポイントの外に出ました", Toast.LENGTH_LONG).show();
        finish();
    }

    /* レシーバーの設定を行う */
    private void setReceiver() {
        AccessPointReceiver receiver = new AccessPointReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("UPDATE_ACTION");
        registerReceiver(receiver, intentFilter);
        receiver.registerHandler (updateHandler);
    }

    /* サービスから値を受け取った時の動作 */
    private Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            // データを受け取る
            Bundle bundle = msg.getData();
            String raspberrypiNumber = bundle.getString("raspberrypiNumber");

            /* アクセスポイント外に出た時の処理 */
            if (raspberrypiNumber == null) {
                outScreen();
            }
        }
    };

    @Override
    protected void onPostCreate( final Bundle savedInstanceState ) {
        super.onPostCreate(savedInstanceState);
        if ( this.architectView != null ) {
            // call mandatory live-cycle method of architectView
            this.architectView.onPostCreate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ( this.architectView != null ) {
            // onResumeメソッドでArchitectViewのonResumeメソッドを実行
            this.architectView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if ( this.architectView != null ) {
            // onPauseメソッドでArchitectViewのonPauseメソッドを実行
            this.architectView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // architectViewの必須のライフサイクルメソッドを呼び出す
        if ( this.architectView != null ) {
            // onDestroyメソッドでArchitectViewのonDestroyメソッドを実行
            this.architectView.onDestroy();
        }
    }
}
