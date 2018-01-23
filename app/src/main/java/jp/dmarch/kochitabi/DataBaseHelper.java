
package jp.dmarch.kochitabi;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class DataBaseHelper {

    private Context context;

    public DataBaseHelper(Context context) {
        this.context = context;
    }

    public Map<String, Object> getEnvironmentData(String environmentId) {

        Map<String, Object> environmentData = new HashMap<String, Object>();

        String a = "晴";
        Double b = 10.0;

        environmentData.put("environment_id", environmentId);
        environmentData.put("weather", a);
        environmentData.put("temperature", b);

        return environmentData;
    }

    public String getSpotText(String spotId) {

        String spotText = "ああああああああああああああああああああああああああああああああ。あああああああああああああああああああああああああああああああ。あああああああああああああああああああ。ああああああああああああああああああ。ああああああああああああああ。";

        return spotText;
    }
}