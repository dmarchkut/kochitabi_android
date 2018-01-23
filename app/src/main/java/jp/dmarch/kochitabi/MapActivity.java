package jp.dmarch.kochitabi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Double.NaN;

/* 参考Webサイト
 * https://qiita.com/rkonno/items/8bec3d5a45235fc88a08
 * https://akira-watson.com/android/google-map.html
 * http://seesaawiki.jp/w/moonlight_aska/d/%B3%C6%A5%DE%A1%BC%A5%AB%A1%BC%A4%CB%C2%D0%B1%FE%A4%B7%A4%BF%BD%E8%CD%FD%A4%F2%A4%B9%A4%EB
 * https://developers.google.com/maps/documentation/android-api/start?hl=ja
 * https://qiita.com/kazy/items/0ef55e1d750a49a9192f
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    // メンバ変数
    private GoogleMap mapData;
    private MapFragment displayMapData;
    private MarkerOptions markerOptions;
    private LocationAcquisition locationAcquisition;
    private Map<String, Object> spotData;
    private ArrayList<Map<String, Object>> spotsData;
    private ArrayList<Map<String, Object>> spotListData;
    private Double[] currentLocation;
    private int preActivity;
    public static final int HOME_ACTIVITY = 0;
    public static final int SPOT_DETAIL_ACTIVITY = 1;

    ListView mapSpotList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);      // バックボタン追加

        // 呼び出し元Activity取得
        Intent intent = getIntent();
        if (intent.getStringExtra("activity_name").equals("HomeActivity")) {
            preActivity = HOME_ACTIVITY;

            setTitle("マップ");
            setContentView(R.layout.activity_map);
        }
        else {
            preActivity = SPOT_DETAIL_ACTIVITY;

            // intentから情報を取得
            final String spotId = intent.getStringExtra("spot_id");
            final String environmentId = intent.getStringExtra("environment_id");
            final String spotName = intent.getStringExtra("spot_name");
            final String spotPhoname = intent.getStringExtra("spot_phoname");
            final String streetAddress = intent.getStringExtra("street_address");
            final Integer postalCode = intent.getIntExtra("postal_code", 0);
            final Double latitude = intent.getDoubleExtra("latitude", 0);
            final Double longitude = intent.getDoubleExtra("longitude", 0);
            final String photoFilePath = intent.getStringExtra("photo_file_path");

            spotData = new HashMap<String, Object>(); // spotDataのインスタンス化

            // spotDataにデータを挿入
            spotData.put("spot_id", spotId);
            spotData.put("environment_id", environmentId);
            spotData.put("spot_name", spotName);
            spotData.put("spot_phoname", spotPhoname);
            spotData.put("street_address", streetAddress);
            spotData.put("postal_code", postalCode);
            spotData.put("latitude", latitude);
            spotData.put("longitude", longitude);
            spotData.put("photo_file_path", photoFilePath);

            setTitle(spotName);
            setContentView(R.layout.activity_map_detail);

        }

        locationAcquisition = new LocationAcquisition(this);

        this.setGoogleMap();        // マップ表示設定

    }

    // バックボタンタップ時処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onResume() {
        super.onResume();
        locationAcquisition.beginLocationAcquisition();
        currentLocation = locationAcquisition.getCurrentLocation();        // 現在地取得

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationAcquisition.endLocationAcquisition();
    }

    /* Google Map を使用するための設定を行うメソッド */
    private void setGoogleMap() {
        // Google Maps Android APIのマップを使用するためMapFragmentを利用
        displayMapData = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
        displayMapData.getMapAsync(this);       // フラグメントにコールバックを設定

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapData = googleMap;

        // 呼び出し元によって処理変更
        // ホーム画面から呼ばれた際
        if (preActivity == HOME_ACTIVITY) {

            // 現在地が取得できるか(GPS稼働時)の判定
            if (!currentLocation[0].equals(NaN)) {
                this.setCurrentLocationMap(currentLocation);        // 現在地マーカ設定
                LatLng current = new LatLng(currentLocation[0], currentLocation[1]);        // 現在地座標設定
                mapData.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 12));     // 現在地カメラ設定(12倍)
            }
            else {
                LatLng defaultSpot = new LatLng(33.567237, 133.543661);
                mapData.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultSpot, 12));     // 現在地カメラ設定(12倍)
            }

            spotsData = new DataBaseHelper(this).getSpotsEnvironmentDistanceData(currentLocation);      // 観光地情報取得

            // 情報を抽出し観光地マーカ設定
            for (int i = 0; i < spotsData.size(); i++) {
                String spotName;        // 観光地名を格納するためのStringオブジェクト
                Double[] spotLocation = new Double[2];      // 緯度、経度の2つの情報を格納するためのdouble型配列

                spotName =  (((HashMap<String, Object>)spotsData.get(i)).get("spot_name")).toString();
                spotLocation[0] = Double.valueOf((((HashMap<String, Object>)spotsData.get(i)).get("latitude")).toString());
                spotLocation[1] = Double.valueOf((((HashMap<String, Object>)spotsData.get(i)).get("longitude")).toString());

                this.setSpotMarker(spotLocation, spotName);     // マーカ設定

            }

            this.displayMap();      // マップ上情報表示

        }
        // 詳細画面から呼ばれた際
        else {
            // 観光地座標取得
            Double[] spotLocation = new Double[2];      // 緯度、経度の2つの情報を格納するためのdouble型配列
            spotLocation[0] = Double.valueOf(spotData.get("latitude").toString());
            spotLocation[1] = Double.valueOf(spotData.get("longitude").toString());

            ArrayList<Double[]> accessPointLocations = new DataBaseHelper(this).getAccessPointLocations(spotData.get("spot_id").toString());        // アクセスポイント座標取得

            if (!currentLocation[0].equals(NaN)) {
                this.setCurrentLocationMap(currentLocation);        // 現在地マーカ設定
            }

            Double distance = locationAcquisition.getDistance(currentLocation, spotLocation);     // 観光地距離情報取得

            this.setSpotMarker(spotLocation, spotData.get("spot_name").toString());     // 観光地マーカ設定
            LatLng spot = new LatLng(spotLocation[0], spotLocation[1]);        // 観光地座標設定
            mapData.moveCamera(CameraUpdateFactory.newLatLngZoom(spot, 16));     // 観光地カメラ設定(16倍)
            // アクセスポイントマーカ設定
            for (int i = 0; i < accessPointLocations.size(); i++) {
                this.setAccessPointMarker(accessPointLocations.get(i));
            }

            this.displayDetailMap(spotData, distance);      // 距離情報表示

        }

    }

    /* マップ画面上にて情報の表示を行うメソッド */
    private void displayMap() {
        this.displaySpotInformation();

    }

    /* 詳細マップ上にて情報の表示を行うメソッド */
    private void displayDetailMap(Map<String, Object> spotData, Double distance) {
        // 現在地名設定
        TextView currentName = findViewById(R.id.map_current);
        currentName.setText("現在地");

        // 観光地名設定
        TextView spotName = findViewById(R.id.map_spot);
        spotName.setText(spotData.get("spot_name").toString());

        // 距離設定
        TextView distanceValue = findViewById(R.id.map_distance);
        if (!distance.equals(NaN)) {
            distanceValue.setText(String.format("%.1f", distance) + " km");
        }
        else {
            distanceValue.setText(" - ");
        }

    }

    /* マップ画面上で観光地のマーカを選択した際、画面下部に選択した観光地の情報を表示するメソッド */
    private void displaySpotInformation() {
        mapData.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String id = marker.getId();     // マーカID取得

                // マーカが示す観光地をListViewによって表示
                for (int i = 1; i <= spotsData.size(); i++) {
                    if (id.equals("m" + String.valueOf(i))) {
                        mapSpotList = (ListView) findViewById(R.id.map_spot_list);     // ListView読み込み
                        mapSpotList.setBackgroundResource(R.drawable.bg_border_around);

                        // リスト表示用ArrayList作成
                        spotListData = new ArrayList<Map<String, Object>>();
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("spot_id", (((HashMap<String, Object>)spotsData.get(i-1)).get("spot_id")).toString());
                        map.put("environment_id", (((HashMap<String, Object>)spotsData.get(i-1)).get("environment_id")).toString());
                        map.put("spot_name", (((HashMap<String, Object>)spotsData.get(i-1)).get("spot_name")).toString());
                        map.put("spot_phoname", (((HashMap<String, Object>)spotsData.get(i-1)).get("spot_phoname")).toString());
                        map.put("street_address", (((HashMap<String, Object>)spotsData.get(i-1)).get("street_address")).toString());
                        map.put("postal_code", (((HashMap<String, Object>)spotsData.get(i-1)).get("postal_code")).toString());
                        map.put("latitude", (((HashMap<String, Object>)spotsData.get(i-1)).get("latitude")).toString());
                        map.put("longitude", (((HashMap<String, Object>)spotsData.get(i-1)).get("longitude")).toString());
                        map.put("weather", (((HashMap<String, Object>)spotsData.get(i-1)).get("weather")).toString());
                        // 距離はNaN発生の恐れがあるためエラー用ハイフンを条件分岐で設定
                        if (!((HashMap<String, Object>)spotsData.get(i-1)).get("distance").equals(NaN)) {
                            map.put("distance", String.format("%.1f", ((HashMap<String, Object>)spotsData.get(i-1)).get("distance")) + " km");
                        }
                        else {
                            map.put("distance", " - ");
                        }
                        // 画像はnull値発生の恐れがあるためエラー用画像をnull条件分岐で設定
                        if (((HashMap<String, Object>)spotsData.get(i-1)).get("photo_file_path") != null) {
                            map.put("photo_file_path", getResources().getIdentifier((((HashMap<String, Object>)spotsData.get(i-1)).get("photo_file_path")).toString(), "drawable", "jp.dmarch.kochitabi"));
                            if (map.get("photo_file_path").equals(0)) {     // drawableにリソースが存在しなかった場合
                                map.put("photo_file_path", getResources().getIdentifier("noimage", "drawable", "jp.dmarch.kochitabi"));
                            }
                        }
                        else {
                            map.put("photo_file_path", getResources().getIdentifier("noimage", "drawable", "jp.dmarch.kochitabi"));
                        }
                        spotListData.add(map);

                        // ListViewにArrayListの中身を表示させるためのAdapter
                        // SimpleAdapterの中身は(Context, ArrayList, ウィジェット定義resource, ArrayListキー項目, 対応ウィジェット)
                        SimpleAdapter adapter = new SimpleAdapter (
                                getApplicationContext(),
                                spotListData,
                                R.layout.map_item,
                                new String[]{ "photo_file_path", "spot_name", "distance", "weather", "weather" },
                                new int[] { R.id.spot_image, R.id.spot_name, R.id.spot_distance, R.id.spot_weather, R.id.spot_frame }

                        );

                        adapter.setViewBinder(new ColorFrameViewBinder());      // フレーム色変更

                        mapSpotList.setAdapter(adapter);      // ListViewにAdapterを設定

                        mapSpotList.setVisibility(View.VISIBLE);        // View起動中

                        // 項目タップ時Detailへ画面遷移
                        mapSpotList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent,
                                                    View view, int pos, long id) {

                                Intent intent = new Intent(getApplicationContext(), SpotDetailActivity.class);        // 遷移Activity設定

                                // Mapからデータ取り出し
                                Map<String, Object> spotData = spotListData.get(pos);
                                String spotId = spotData.get("spot_id").toString();
                                String environmentId = spotData.get("environment_id").toString();
                                String spotName = spotData.get("spot_name").toString();
                                String spotPhoname = spotData.get("spot_phoname").toString();
                                String streetAddress = spotData.get("street_address").toString();
                                Integer postalCode = Integer.valueOf(spotData.get("postal_code").toString());
                                Double latitude = Double.valueOf(spotData.get("latitude").toString());
                                Double longitude = Double.valueOf(spotData.get("longitude").toString());
                                String photoFilePath;
                                if (spotData.get("photo_file_path") == null) {
                                    photoFilePath = null;
                                }
                                else {
                                    photoFilePath = spotData.get("photo_file_path").toString();
                                }

                                // 渡すデータ付与
                                intent.putExtra("spot_id", spotId);
                                intent.putExtra("environment_id", environmentId);
                                intent.putExtra("spot_name", spotName);
                                intent.putExtra("spot_phoname", spotPhoname);
                                intent.putExtra("street_address", streetAddress);
                                intent.putExtra("postal_code", postalCode);
                                intent.putExtra("latitude", latitude);
                                intent.putExtra("longitude", longitude);
                                intent.putExtra("photo_file_path", photoFilePath);

                                startActivity(intent);      // 画面遷移

                            }
                        });

                    }
                }

                return false;
            }
        });

        mapData.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mapSpotList = (ListView) findViewById(R.id.map_spot_list);     // ListView読み込み
                if(mapSpotList.getVisibility() == View.VISIBLE) {
                    mapSpotList.setVisibility(View.GONE);
                }

            }
        });

    }

    /* 現在地の位置情報を指すマーカを設定しマップ上に立てるメソッド */
    private void setCurrentLocationMap(Double[] currentLocation) {
        markerOptions = new MarkerOptions();        // マーカオブジェクトインスタンス
        LatLng current = new LatLng(currentLocation[0], currentLocation[1]);        // 現在地座標設定
        mapData.addMarker(markerOptions.position(current).title("現在地"));       // 現在地マーカ設定

    }

    /* 観光地の位置情報を指すマーカを設定しマップ上に立てるメソッド */
    private void setSpotMarker(Double[] spotLocation, String spotName) {
        markerOptions = new MarkerOptions();        // マーカオブジェクトインスタンス
        BitmapDescriptor spotIcon = BitmapDescriptorFactory.fromResource(R.drawable.spot_point);        // アイコン変更
        LatLng spot = new LatLng(spotLocation[0], spotLocation[1]);        // 観光地座標設定
        mapData.addMarker(markerOptions.icon(spotIcon).position(spot).title(spotName));       // 観光地マーカ設定

    }

    /* アクセスポイントの位置情報を指すマーカを設定しマップ上に立てるメソッド */
    private void setAccessPointMarker(Double[] accessPointLocation) {
        markerOptions = new MarkerOptions();        // マーカオブジェクトインスタンス
        BitmapDescriptor spotIcon = BitmapDescriptorFactory.fromResource(R.drawable.access_point);        // アイコン変更
        LatLng accessPoint = new LatLng(accessPointLocation[0], accessPointLocation[1]);        // 観光地座標設定
        mapData.addMarker(markerOptions.icon(spotIcon).position(accessPoint).title("アクセスポイント"));       // 観光地マーカ設定

    }

}

