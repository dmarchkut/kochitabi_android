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
        setContentView(R.layout.activity_camera);

        this.architectView = (ArchitectView)this.findViewById( R.id.architectView );
        final ArchitectStartupConfiguration config = new ArchitectStartupConfiguration();
        // ライセンスキーの読み込み
        config.setLicenseKey(TestWikitude.getWikitudeSDKLicenseKey());
        // バックカメラを使用する
        config.setCameraPosition(TestWikitude.getCameraPosition());
        try {
            // ArchitectViewのonCreateメソッドを実行
            this.architectView.onCreate( config );
        } catch (RuntimeException ex)
        {
            this.architectView = null;
            Toast.makeText(getApplicationContext(), "can't create Architect View", Toast.LENGTH_SHORT).show();
        }

        Button cameraButton = findViewById(R.id.camera_button);

        if (savedInstanceState != null){
            cameraUri = savedInstanceState.getParcelable("CaptureUri");
        }

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //camera.takePicture(null, null, new TakePictureCallback());
                CaptureScreen();
            }
        });
    }


    void CaptureScreen() {
        if ("button".equalsIgnoreCase(cameraUri.getHost())) {
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
                                Toast.makeText(CameraActivity.this, "Unexpected error, " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });
        }
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