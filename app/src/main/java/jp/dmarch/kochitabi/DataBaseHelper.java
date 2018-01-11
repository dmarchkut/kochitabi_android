package jp.dmarch.kochitabi;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataBaseHelper {

    /* サンプルデータ */

    private String[] spot_id = {"sp0001", "sp0002", "sp0003", "sp0004", "sp0005", "sp0006", "sp0007"};
    private String[] environment_id = {"en0001", "en0002", "en0003", "en0004", "en0005", "en0006", "en0007"};
    private String[] spot_name = {"高知工科大学", "高知城", "ひろめ市場", "室戸岬", "四万十川", "桂浜", "早明浦ダム"};
    private String[] spot_phoname = {"コウチコウカダイガク", "コウチジョウ", "ヒロメイチバ", "ムロトミサキ", "シマントガワ", "カツラハマ", "サメウラダム"};
    private String[] street_address = {"高知県1", "高知県2", "高知県3", "高知県4", "高知県5", "高知県6", "高知県7"};
    private Integer[] postal_code = {1000001, 2000002, 3000003, 4000004, 5000005, 6000006, 7000007};
    private Double[] latitude = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7};
    private Double[] longitude = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7};
    private String[] photo_file_path = {"kut", "kochi", "hirome", "muroto"/* ←drawableに存在しない */, "shimanto", "katurahama", null };
    private String[] weather = {"雨", "晴", "曇", "曇", "晴", "晴", "雨"};
    private Double[] temperature = {11.0, 12.0, -1.0, 0.0, 1.0, -10.2, 30.3};
    private Double[] distance = {0.6444, 20.65353, 19.853546, 74.464262, 116.06246, 23.662462, 47.462642};


    public DataBaseHelper(Context context) {}

    public ArrayList<Map<String, Object>> getSpotsEnvironmentDistanceData(Double[] currentLocation) {
        ArrayList<Map<String, Object>> spotsEnvironmentDistanceData = null;

        spotsEnvironmentDistanceData = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < spot_name.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("spot_id", spot_id[i]);
            map.put("environment_id", environment_id[i]);
            map.put("spot_name", spot_name[i]);
            map.put("spot_phoname", spot_phoname[i]);
            map.put("street_address", street_address[i]);
            map.put("postal_code", postal_code[i]);
            map.put("latitude", latitude[i]);
            map.put("longitude", longitude[i]);
            map.put("photo_file_path",photo_file_path[i]);
            map.put("weather", weather[i]);
            map.put("temperature", temperature[i]);
            map.put("distance", distance[i]);

            spotsEnvironmentDistanceData.add(map);

        }

        return spotsEnvironmentDistanceData;
    };

}
