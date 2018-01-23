package jp.dmarch.kochitabi;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;

import static android.content.Context.WIFI_SERVICE;
import static java.lang.Double.NaN;
import static java.lang.Math.sin;

/* 距離計算(getDistance)
 * https://qiita.com/a_nishimura/items/6c2642343c0af832acd4
 * http://seesaawiki.jp/w/moonlight_aska/d/WiFi%A4%F2ON/OFF%A4%B9%A4%EB
 */


public class LocationAcquisition implements LocationListener {

    private final double NO_DATA = NaN; // データが入っていない状態を示す
    private final int REQUEST_PERMISSION = 1000;

    private Context context;
    private Double[] currentLocation = {NO_DATA, NO_DATA}; // 現在地
    private LocationManager locationManager;
    private Boolean hasGottenFlag; // GPSで現在地を取得したことがあるか(true:ある, false:なし)
    private Boolean settingFlag;    // GPSの設定をしたか(設定画面を開いたか)

    // コンストラクタ
    public LocationAcquisition(Context context) {

        this.context = context;

        settingFlag = false;

        // Android 6.0以上の端末ならパーミッションチェックを行う
        if (Build.VERSION.SDK_INT >= 23) {

            // アプリでGPSの使用が許可されていないなら
            if (PermissionChecker.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && PermissionChecker.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // 再度、使用許可要求を出す必要があるか（一度拒否していたらtrue）
                if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // GPSの使用許可を要求
                    ActivityCompat.requestPermissions((Activity) context, new String[] {Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION);
                }

            }

        }

    }

    /* 現在地の計測を行うための設定及び、その後の計測を開始 */
    public void beginLocationAcquisition() {

        hasGottenFlag = false; // GPSで取得したことがないと設定

        // Managerの設定
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // GPSが無効であればGPSエラーダイアログを表示
        if(!isLocationAcquisition()) {
            // GPSエラーダイアログを表示
            new AlertDialog.Builder(context)
                    .setMessage("位置情報が取得できませんでした\nGPSを有効にします")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            displayLocationAcquisition(); // 設定画面へ遷移
                        }
                    })
                    .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    })
                    .show();

        }

        // Android 6.0以上の端末ならパーミッションチェックを行う
        if (Build.VERSION.SDK_INT >= 23) {

            // アプリでGPSの使用が許可されていないなら
            if (PermissionChecker.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && PermissionChecker.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // 再度、使用許可要求を出す必要があるか（一度拒否していたらtrue）
                if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // GPSの使用許可を要求
                    ActivityCompat.requestPermissions((Activity) context, new String[] {Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION);

                } else {
                    return; // 一度拒否されているため処理を行わない
                }

            } else { // パーミッションが許可されている場合

                // 計測を開始
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, // 使う機能(GPS機能かWifi機能か)
                        1000, // 最小1000ミリ秒経過ごとに通知
                        1, // 最小1メートル移動ごとに通知
                        this // リスナー
                );
            }

        } else { // パーミッションチェックが不要である場合

            // 計測を開始
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, // 使う機能(GPS機能かWifi機能か)
                    1000, // 最小1000ミリ秒経過ごとに通知
                    1, // 最小1メートル移動ごとに通知
                    this // リスナー
            );
        }

    }

    /* 現在地の計測を終了 */
    public void endLocationAcquisition() {

        // 計測を終了
        locationManager.removeUpdates(this);

    }

    /* GPS機能が使用可能であるか判断 */
    private Boolean isLocationAcquisition() {

        // GPS機能が使えるか否か
        Boolean checkedLocation = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return checkedLocation;

    }

    /* 現在地の位置情報を取得 */
    public Double[] getCurrentLocation() {
        Double[] currentLocation = {33.567222, 133.543660};
        //currentLocation[0] = NaN;
        //currentLocation[1] = NaN;


        // 現在地の測定がまだ行われていないならば
        if (currentLocation[0].equals(NO_DATA) || currentLocation[1].equals(NO_DATA) || !hasGottenFlag) {

            // アプリでGPSの使用が許可されていないなら
            if (PermissionChecker.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && PermissionChecker.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // 再度、使用許可要求を出す必要があるか（一度拒否していたらtrue）
                if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // GPSの使用許可を要求
                   ActivityCompat.requestPermissions((Activity) context, new String[] {Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION);
                }

            } else { //許可されているなら

                // Wifiが無効なら有効に
                if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                    WifiManager wifi = (WifiManager)context.getSystemService(WIFI_SERVICE);
                    wifi.setWifiEnabled(true);
                }

                try {

                    // 設定画面から戻ってきたなら設定反映のため少し待つ
                    if (settingFlag) Thread.sleep(2000);
                    settingFlag = false;

                    // Wifiで過去に取得した最新の現在地を取得
                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    currentLocation[0] = location.getLatitude(); // 緯度を取得
                    currentLocation[1] = location.getLongitude(); // 経度を取得

                } catch (Exception exception) {
                    exception.printStackTrace();
                }

            }
        }
        return currentLocation;

    }

    /* 現在地の位置情報を更新 */
    private void updateCurrentLocation(Location currentLocation) {

        this.currentLocation[0] = currentLocation.getLatitude(); // 緯度を取得
        this.currentLocation[1] = currentLocation.getLongitude(); // 経度を取得

        hasGottenFlag = true; // GPSで現在地を取得したことがあると設定

    }

    /* 現在地と観光地との距離を計算 */
    public Double getDistance(Double[] currentLocation, Double[] spotLocation) {

        // 受け取ったデータが想定外のものである際にnullを返す
        if ((currentLocation.length != 2) || (spotLocation.length != 2)) return null;

        Double latitudeCurrent = currentLocation[0]; // 現在地の緯度
        Double longitudeCurrent = currentLocation[1]; // 現在地の経度
        Double latitudeSpot = spotLocation[0]; // 観光地の緯度
        Double longitudeSpot = spotLocation[1]; // 観光地の経度

        // 現在地が取得できていないため、NO_DATAを返す
        if ((latitudeCurrent.equals(NO_DATA)) || (longitudeCurrent.equals(NO_DATA))) return NO_DATA;

        Double theta = longitudeCurrent - longitudeSpot;

        // 距離を計算
        Double distance = Math.sin(convertDegreesToRadian(latitudeCurrent)) * Math.sin(convertDegreesToRadian(latitudeSpot))
                + Math.cos(convertDegreesToRadian(latitudeCurrent)) * Math.cos(convertDegreesToRadian(latitudeSpot)) * Math.cos(convertDegreesToRadian(theta));

        distance = Math.acos(distance);
        distance = convertRadianToDegrees(distance);

        distance = distance * 60 * 1.1515 * 1.609344; // kmに変換

        return distance;

    }

    /* DegreesをRadianに変換 */
    private Double convertDegreesToRadian(Double degrees) {
        return degrees * (Math.PI / 180f);
    }

    /* RadianをDegreesに変換 */
    private Double convertRadianToDegrees(Double radian) {
        return radian * (180f / Math.PI);
    }

    /* GPSの設定画面を表示 */
    private void displayLocationAcquisition() {

        // GPSの設定を行ったことを示すフラグをtrueへ
        settingFlag = true;

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
