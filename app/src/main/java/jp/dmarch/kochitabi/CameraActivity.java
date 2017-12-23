package jp.dmarch.kochitabi;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.net.Uri;
import android.graphics.Bitmap;
import android.content.Intent;



import java.io.File;
import java.io.FileOutputStream;

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
        this.architectView = (ArchitectView)this.findViewById( R.id.architectView );
        final ArchitectStartupConfiguration config = new ArchitectStartupConfiguration();
        config.setLicenseKey(TestWikitude.getWikitudeSDKLicenseKey());  // ライセンスキーの読み込み
        config.setCameraPosition(TestWikitude.getCameraPosition());     // バックカメラを使用する
        try {
            // ArchitectViewのonCreateメソッドを実行
            this.architectView.onCreate( config );
        } catch (RuntimeException ex) {
            this.architectView = null;
            // エラー文を表示
            Toast.makeText(getApplicationContext(), "ArchitectViewを作成できません", Toast.LENGTH_SHORT).show();
        }

        // カメラボタンと紐付け
        Button cameraButton = findViewById(R.id.camera_button);
        /* カメラボタンがクリックされた時の処理 */
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureScreen();
            }
        });
    }


    private void captureScreen() {
        CameraActivity.this.architectView.captureScreen(ArchitectView.CaptureScreenCallback.CAPTURE_MODE_CAM_AND_WEBVIEW, new CaptureScreenCallback() {
            @Override
            public void onScreenCaptured(final Bitmap screenCapture) {
                // 「kochitabiAR」って名前のディレクトリを作成
                File fileName = new File(Environment.getExternalStorageDirectory(), "kochitabiAR");
                if(!fileName.exists()) {
                    fileName.mkdir();
                }
                // 作成したkochitabiARの中に「kochitabiAR_時間.jpg」って名前の写真を格納する
                final File screenCaptureFile = new File(fileName, "kochitabiAR_" + System.currentTimeMillis() + ".jpg");

                /* 写真の保存処理 */
                try {
                    // 写真の保存処理を開始する
                    final FileOutputStream out = new FileOutputStream(screenCaptureFile);
                    // JPEG形式度保存する
                    screenCapture.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    // 保存完了テキストの表示
                    Toast.makeText(getApplicationContext(), "写真を保存しました", Toast.LENGTH_LONG).show();
                    // 保存処理終了
                    out.flush();
                    out.close();


/*
                    // 2. create send intent
                    final Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/jpg");
                    share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(screenCaptureFile));

                    // 3. launch intent-chooser
                    final String chooserTitle = "Share Snaphot";
                    CameraActivity.this.startActivity(Intent.createChooser(share, chooserTitle));
*/
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