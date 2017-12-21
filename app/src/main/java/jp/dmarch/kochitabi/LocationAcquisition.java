package jp.dmarch.kochitabi;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import static java.lang.Math.sin;


public class LocationAcquisition implements LocationListener{

    private final double NO_DATA = Double.parseDouble(null);
    private final int REQUEST_PERMISSION = 1000;

    private Context context;
    private Double[] currentLocation = {NO_DATA, NO_DATA};
    private LocationManager locationManager;

    public LocationAcquisition(Context context) {
        this.context = context;
    }

    /* 現在地の計測を行うための設定及び、その後の計測を開始 */
    public void beginLocationAcquisition() {

        // Managerの設定
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // アプリでGPSの使用が許可されていないなら
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // GPSの使用許可を要求
            ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},REQUEST_PERMISSION);
        }

        // GPSが無効であればGPS設定画面へ遷移
        if(!isLocationAcquisition()) displayLocationAcquisition();

        // 計測を開始
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, // 使う機能(GPS機能かWifi機能か)
                1000, // 最小1000ミリ秒経過ごとに通知
                1, // 最小1メートル移動ごとに通知
                this // リスナー
        );
    }

    /* 現在地の計測を終了 */
    public void endLocationAcquisition() {

        // 計測を終了
        locationManager.removeUpdates(this);

    }

    /* GPS機能が使用可能であるか判断 */
    private Boolean isLocationAcquisition() {

        Boolean checkedLocation = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return checkedLocation;

    }

    /* 現在地の位置情報を取得 */
    public Double[] getCurrentLocation() {

        // 現在地の測定がまだ行われていないならば
        if (currentLocation[0] == NO_DATA
                || currentLocation[1] == NO_DATA) {

            // アプリでGPSの使用が許可されていないなら
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // GPSの使用許可を要求
                ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},REQUEST_PERMISSION);
            }

            // Wifiで過去に取得した最新の現在地を取得
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            currentLocation[0] = location.getLatitude(); // 緯度を取得
            currentLocation[1] = location.getLongitude(); // 経度を取得
        }

        return currentLocation;

    }

    /* 現在地の位置情報を更新 */
    private void updateCurrentLocation(Location currentLocation) {

        this.currentLocation[0] = currentLocation.getLatitude(); // 緯度を取得
        this.currentLocation[1] = currentLocation.getLongitude(); // 経度を取得

    }

    /* 現在地と観光地との距離を計算 */
    public Double getDistance(Double[] currentLocation, Double[] spotLocation) {

        double latitudeCurrent = currentLocation[0]; // 現在地の緯度
        double longitudeCurret = currentLocation[1]; // 現在地の経度
        double latitudeSpot = spotLocation[0]; // 観光地の緯度
        double longitudeSpot = spotLocation[1]; // 観光地の経度

        // 三角形の隣辺、対辺の長さの計算
        double side1 = (latitudeCurrent > latitudeSpot)? latitudeCurrent - latitudeSpot : latitudeSpot - latitudeCurrent;
        double side2 = (longitudeCurret > longitudeSpot)? longitudeCurret - longitudeSpot : longitudeSpot - longitudeCurret;

        double distance = Math.sqrt(Math.pow(side1, 2) + Math.pow(side2, 2)); // 距離の計算
        return distance;

    }

    /* GPSの設定画面を表示 */
    private void displayLocationAcquisition() {

        // GPSの設定画面へ遷移
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);

    }

    // 現在地が取得され通知されたら自動的に実行される
    @Override
    public void onLocationChanged(Location location) {
        updateCurrentLocation(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}
}
