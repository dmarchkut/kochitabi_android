package jp.dmarch.kochitabi;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataBaseHelper {

    private String[] spot_id= {"sp0001", "sp0002", "sp0003", "sp0004", "sp0005", "sp0006", "sp0007"};
    private String[] environment_id= {"en0001", "en0002", "en0003", "en0004", "en0005", "en0006", "en0007"};
    private String[] spot_name = {null, null, null, "室戸岬", "四万十川", "桂浜", "早明浦ダム"};
    private String[] spot_phoname = {"コウチコウカダイガク", "コウチジョウ", "ヒロメイチバ", "ムロトミサキ", "シマントガワ", "カツラハマ", "サメウラダム"};
    private String[] street_address = {"土佐山田", "高知", "高知中央", "室戸", "四万十", "高知南岸", "土佐・本山"};
    private Integer[] postal_code = {7830001, 7830002, 7830003, 7830004, 7830005, 7830006, 783007};
    private Double[] latitude = {33.620623, 33.560801, 33.560635, 33.2415162, 33.132971, 33.497154, 33.757142};
    private Double[] longitude = {133.719825, 133.531500, 133.535728, 134.176223, 132.980917, 133.574927, 133.550383};
    private String[] photo_file_path = {"kut", "kochi", "hirome", "muroto", "shimanto", "katurahama", null};

    public DataBaseHelper(Context context) {}

    public  void setRegisterData(){

    }

    public ArrayList<Map<String, Object>> getSpotsData() {
        ArrayList<Map<String, Object>> spotsData = new ArrayList<Map<String, Object>>();

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
            spotsData.add(map);
        }

        // データが一つもなければnullを返す
        if (spotsData.size() == 0) return null;

        return  spotsData;
    }


}