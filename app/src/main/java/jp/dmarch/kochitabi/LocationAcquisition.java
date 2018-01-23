package jp.dmarch.kochitabi;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import static java.lang.Double.NaN;

public class LocationAcquisition implements LocationListener {

    private final double NO_DATA = NaN; // データが入っていない状態を示す

    public LocationAcquisition(Context context) {

    }

    public void beginLocationAcquisition() {

    }

    public void endLocationAcquisition() {

    }

    public Double[] getCurrentLocation() {
        Double[] currentLocation = {33.567222, 133.543660};
        //currentLocation[0] = NaN;
        //currentLocation[1] = NaN;

        return currentLocation;
    }

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

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
