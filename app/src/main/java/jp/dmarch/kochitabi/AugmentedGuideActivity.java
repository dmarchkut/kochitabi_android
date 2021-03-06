package jp.dmarch.kochitabi;

import android.bluetooth.BluetoothAdapter;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;

import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.architect.ArchitectView;

import java.io.IOException;

public class AugmentedGuideActivity extends AppCompatActivity {
    private ArchitectView architectView;
    private TextView name;
    private TextView message;
    private String characterFilePath;
    private static boolean characterCondition;
    private int textNumber;
    private final int MAX_TEXT_SIZE = 120;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("AR案内");
        // xmlファイルと紐付け
        setContentView(R.layout.activity_augmentedguide);

        /* 前の画面(CameraActivity)からのデータを受け取る */
        Intent intent = getIntent();
        final String accessPointId = (String)intent.getStringExtra("access_point_id");
        final String characterName = (String)intent.getStringExtra("character_name");
        characterFilePath = (String)intent.getStringExtra("character_file_path");
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

        // 画面領域の設定
        LinearLayout screenArea = (LinearLayout)this.findViewById(R.id.screen_area);

        // 名前テキストと紐付け
        name = (TextView)this.findViewById(R.id.name);
        name.setText(characterName);

        // 案内テキストと紐付け
        message = (TextView)this.findViewById(R.id.message);
        // テキストを表示単位に分割
        final String[] cutTextData = cutSentence(textData);
        // 次のテキストの番号指定
        textNumber = 0;
        // 表示可能テキストの探索
        while(textNumber < cutTextData.length) {
            if (cutTextData[textNumber].length() < MAX_TEXT_SIZE) {
                break;
            }
            textNumber++;
        }
        // 最初の表示テキスト
        if(textNumber < cutTextData.length) {
            message.setText(cutTextData[textNumber] + "。");
            textNumber++;
        } else {
            name.setVisibility(View.GONE);
            message.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "データが登録されていません。他のアクセスポイントをご利用ください。", Toast.LENGTH_LONG).show();
        }
        // クリックイベントを有効にする
        screenArea.setClickable(true);
        screenArea.setOnClickListener(new View.OnClickListener() {
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
        if(textNumber < cutTextData.length) {
            // 最初に戻った時の処理(テキストの表示を行う)
            if(textNumber == 0) {
                name.setVisibility(View.VISIBLE);
                message.setVisibility(View.VISIBLE);
            }
            // 表示可能テキストの探索
            while(textNumber < cutTextData.length) {
                if (cutTextData[textNumber].length() < MAX_TEXT_SIZE) {
                    break;
                }
                textNumber++;
            }
            // テキストの表示を行う
            if (textNumber < cutTextData.length) {
                message.setText(cutTextData[textNumber] + "。");
                textNumber++;
            // テキストが終了したので最初に戻る
            } else {
                name.setVisibility(View.GONE);
                message.setVisibility(View.GONE);
                textNumber = 0;
            }
        // テキストが終了したので最初に戻る
        } else {
            name.setVisibility(View.GONE);
            message.setVisibility(View.GONE);
            textNumber = 0;
        }
    }

    /* AR案内画面から強制退場 */
    private void outScreen() {
        if(BluetoothAdapter.getDefaultAdapter().isDiscovering()) {
            Toast.makeText(getApplicationContext(), "アクセスポイントの外に出ました", Toast.LENGTH_LONG).show();
            finish();
        }
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
            } else {
                // ARキャラクターの描画処理
                getCharacter(characterFilePath.toString());
                // ARキャラクターが表示されている状態
                characterCondition = true;
            }
        }
    };

    /* ARキャラクターの追加を行う */
    private void getCharacter(String characterFilePath) {
        // ARキャラクターの描画処理
        try {
            this.architectView.load(WikitudeContentsFragment.getArchitectWorldPath(characterFilePath)); //AR表示
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /* ARキャラクターの削除を行う */
    private void resetCharacter() {
        // ARキャラクターの削除処理
        try {
            this.architectView.load(WikitudeContentsFragment.resetArchitectWorldPath()); //AR非表示
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    protected static boolean characterCondition() {
        return characterCondition;
    }

    @Override
    protected void onPostCreate( final Bundle savedInstanceState ) {
        super.onPostCreate(savedInstanceState);
        if ( this.architectView != null ) {
            // call mandatory live-cycle method of architectView
            this.architectView.onPostCreate();
            // AR表示のためにHTMLファイルを指定
            resetCharacter();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ( this.architectView != null ) {
            // onResumeメソッドでArchitectViewのonResumeメソッドを実行
            this.architectView.onResume();
        }
        // ARキャラクターが表示されていない状態
        characterCondition = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if ( this.architectView != null ) {
            // onPauseメソッドでArchitectViewのonPauseメソッドを実行
            this.architectView.onPause();
        }
        // ARキャラクターが表示されていない状態
        characterCondition = false;
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
