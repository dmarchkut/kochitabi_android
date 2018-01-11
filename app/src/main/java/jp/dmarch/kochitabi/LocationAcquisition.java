package jp.dmarch.kochitabi;

import android.content.Context;
import android.location.LocationListener;
import android.util.Log;

import static java.lang.Double.NaN;

public abstract class LocationAcquisition implements LocationListener {

    private Double[] currentLocation = {0.5, 0.5};

    public LocationAcquisition(Context context) {}

    public void beginLocationAcquisition() { Log.d("Location", "begin"); }

    public void endLocationAcquisition() { Log.d("Location", "end"); }

    public Double[] getCurrentLocation() {
        return currentLocation;
    }
}
