package jp.dmarch.kochitabi;

        import android.hardware.SensorManager;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.os.Environment;
        import android.support.v7.app.AppCompatActivity;
        import android.view.View;
        import android.view.MenuItem;
        import android.widget.Button;
        import android.widget.Toast;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.graphics.Bitmap;

        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.util.Map;

        import com.wikitude.architect.ArchitectView;
        import com.wikitude.architect.ArchitectStartupConfiguration;
        import com.wikitude.architect.ArchitectView.CaptureScreenCallback;


        import android.location.Location;
        import android.location.LocationListener;
        //import com.wikitude.architect.ArchitectView;
        import com.wikitude.architect.ArchitectView.ArchitectUrlListener;
        import com.wikitude.architect.ArchitectView.SensorAccuracyChangeListener;
        //import com.wikitude.architect.StartupConfiguration;
        //import com.wikitude.architect.StartupConfiguration.CameraPosition;

        import android.webkit.WebView;

        import org.json.JSONException;
        import org.json.JSONObject;


public class CameraActivity extends AppCompatActivity implements ArchitectViewHolderInterface {
    private ArchitectView architectView;
    private Button arguideButton;
    private BluetoothAcqisition bluetoothAcqisition = new BluetoothAcqisition(this);

    //protected ArchitectView					 architectView;
    protected SensorAccuracyChangeListener      sensorAccuracyListener;
    protected Location 						 lastKnownLocaton;
    protected ArchitectViewHolderInterface.ILocationProvider locationProvider;
    protected LocationListener 				 locationListener;

