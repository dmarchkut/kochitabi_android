package jp.dmarch.kochitabi;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Intent;
import android.widget.Toast;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;


public class SavePhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // xmlファイルと紐付け
        setContentView(R.layout.activity_savephoto);

        /* 前の画面(CameraActivity)からのデータを受け取る */
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final File screenCaptureFile = (File)bundle.get("bitmapData");
        // 写真ファイルパスを取得
        String filePath = screenCaptureFile.toString();

        // imageViewと紐付け
        ImageView imageView = findViewById(R.id.image_view);
        // プレビュの表示
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        imageView.setImageBitmap(bitmap);

        // キャンセルボタンと紐付け
        Button cancelButton = (Button)this.findViewById(R.id.cancel_button);
        // 保存ボタンと紐付け
        Button saveButton = (Button)this.findViewById(R.id.save_button);
        // 共有ボタンと紐付け
        Button shareButton = (Button)this.findViewById(R.id.share_button);

        /* キャンセルボタンがクリックされた時の処理 */
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePhoto();

            }
        });

        /* 保存ボタンがクリックされた時の処理 */
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /// 保存完了テキストの表示
                Toast.makeText(getApplicationContext(), "写真を保存しました", Toast.LENGTH_LONG).show();

            }
        });

        /* 共有ボタンがクリックされた時の処理 */
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharePhoto(screenCaptureFile);
            }
        });


    }


    private void deletePhoto() {

    }

    private void sharePhoto(File screenCaptureFile) {
        // 2. create send intent
        final Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpg");
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(screenCaptureFile));

        // 3. launch intent-chooser
        final String chooserTitle = "Share Snaphot";
        SavePhotoActivity.this.startActivity(Intent.createChooser(share, chooserTitle));
    }




}
