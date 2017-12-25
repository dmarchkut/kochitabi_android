package jp.dmarch.kochitabi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.net.Uri;

import java.io.File;

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
        final String filePath = screenCaptureFile.toString();

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
                // 写真の削除を行う
                screenCaptureFile.delete();
                // 画面遷移の処理
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);  // 切り替え準備
                startActivity(intent);                                                      // Activity切り替え
            }
        });

        /* 保存ボタンがクリックされた時の処理 */
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /// 保存完了テキストの表示
                Toast.makeText(getApplicationContext(), "写真を保存しました", Toast.LENGTH_LONG).show();
                // 写真をギャラリーに保存する
                addImageToGallery(filePath);
                // 画面遷移の処理
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);  // 切り替え準備
                startActivity(intent);
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

    /* 写真共有を行うためのメソッド */
    private void sharePhoto(File screenCaptureFile) {
        // 共有するものを設定する
        final Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpg");
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(screenCaptureFile));

        // 写真共有画面を起動する
        final String chooserTitle = "写真共有";
        SavePhotoActivity.this.startActivity(Intent.createChooser(share, chooserTitle));
    }

    /* 写真をギャラリーに保存するためのメソッド */
    private void addImageToGallery(String filePath) {
        try {
            ContentValues values = new ContentValues();

            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.MediaColumns.DATA, filePath);

            getApplicationContext().getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
