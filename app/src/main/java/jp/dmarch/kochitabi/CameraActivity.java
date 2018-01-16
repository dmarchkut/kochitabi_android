package jp.dmarch.kochitabi;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.architect.ArchitectView.CaptureScreenCallback;

public class CameraActivity extends AppCompatActivity {
    private static CameraActivity instance;
    private ArchitectView architectView;
    private ImageButton cameraButton;
    private ImageButton arguideButton;
    private static final int PERMISSION_CAMERA = 1;
    private static final int PERMISSION_STORAGE = 2;

    /* 外部からcontextを参照するときに使う */
    protected static Context getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;

        // xmlファイルと紐付け
        setContentView(R.layout.activity_camera);
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
        cameraButton = (ImageButton)this.findViewById(R.id.snapshot_button);
        // カメラボタンを非表示にする
        cameraButton.setVisibility(View.GONE);
        // AR案内ボタンとの紐付け
        arguideButton = (ImageButton)this.findViewById(R.id.arguide_button);
        // AR案内ボタンを非表示にする
        arguideButton.setVisibility(View.GONE);

        // Android 6, API 23以上でパーミッシンの確認
        if(Build.VERSION.SDK_INT >= 23){
            // カメラが許可されていない
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                // カメラの使用許可を要求
                ActivityCompat.requestPermissions((Activity) this, new String[] {Manifest.permission.CAMERA,}, PERMISSION_CAMERA);
            } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                // ストレージの使用許可を要求
                ActivityCompat.requestPermissions((Activity) this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,}, PERMISSION_STORAGE);
            } else {
                //撮影ボタンを表示させる
                cameraButton.setVisibility(View.VISIBLE);
            }
        }

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

                // 画面のキャプチャを行う
                captureScreen(screenCaptureFile);
                // SavePhotoActivityに移動する
                Intent intent = new Intent(getApplication(), SavePhotoActivity.class);
                intent.putExtra("fileData", screenCaptureFile);
                startActivity(intent);
            }
        });
        // レシーバーの設定を行う
        setReceiver();
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

    /* パーミッションの処理 */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            // 先ほどの独自定義したrequestCodeの結果確認
            case PERMISSION_CAMERA: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // ユーザーがカメラの使用を許可したとき
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        // ストレージの使用許可を要求
                        ActivityCompat.requestPermissions((Activity) this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,}, PERMISSION_STORAGE);
                    }
                } else {
                    // ユーザーがカメラの使用を許可しなかったとき
                    finish();
                }
                return;
            } case PERMISSION_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // ユーザーがSTORAGEの使用を許可したとき
                    //撮影ボタンを表示させる
                    cameraButton.setVisibility(View.VISIBLE);
                }
                return;
            }
        }
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

    /* レシーバーの設定を行う */
    private void setReceiver() {
        AccessPointReceiver receiver = new AccessPointReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("UPDATE_ACTION");
        registerReceiver(receiver, intentFilter);
        receiver.registerHandler (updateHandler);
    }

    /* サービスから値を受け取ったら動かしたい内容を書く */
    private Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            // データを受け取る
            Bundle bundle = msg.getData();
            String raspberrypiNumber = bundle.getString("raspberrypiNumber");

            /* アクセスポイント内にいる時の処理 */
            if (raspberrypiNumber != null) {
                // raspberrpiNumberに対応したAR案内情報を取得する
                final Map<String, Object> characterGuideData = new DataBaseHelper().getCharacterGuide(raspberrypiNumber);

                // ARキャラクターの表示を行う
                WikitudeContentsFragment.setWikitudeContents(characterGuideData);

                // Mapオブジェクトを分解する
                final Object accessPointId = characterGuideData.get("access_point_id");
                final Object characterName = characterGuideData.get("character_name");
                final Object characterFilePath = characterGuideData.get("character_file_path");
                final Object textData = characterGuideData.get("text_data");

                //AR案内ボタンを表示させる
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
            /* アクセスポイント外にいる時の処理 */
            } else {
                // AR案内ボタンを非表示にする
                arguideButton.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if ( this.architectView != null ) {
            // call mandatory live-cycle method of architectView
            this.architectView.onPostCreate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.architectView != null) {
            // onResumeメソッドでArchitectViewのonResumeメソッドを実行
            this.architectView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.architectView != null) {
            // onPauseメソッドでArchitectViewのonPauseメソッドを実行
            this.architectView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // architectViewの必須のライフサイクルメソッドを呼び出す
        if (this.architectView != null) {
            // onDestroyメソッドでArchitectViewのonDestroyメソッドを実行
            this.architectView.onDestroy();
        }
    }

    @Override
    protected  void onStart() {
        super.onStart();
        // サービスクラスを開始する
        Intent intent = new Intent(getApplication(), AccessPointService.class);
        startService(intent);
    }

    @Override
    protected  void onStop() {
        super.onStop();
        // サービスクラスを終了する
        Intent intent = new Intent(getApplication(), AccessPointService.class);
        stopService(intent);
    }
}