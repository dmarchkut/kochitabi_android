package jp.dmarch.kochitabi;

import java.util.Map;
import java.util.HashMap;

public class DataBaseHelper {
    protected Map<String, Object> getCharacterGuide(String raspberrypiNumber) {
        Map<String, Object> characterGuideData = new HashMap<String, Object>();

        String accessPointId = "ac0001";
        String characterName = "ユニティちゃん";
        String characterFilePath = "cube.wt3";
        String textData = "高知工科大学です。情報学群です。よろしくお願いします。";

        characterGuideData.put("access_point_id", accessPointId);
        characterGuideData.put("character_name", characterName);
        characterGuideData.put("character_file_path", characterFilePath);
        characterGuideData.put("text_data", textData);

        return characterGuideData;
    }
}