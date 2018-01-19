package jp.dmarch.kochitabi;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ServerExchange {

    public final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final static String spotKeys[] // ローカル観光地テーブルのレコードのキー
            = new String[] {"spot_id", "environment_id", "spot_name", "spot_phoname", "street_address","postal_code",
            "latitude", "longitude", "photo_file_path", "text_data", "created_at", "updated_at"};
    private final static String environmentKeys[] // ローカル環境テーブルのレコードのキー
            = new String[] {"environment_id", "weather", "temperature", "created_at", "updated_at"};
    private final static String accessPointKeys[] // ローカルアクセスポイントテーブルのレコードのキー
            = new String[] {"access_point_id", "spot_id", "access_point_name", "latitude", "longitude",
            "raspberry_pi_number", "text_data", "created_at", "updated_at"};
    private final static String characterKeys[] // ローカルキャラクターテーブルのレコードのキー
            = new String[] {"access_point_id", "character_name", "character_file_path",
            "created_at", "updated_at"};

    private final String[][] DBKeys = {spotKeys, environmentKeys, accessPointKeys, characterKeys};

    String date = "2018-01-16 02:17:00.624643";

    private Object spotDatas[]
            = {"sp0001", "en0001", "高知工科大学","コウチコウカダイガク", "高知県〇〇市", new Integer(1002345),
                new Double(54.0), new Double(325.0), "kut", "ここは高知工科大学です。",date , date};
    private Object environmentDatas[]
            = {"en0001", "晴", new Double(10.5), date, date};
    private Object accessPointDatas[]
            = {"ac0001", "sp0001", "本館", new Double(54.5), new Double(326.2),
               "pi0001", "どうもよろしくなのじゃ", date, date};
    private Object characterDatas[]
            = {"ac0001", "のじゃのじゃ", "noja",
            date, date};


    private Object characterDatas2[]
            = {"ac0001", "がとさん", "noja",
            date, date};

    private Object[][] datas = {spotDatas, environmentDatas, accessPointDatas, characterDatas};

    public ServerExchange() {}

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
            Log.d("ServerExchange", "start getCharacterTable method:");

            ArrayList<Map<String, Object>> tableData = new ArrayList<Map<String, Object>>();
            Map<String, Object> recordData = new HashMap<String, Object>();
            int j = 0;

            // / レコード単位で実行
            for (String recordKey: characterKeys) {
                //if (flag) recordData.put(recordKey, characterDatas[j]);
                recordData.put(recordKey, characterDatas2[j]);
                j++;
            }
            tableData.add(recordData);

        Log.d("ServerExchange", "end getCharacterTable method:");

        return tableData;
    }

    public ArrayList<Map<String, Object>> getCharacterTable2() {

        ArrayList<Map<String, Object>> tableData = new ArrayList<Map<String, Object>>();
        Map<String, Object> recordData = new HashMap<String, Object>();
        int j = 0;

        // / レコード単位で実行
        for (String recordKey: characterKeys) {
            recordData.put(recordKey, characterDatas2[j]);
            j++;
        }
        tableData.add(recordData);

        return tableData;
    }
}
