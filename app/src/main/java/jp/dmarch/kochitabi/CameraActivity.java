package jp.dmarch.kochitabi;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.ArchitectStartupConfiguration;

import com.wikitude.architect.ArchitectView.ArchitectUrlListener;
import com.wikitude.architect.ArchitectView.CaptureScreenCallback;
import android.net.Uri;
import android.graphics.Bitmap;
import android.content.Intent;



public class CameraActivity extends AppCompatActivity {
    private ArchitectView architectView;
    private Uri cameraUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // xmlファイルと紐付け
        setContentView(R.layout.activity_camera);
        // ArchitectViewと紐付け
        this.architectView = (ArchitectView)this.findViewById( R.id.architectView );
        final ArchitectStartupConfiguration config = new ArchitectStartupConfiguration();
        // ライセンスキーの読み込み
        config.setLicenseKey(TestWikitude.getWikitudeSDKLicenseKey());
        // バックカメラを使用する
        config.setCameraPosition(TestWikitude.getCameraPosition());
        try {
            // ArchitectViewのonCreateメソッドを実行
            this.architectView.onCreate( config );
        } catch (RuntimeException ex) {
            this.architectView = null;
            // エラー文を表示
            Toast.makeText(getApplicationContext(), "ArchitectViewを作成できません", Toast.LENGTH_SHORT).show();
        }

        if (savedInstanceState != null){
            cameraUri = savedInstanceState.getParcelable("CaptureUri");
        }

        // カメラボタンと紐付け
        Button cameraButton = findViewById(R.id.camera_button);
        // カメラボタンがクリックされた時の処理
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //camera.takePicture(null, null, new TakePictureCallback());
                captureScreen();
            }
        });
    }


    private void captureScreen() {
        CameraActivity.this.architectView.captureScreen(ArchitectView.CaptureScreenCallback.CAPTURE_MODE_CAM_AND_WEBVIEW, new CaptureScreenCallback() {

            @Override
            public void onScreenCaptured(final Bitmap screenCapture) {
                // store screenCapture into external cache directory
                final File screenCaptureFile = new File(Environment.getExternalStorageDirectory().toString(), "screenCapture_" + System.currentTimeMillis() + ".jpg");

                // 1. Save bitmap to file & compress to jpeg. You may use PNG too
                try {
                    final FileOutputStream out = new FileOutputStream(screenCaptureFile);
                    screenCapture.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();

                    // 2. create send intent
                    final Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/jpg");
                    share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(screenCaptureFile));

                    // 3. launch intent-chooser
                    final String chooserTitle = "Share Snaphot";
                    CameraActivity.this.startActivity(Intent.createChooser(share, chooserTitle));

                } catch (final Exception e) {
                    // should not occur when all permissions are set
                    CameraActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // show toast message in case something went wrong
                            Toast.makeText(CameraActivity.this, "Unexpected error, " + e, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }


/*
    class TakePictureCallback implements Camera.PictureCallback {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                File dir = new File(
                        Environment.getExternalStorageDirectory(), "Camera");
                if(!dir.exists()) {
                    dir.mkdir();
                }
                File f = new File(dir, "img.jpg");
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(data);
                Toast.makeText(getApplicationContext(),
                        "写真を保存しました", Toast.LENGTH_LONG).show();
                fos.close();
                camera.startPreview();
            } catch (Exception e) { }
        }
    }
*/
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