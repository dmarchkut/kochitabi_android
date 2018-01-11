package jp.dmarch.kochitabi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by monki on 2018/01/11.
 */

public class TestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // ディレクトリ作成
        File fileName = new File(Environment.getExternalStorageDirectory(), "kochitabiAR");
        if(!fileName.exists()) fileName.mkdir();

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestActivity.this, SavePhotoActivity.class);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test);
                File dir = new File(Environment.getExternalStorageDirectory().getPath()+"/test/");
                if (!dir.exists()) dir.mkdir();
                File file = new File(dir, "test.jpg");
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                intent.putExtra("fileData", file);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        File fileName = new File(Environment.getExternalStorageDirectory(), "kochitabiAR/test.jpg");
        Log.d("test4After", String.valueOf(fileName.exists()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // ディレクトリ削除
        File fileName = new File(Environment.getExternalStorageDirectory(), "kochitabiAR");
        for (File child:fileName.listFiles()) child.delete();
        if(fileName.exists()) fileName.delete();

    }
}
