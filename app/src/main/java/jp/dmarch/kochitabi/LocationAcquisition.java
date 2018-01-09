package jp.dmarch.kochitabi;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import static java.lang.Double.NaN;

public class LocationAcquisition implements LocationListener {

    private Double[] currentLocation = {NaN, NaN};
    private Double distance;

    public LocationAcquisition(Context context) {

    }

    public void beginLocationAcquisition() {

    }

    public void endLocationAcquisition() {

    }

    public Double[] getCurrentLocation() {
        return currentLocation;
    }

    public Double getDistance(Double[] currentLocation, Double[] spotLocation) {
        return distance;
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
