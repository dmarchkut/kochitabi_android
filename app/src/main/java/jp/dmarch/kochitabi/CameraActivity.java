package jp.dmarch.kochitabi;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.architect.ArchitectView.CaptureScreenCallback;

public class CameraActivity extends AppCompatActivity {
    private ArchitectView architectView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // xmlファイルと紐付け
        setContentView(R.layout.activity_camera);

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
        Button cameraButton = (Button)this.findViewById(R.id.camera_button);
        // AR案内ボタンとの紐付け
        Button arguideButton = (Button)this.findViewById(R.id.arguide_button);
        arguideButton.setVisibility(View.GONE);

        /* カメラボタンがクリックされた時の処理 */
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 「kochitabiAR」って名前のディレクトリを作成
                File fileName = new File(Environment.getExternalStorageDirectory(), "kochitabiAR");
                if(!fileName.exists()) {
                    fileName.mkdir();
                }
                // 作成したkochitabiARの中に「kochitabiAR_時間.jpg」って名前の写真を格納する
                final File screenCaptureFile = new File(fileName, "kochitabiAR_" + System.currentTimeMillis() + ".jpg");

                captureScreen(screenCaptureFile);   // 画面のキャプチャを行う
                // SavePhotoActivityに移動する
                Intent intent = new Intent(getApplication(), SavePhotoActivity.class);
                intent.putExtra("bitmapData", screenCaptureFile);
                startActivity(intent);
            }
        });

        // アクセスポイント内：raspberrypiNumber、外：nullを受け取る
        String raspberrypiNumber = BluetoothAcqisition.checkAccessPoint();

        if (raspberrypiNumber != null) {
            // raspberrpiNumberに対応したAR案内情報を取得する
            Map<String, Object> characterGuideData = DataBaseHelper.getCharacterGuide(raspberrypiNumber);

            // ARキャラクターの表示を行う
            WikitudeContentsFragment.setWikitudeContents(characterGuideData);

            // Mapオブジェクトを分解する
            final Object accessPointId = characterGuideData.get("access_point_id");
            final Object characterName = characterGuideData.get("character_name");
            final Object characterFilePath = characterGuideData.get("character_file_path");
            final Object textData = characterGuideData.get("text_data");

            arguideButton.setVisibility(View.VISIBLE);
            /* AR案内ボタンがクリックされた時の処理 */
            arguideButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // AugmentedGuideActivityに移動する
                    Intent intent = new Intent(getApplication(), AugmentedGuideActivity.class);
                    intent.putExtra("access_point_id", accessPointId.toString());
                    intent.putExtra("character_name", characterName.toString());
                    intent.putExtra("character_file_path", characterFilePath.toString());
                    intent.putExtra("text_data", textData.toString());
                    startActivity(intent);
                }
            });
        }
    }

    /* 画面のキャプチャを行うメソッド */
    private void captureScreen(final File screenCaptureFile) {
        CameraActivity.this.architectView.captureScreen(ArchitectView.CaptureScreenCallback.CAPTURE_MODE_CAM_AND_WEBVIEW, new CaptureScreenCallback() {
            @Override
            public void onScreenCaptured(final Bitmap screenCapture) {

                /* 写真の保存処理 */
                try {
                    // 写真の保存処理を開始する
                    final FileOutputStream output = new FileOutputStream(screenCaptureFile);
                    // JPEG形式度保存する
                    screenCapture.compress(Bitmap.CompressFormat.JPEG, 90, output);
                    // 保存処理終了
                    output.flush();
                    output.close();

                /* 全てのアクセス許可が設定されているときは実行されない部分 */
                } catch (final Exception e) {
                    CameraActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 誤りがある場合にエラー文を表示する
                            Toast.makeText(CameraActivity.this, "予期せぬエラーが発生しました" + e, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

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