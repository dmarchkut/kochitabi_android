package jp.dmarch.kochitabi;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private int preActivity;
    public static final int HOME_ACTIVITY = 0;
    public static final int SPOT_DETAIL_ACTIVITY = 1;

    ListView mapSpotList;

    /* サンプルデータ
    private Double[] currentLocation = {33.567240, 133.543657};
    private String[] spot_name = {"高知工科大学", "高知城", "ひろめ市場", "室戸岬", "四万十川", "桂浜", "早明浦ダム"};
    private Double[] latitude = {33.620623, 33.560801, 33.560635, 33.2415162, 33.132971, 33.497154, 33.757142};
    private Double[] longitude = {133.719825, 133.531500, 133.535728, 134.176223, 132.980917, 133.574927, 133.550383};
    private String[] photo_file_path = {"kut",
            "kochi",
            "hirome",
            "muroto",
            "shimanto",
            "katurahama",
            null };
    private Double[] distance = {0.6, 20.6, 19.8, 74.4, 116.0, 23.6, 47.4};
    private String[] weather = {"雨", "晴", "曇", "曇", "晴", "晴", "曇", "雨"};
    private ArrayList<Double[]> accessPointLocations;
    */

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
            final String environmentId = intent.getStringExtra("environment id");
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
            spotData.put("environment id", environmentId);
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
        locationAcquisition.beginLocationAcquisition();

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
    public void onDestroy() {
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
            Double[] currentLocation = locationAcquisition.getCurrentLocation();        // 現在地取得
            this.setCurrentLocationMap(currentLocation);        // 現在地マーカ設定

            spotsData = new DataBaseHelper(this).getSpotsEnvironmentDistanceData(currentLocation);      // 観光地情報取得

            /* サンプル
            // spotsDataデータ代入
            spotsData = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < spot_name.length; i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("spot_name", spot_name[i]);
                map.put("latitude", latitude[i]);
                map.put("longitude", longitude[i]);
                map.put("distance", distance[i]);
                map.put("weather", weather[i]);
                map.put("photo_file_path",photo_file_path[i]);

                spotsData.add(map);

            }
            */

            // 情報を抽出し観光地マーカ設定
            for (int i = 0; i < spotsData.size(); i++) {
                String spotName;        // 観光地名を格納するためのStringオブジェクト
                Double[] spotLocation = new Double[2];      // 緯度、経度の2つの情報を格納するためのdouble型配列

                spotName =  (((HashMap<String, Object>)spotsData.get(i)).get("spot_name")).toString();
                spotLocation[0] = new Double((((HashMap<String, Object>)spotsData.get(i)).get("latitude")).toString());
                spotLocation[1] = new Double((((HashMap<String, Object>)spotsData.get(i)).get("longitude")).toString());

                this.setSpotMarker(spotLocation, spotName);     // マーカ設定

            }

            this.displayMap();      // マップ上情報表示

        }
        // 詳細画面から呼ばれた際
        else {
            // 観光地座標取得
            Double[] spotLocation = new Double[2];      // 緯度、経度の2つの情報を格納するためのdouble型配列
            spotLocation[0] = new Double(spotData.get("latitude").toString());
            spotLocation[1] = new Double(spotData.get("longitude").toString());
            /* サンプル
            spotLocation[0] = 33.620623;
            spotLocation[1] = 133.719825;
            */

            ArrayList<Double[]> accessPointLocations = new DataBaseHelper(this).getAccessPointLocations(spotData.get("spot_id").toString());        // アクセスポイント座標取得
            /* サンプル
            accessPointLocations = new ArrayList<Double[]>();
            accessPointLocations.add(new Double[] {33.621136, 133.717958});
            accessPointLocations.add(new Double[] {33.620288, 133.720973});
            accessPointLocations.add(new Double[] {33.623013, 133.720061});
            Double distance = 18.6;
            */

            Double[] currentLocation = locationAcquisition.getCurrentLocation();        // 現在地位置情報取得
            this.setCurrentLocationMap(currentLocation);        // 現在地マーカ設定

            Double distance = locationAcquisition.getDistance(currentLocation, spotLocation);     // 観光地距離情報取得

            this.setSpotMarker(spotLocation, spotData.get("spot_name").toString());     // 観光地マーカ設定
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
        distanceValue.setText(String.valueOf(distance));

    }

    /* マップ画面上で観光地のマーカを選択した際、画面下部に選択した観光地の情報を表示するメソッド */
    private void displaySpotInformation() {
        mapData.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String id = marker.getId();     // マーカID取得

                // マーカが示す観光地をListViewによって表示
                for (int i = 0; i < spotsData.size(); i++) {
                    if (id.equals("m" + String.valueOf(i))) {
                        mapSpotList = (ListView) findViewById(R.id.map_spot_list);     // ListView読み込み
                        mapSpotList.setBackgroundColor(getResources().getColor(R.color.white));

                        // リスト表示用ArrayList作成
                        spotListData = new ArrayList<Map<String, Object>>();
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("spot_name", (((HashMap<String, Object>)spotsData.get(i-1)).get("spot_name")).toString());
                        map.put("distance", (((HashMap<String, Object>)spotsData.get(i-1)).get("distance")).toString());
                        map.put("weather", (((HashMap<String, Object>)spotsData.get(i-1)).get("weather")).toString());
                        // 画像はnull値発生の恐れがあるためエラー用画像をnull条件分岐で設定
                        if (((HashMap<String, Object>)spotsData.get(i-1)).get("photo_file_path") != null) {
                            map.put("photo_file_path", getResources().getIdentifier((((HashMap<String, Object>)spotsData.get(i-1)).get("photo_file_path")).toString(), "drawable", "jp.dmarch.kochitabi"));
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
                                R.layout.spot_item,
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
                                Integer postalCode = new Integer(spotData.get("postal_code").toString()).intValue();
                                Double latitude = new Double(spotData.get("latitude").toString()).doubleValue();
                                Double longitude = new Double(spotData.get("longitude").toString()).doubleValue();
                                String photoFilePath = spotData.get("photo_file_path").toString();

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
        mapData.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 12));     // 現在地カメラ設定(12倍)

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
