package jp.dmarch.kochitabi;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataBaseHelper {

    private String[] spot_id= {"sp0001", "sp0002", "sp0003", "sp0004", "sp0005", "sp0006", "sp0007"};
    private String[] environment_id= {"en0001", "en0002", "en0003", "en0004", "en0005", "en0006", "en0007"};
    private String[] spot_name = {"高知工科大学", "高知城", "ひろめ市場", "室戸岬", "四万十川", "桂浜", "早明浦ダム"};
    private String[] spot_phoname = {"コウチコウカダイガク", "コウチジョウ", "ヒロメイチバ", "ムロトミサキ", "シマントガワ", "カツラハマ", "サメウラダム"};
    private String[] street_address = {"土佐山田", "高知", "高知中央", "室戸", "四万十", "高知南岸", "土佐・本山"};
    private Integer[] postal_code = {7830001, 7830002, 7830003, 7830004, 7830005, 7830006, 783007};
    private Double[] latitude = {33.620623, 33.560801, 33.560635, 33.2415162, 33.132971, 33.497154, 33.757142};
    private Double[] longitude = {133.719825, 133.531500, 133.535728, 134.176223, 132.980917, 133.574927, 133.550383};
    private String[] photo_file_path = {"kut", "kochi", "hirome", "muroto", "shimanto", "katurahama", null};
    private String[] weather = {"雨", "晴れ", "曇り", "曇り", "晴れ", "晴れ", "霧雨"};
    private Double[] temperature = {20.0, 20.6, 19.8, 14.4, 16.0, 23.6, 17.4};
    private Double[] distance = {0.6, 20.6, 19.8, 74.4, 116.0, 23.6, 47.4};

    private String[] spot_text = {
            "本学は「大学のあるべき姿を常に追求し、世界一流の大学をめざす」という高い志を掲げ、高知県が設置し学校法人が運営する公設民営大学として開学しました。先進的な教育システムをいち早く取り入れ、大学としての存在感を発揮し続けています。平成21年4月からは公立大学法人として新たな一歩を踏み出し、公立大学法人という強固な基盤の上に積み重ねてきた自主・自律的な運営ノウハウを生かした革新的な大学運営を進めています。",
            "土佐24万石を襲封した山内一豊によって創建されて以来、約400年余りの歴史を有する南海の名城として名高い",
            "平成１０年、「この土地を商店街活性化の核に」と地元から提案されたプランに、所有者である財団法人民間都市開発推進機構、ミサワホーム株式会社、地元建設会社等が賛同し「ひろめ市場」が建設されました。",
            "壮大な岩、荒々しい海、空海が残した数々の伝説、パワースポットなど盛りだくさん　地球の営みを体感できるスポット！",
            "津野町不入山に端を発し、中土佐町、四万十町、四万十市を流れる四国最長の大河（全長196km）。『最後の清流』として知られ、火振り漁や柴づけ漁など現在でも伝統的な漁が行われている。上流から下流に数多く残っている沈下橋は、欄干がなく川の増水時に水面下に沈むことで流失しないように作られた橋で、今も住民の生活道であるとともに、四万十川の風物詩となっている。",
            "高知県を代表する景勝地の一つ。浦戸湾口、龍頭［りゅうず］岬と龍王岬の間に弓状に広がる海岸で、背後に茂り合う松の緑と、海浜の五色の小砂利、紺碧の海が箱庭のように調和する見事な景勝地。古来より月の名所として知られ、“月の名所は桂浜・・・”と「よさこい節」にも唄われている。東端の龍頭岬では、幕末の志士坂本龍馬の銅像が太平洋を見下ろしている。海浜一帯は「桂浜公園」となっており、水族館や、山手には「坂本龍馬記念館」もある。",
            null
    };

    public DataBaseHelper(Context context) {

    }

    public ArrayList<Map<String, Object>> getSpotsEnvironmentDistanceData(Double[] currentLocation) {
        ArrayList<Map<String, Object>> spotsEnvironmentDistanceData = new ArrayList<Map<String, Object>>();

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
    }

    public Map<String, Object> getEnvironmentData(String environmentId) {
        Map<String, Object> environmentData = new HashMap<String, Object>();

        int i;

        for (i = 0; i < spot_name.length; i++) {
            if (environmentId.equals(environment_id[i])) {
                break;
            }
        }

        environmentData.put("spot_id", spot_id[i]);
        environmentData.put("environment_id", environment_id[i]);
        environmentData.put("spot_name", spot_name[i]);
        environmentData.put("spot_phoname", spot_phoname[i]);
        environmentData.put("street_address", street_address[i]);
        environmentData.put("postal_code", postal_code[i]);
        environmentData.put("latitude", latitude[i]);
        environmentData.put("longitude", longitude[i]);
        environmentData.put("photo_file_path",photo_file_path[i]);
        environmentData.put("weather", weather[i]);
        environmentData.put("temperature", temperature[i]);
        environmentData.put("distance", distance[i]);

        return environmentData;
    }

    public ArrayList<Double[]> getAccessPointLocations(String spotId) {
        ArrayList<Double[]> accessPointLocations = new ArrayList<Double[]>();

        accessPointLocations.add(new Double[] {33.621136, 133.717958});
        accessPointLocations.add(new Double[] {33.620288, 133.720973});
        accessPointLocations.add(new Double[] {33.623013, 133.720061});

        return accessPointLocations;
    }

    public String getSpotText(String spotId) {
        String spotText = null;

        int i;

        for (i = 0; i < spot_name.length; i++) {
            if (spotId.equals(spot_id[i])) {
                break;
            }
        }

        spotText = spot_text[i];

        return spotText;
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
            map.put("text_data", spot_text[i]);
            spotsData.add(map);
        }

        // データが一つもなければnullを返す
        if (spotsData.size() == 0) return null;

        return  spotsData;
    }


}