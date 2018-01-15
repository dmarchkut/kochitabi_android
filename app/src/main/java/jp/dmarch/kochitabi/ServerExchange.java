package jp.dmarch.kochitabi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerExchange {

    private final static String spotKeys[] // ローカル観光地テーブルのレコードのキー
            = new String[] {"spot_id", "environment_id", "spot_name", "spot_phoname", "street_address",
            "latitude", "longitude", "photo_file_path", "text_data", "create_at", "update_at"};
    private final static String environmentKeys[] // ローカル環境テーブルのレコードのキー
            = new String[] {"environment_id", "weather", "temperature", "create_at", "update_at"};
    private final static String accessPointKeys[] // ローカルアクセスポイントテーブルのレコードのキー
            = new String[] {"access_point_id", "spot_id", "access_point_name", "latitude", "longitude",
            "raspberry_pi_number", "text_data", "create_at", "update_at"};
    private final static String characterKeys[] // ローカルキャラクターテーブルのレコードのキー
            = new String[] {"access_point_id", "character_name", "character_file_path",
            "create_at", "update_at"};

    private final String[][] DBKeys = {spotKeys, environmentKeys, accessPointKeys, characterKeys};


    private Object spotDatas[]
            = {"sp0001", "en0001", "高知工科大学", "高知県〇〇市", 1002345,
                54.0, 325.0, "kut", "ここは高知工科大学です。", "2018-01-16 02:17:00.624643", "2018-01-16 02:17:30.35342"};
    private Object environmentDatas[]
            = {"en0001", "晴", 10.5, "2018-01-10 02:17:00.624643", "2018-01-10 02:17:30.35342"};
    private Object accessPointDatas[]
            = {"ac0001", "sp0001", "本館", 54.5, 326.2,
               "pi0001", "どうもよろしくなのじゃ", "2018-02-16 02:17:00.624643", "2018-02-16 02:17:30.35342"};
    private Object characterDatas[]
            = {"ac0001", "のじゃのじゃ", "noja",
               "2018-01-20 02:17:00.624643", "2018-01-20 02:17:30.35342"};

    private Object[][] datas = {spotDatas, environmentDatas, accessPointDatas, characterDatas};

    public ArrayList<ArrayList<Map<String, Object>>> getLocalDataBaseTables() {
        ArrayList<ArrayList<Map<String, Object>>> localDataBaseTables;


        localDataBaseTables = new ArrayList<ArrayList<Map<String,Object>>>();
        int i = 0;

        // テーブル単位で実行
        for (String[] tableKeys: DBKeys) {
            ArrayList<Map<String, Object>> tableData = new ArrayList<Map<String, Object>>();
            Map<String, Object> recordData = new HashMap<String, Object>();
            Object[] data = this.datas[i];
            int j = 0;

            // / レコード単位で実行
            for (String recordKey: tableKeys) {
                recordData.put(recordKey, data[j]);
                j++;
            }

            i++;
            tableData.add(recordData);
            localDataBaseTables.add(tableData);
        }
        return localDataBaseTables;
    }

    public ArrayList<Map<String, Object>> getEnvironmentTable() {

        ArrayList<Map<String, Object>> tableData = new ArrayList<Map<String, Object>>();
        Map<String, Object> recordData = new HashMap<String, Object>();
        int j = 0;

        // / レコード単位で実行
        for (String recordKey: environmentKeys) {
            recordData.put(recordKey, environmentDatas[j]);
            j++;
        }
        tableData.add(recordData);

        return tableData;
    }


    public ArrayList<Map<String, Object>> getCharacterTable() {

            ArrayList<Map<String, Object>> tableData = new ArrayList<Map<String, Object>>();
            Map<String, Object> recordData = new HashMap<String, Object>();
            int j = 0;

            // / レコード単位で実行
            for (String recordKey: characterKeys) {
                recordData.put(recordKey, characterDatas[j]);
                j++;
            }
            tableData.add(recordData);

        return tableData;
    }
}
