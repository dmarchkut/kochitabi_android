package jp.dmarch.kochitabi;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataBaseHelper {
    protected Map<String, Object> getCharacterGuideData(String raspberrypiNumber) {
        Map<String, Object> characterGuideData = new HashMap<String, Object>();

        String accessPointId = "ac0001";
        String characterName = "レレジイ";
        String characterFilePath = "wikitude/scarecrow_sunny.html";
        String textData = "高知工科大学、情報学群、クリピー、レレジイだよ。情報学群です。よろしくお願いします。";

        characterGuideData.put("access_point_id", accessPointId);
        characterGuideData.put("character_name", characterName);
        characterGuideData.put("character_file_path", characterFilePath);
        characterGuideData.put("text_data", textData);

        return characterGuideData;
    }

    public DataBaseHelper(Context context) {

    }

    public String getSpotText(String spotId) {
        return null;
    }

    public ArrayList<Map<String, Object>> getSpotsData() {
        return null;
    }

    public void setRegisterData(){}
}