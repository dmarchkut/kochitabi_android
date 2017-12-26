package jp.dmarch.kochitabi;

import android.content.Context;
import android.location.LocationListener;

import static java.lang.Double.NaN;

public abstract class LocationAcquisition implements LocationListener {

    private Double[] currentLocation = {NaN, NaN};

    public LocationAcquisition(Context context) {

    }

    public void beginLocationAcquisition() {

    }

    public void endLocationAcquisition() {

    }

    public Double[] getCurrentLocation() {
        return currentLocation;
    }
}
