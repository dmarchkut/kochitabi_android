package jp.dmarch.kochitabi;

import android.content.Context;
import android.location.LocationListener;

import static java.lang.Double.NaN;

public abstract class LocationAcquisition implements LocationListener {

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
}