    String a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // xmlファイルと紐付け
        setContentView(R.layout.activity_camera);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true); // バックボタンを追加

        WebView webview = new WebView(this);
        webview.addJavascriptInterface(this,"javaToJavaScript");



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

        this.sensorAccuracyListener = this.getSensorAccuracyListener();
        this.locationListener = new LocationListener() {

            @Override
            public void onStatusChanged( String provider, int status, Bundle extras ) {
            }

            @Override
            public void onProviderEnabled( String provider ) {
            }

            @Override
            public void onProviderDisabled( String provider ) {
            }

            @Override
            public void onLocationChanged( final Location location ) {
                // forward location updates fired by LocationProvider to architectView, you can set lat/lon from any location-strategy
                if (location!=null) {
                    // sore last location as member, in case it is needed somewhere (in e.g. your adjusted project)
                    CameraActivity.this.lastKnownLocaton = location;
                    if ( CameraActivity.this.architectView != null ) {
                        // check if location has altitude at certain accuracy level & call right architect method (the one with altitude information)
                        if ( location.hasAltitude() && location.hasAccuracy() && location.getAccuracy()<7) {
                            CameraActivity.this.architectView.setLocation( location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getAccuracy() );
                        } else {
                            CameraActivity.this.architectView.setLocation( location.getLatitude(), location.getLongitude(), location.hasAccuracy() ? location.getAccuracy() : 1000 );
                        }
                    }
                }
            }
        };
        this.locationProvider = getLocationProvider(this.locationListener);

        // カメラボタンと紐付け
        Button cameraButton = (Button)this.findViewById(R.id.camera_button);
        // AR案内ボタンとの紐付け
        arguideButton = (Button)this.findViewById(R.id.arguide_button);
        // AR案内ボタンを非表示にする
        arguideButton.setVisibility(View.GONE);

        /* カメラボタンがクリックされた時の処理 */
        /*cameraButton.setOnClickListener(new View.OnClickListener() {
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
        });*/
        //bluetoothAcqisition.beginSearchDevice();
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

                a = characterFilePath.toString();



                //AR案内ボタンを表示させる
                arguideButton.setVisibility(View.VISIBLE);
                /* AR案内ボタンがクリックされた時の処理 */
                arguideButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // AugmentedGuideActivityに移動する
                        Intent intent = new Intent(getApplication(), AugmentedGuideActivity.class);
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

    /*
    public JSONObject setFilePath() {
        //public String javaToJavascript() {
        //JSONObject toJavascript = new JSONObject();
        try {
            //JSONObject jsonObject = new JSONObject(json);
            //System.out.println(jsonObject.getString("screen_name"));
            JSONObject toJavascript = new JSONObject();
            toJavascript.put("a", "a");
            return toJavascript;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
    */

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if ( this.architectView != null ) {
            // call mandatory live-cycle method of architectView
            this.architectView.onPostCreate();

            try {
                this.architectView.load(WikitudeContentsFragment.getArchitectWorldPath());
                //this.architectView.setCullingDistance(50 * 1000);

/*
                //途中で変わるか
                for (int i=0; i<=900000000; i++) {
                }

                for (int i=0; i<=900000000; i++) {
                }

                String raspberrypiNumber = "pi0001";
                // raspberrpiNumberに対応したAR案内情報を取得する
                final Map<String, Object> characterGuideData = new DataBaseHelper().getCharacterGuide(raspberrypiNumber);

                // ARキャラクターの表示を行う
                WikitudeContentsFragment.setWikitudeContents(characterGuideData);

                // Mapオブジェクトを分解する
                final Object accessPointId = characterGuideData.get("access_point_id");
                final Object characterName = characterGuideData.get("character_name");
                final Object characterFilePath = characterGuideData.get("character_file_path");
                final Object textData = characterGuideData.get("text_data");

                a = characterFilePath.toString();

                //AR案内ボタンを表示させる
                arguideButton.setVisibility(View.VISIBLE);
                /* AR案内ボタンがクリックされた時の処理 */
 /*               arguideButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // AugmentedGuideActivityに移動する
                        Intent intent = new Intent(getApplication(), AugmentedGuideActivity.class);
                        startActivity(intent);
                    }
                });

                this.architectView.onPostCreate();
                this.architectView.load(WikitudeContentsFragment.getArchitectWorldPath());
*/








            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.architectView != null) {
            // onResumeメソッドでArchitectViewのonResumeメソッドを実行
            this.architectView.onResume();
            if (this.sensorAccuracyListener!=null) {
                this.architectView.registerSensorAccuracyChangeListener( this.sensorAccuracyListener );
            }
        }
        if ( this.locationProvider != null ) {
            this.locationProvider.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.architectView != null) {
            // onPauseメソッドでArchitectViewのonPauseメソッドを実行
            this.architectView.onPause();
            if ( this.sensorAccuracyListener != null ) {
                this.architectView.unregisterSensorAccuracyChangeListener(this.sensorAccuracyListener);
            }
        }
        if ( this.locationProvider != null ) {
            this.locationProvider.onPause();
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
        bluetoothAcqisition.endSearchDevice();
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

    public ArchitectViewHolderInterface.ILocationProvider getLocationProvider(final LocationListener locationListener) {
        return new LocationProvider(this, locationListener);
    }

    private long lastCalibrationToastShownTimeMillis = System.currentTimeMillis();

    public ArchitectView.SensorAccuracyChangeListener getSensorAccuracyListener() {
        return new ArchitectView.SensorAccuracyChangeListener() {
            @Override
            public void onCompassAccuracyChanged( int accuracy ) {
				/* UNRELIABLE = 0, LOW = 1, MEDIUM = 2, HIGH = 3 */
                if ( accuracy < SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM && CameraActivity.this != null && !CameraActivity.this.isFinishing() && System.currentTimeMillis() - CameraActivity.this.lastCalibrationToastShownTimeMillis > 5 * 1000) {
                    Toast.makeText( CameraActivity.this, R.string.compass_accuracy_low, Toast.LENGTH_LONG ).show();
                    CameraActivity.this.lastCalibrationToastShownTimeMillis = System.currentTimeMillis();
                }
            }
        };
    }
}