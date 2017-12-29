package jp.dmarch.kochitabi;

import android.content.Context;

import java.util.ArrayList;
import java.util.Map;

public class DataBaseHelper {

    public DataBaseHelper(Context context) {

    }

    public ArrayList<Map<String, Object>> getSpotsEnvironmentDistanceData(Double[] currentLocation) {
        ArrayList<Map<String, Object>> spotsEnvironmentDistanceData = new ArrayList<Map<String, Object>>();

        return spotsEnvironmentDistanceData;
    }

    public ArrayList<Double[]> getAccessPointLocations(String spotId) {
        ArrayList<Double[]> accessPointLocations = new ArrayList<Double[]>();

        return accessPointLocations;
    }

}
