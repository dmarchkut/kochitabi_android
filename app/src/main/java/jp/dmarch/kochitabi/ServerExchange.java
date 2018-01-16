package jp.dmarch.kochitabi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*
 * AsyncTaskとHttp通信
 * https://qiita.com/a_nishimura/items/19cf3f60ad1dd3f66a84
 * https://qiita.com/yamikoo@github/items/2a40f93509f4154ccd48
 * https://qiita.com/hkusu/items/8572d768243fe7e7ed88
 * JSON ↓
 * https://qiita.com/kaikusakari/items/383ecf413618178c8be6
 */


public class ServerExchange {

    // ローカルDBのデータを取得するためのAPIに共通するURL部分
    private final static String API_URL = "https://lit-springs-17205.herokuapp.com/api/";

    // ローカル環境テーブルの名前
    private final static String SPOT_TABLE_NAME = "local_spot";
    // ローカル環境テーブルの名前
    private final static String ENVIRONMENT_TABLE_NAME = "local_environment";
    // ローカル環境テーブルの名前
    private final static String ACCESS_POINT_TABLE_NAME = "local_access_point";
    // ローカルキャラクターテーブルの名前
    private final static String CHARACTER_TABLE_NAME = "local_character";

    // ローカルデータベースのテーブル
    private final static String[] LOCAL_DATABASE_TABLES = {
            /*SPOT_TABLE_NAME, */ENVIRONMENT_TABLE_NAME, ACCESS_POINT_TABLE_NAME, CHARACTER_TABLE_NAME
    };

    // キーに対応するデータの種類
    private final static String[] DOUBLE_DATA = {"latitude", "longitude", "temperature"};
    private final static String[] INTEGER_DATA = {"postal_code"};

    // 日時データのフォーマット
    public final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /* サーバからすべてのテーブルデータを取得 */
    public static ArrayList<ArrayList<Map<String, Object>>> getLocalDataBaseOption() {

        // すべてのローカルデータベーステーブルのデータを管理するためのArrayListオブジェクト
        ArrayList<ArrayList<Map<String, Object>>> localDataBaseTables = new ArrayList<ArrayList<Map<String,Object>>>();

        // テーブルごとにデータを取得
        for (String tableName: LOCAL_DATABASE_TABLES) {

            ArrayList<Map<String, Object>> tableData = getTableData(tableName);
            localDataBaseTables.add(tableData);

        }

        return localDataBaseTables;

    }

    /* サーバからローカル環境テーブルデータを取得 */
    public static ArrayList<Map<String, Object>> getEnvironmentTable() {

        // ローカル環境テーブルを管理するためのArrayListオブジェクト
        ArrayList<Map<String, Object>> environmentTable = getTableData(ENVIRONMENT_TABLE_NAME);
        return environmentTable;

    }

    /* サーバからローカルキャラクターテーブルデータを取得 */
    public static ArrayList<Map<String, Object>> getCharacterTable() {

        // ローカルキャラクターテーブルを管理するためのArrayListオブジェクト
        ArrayList<Map<String, Object>> characterTable = getTableData(CHARACTER_TABLE_NAME);
        return characterTable;

    }

    /* 特定のテーブルデータを取得 */
    private static ArrayList<Map<String, Object>> getTableData(String tableName) {
        // テーブルのJSON
        JSONArray jsonData = getJSON(tableName);

        // テーブルデータを管理するためのArrayListオブジェクト
        ArrayList<Map<String, Object>> tableData = new ArrayList<Map<String, Object>>();

        // すべてのレコードに対してJSONからMapオブジェクトへの変換
        // レコードごとに処理
        for (int i = 0; i < jsonData.length(); i++) {

            try {
                JSONObject jsonObject = jsonData.getJSONObject(i); // 1レコードを取り出す
                // 1レコードのデータを管理するためのMapオブジェクト
                Map<String, Object> recordData = new HashMap<>();
                try {
                    recordData = convertJsonToMap(jsonObject);
                } catch (Exception error) {
                    error.printStackTrace();
                }

                // レコードデータを管理するMapオブジェクトを
                // テーブルデータを管理するArrayListオブジェクトに追加
                tableData.add(recordData);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return tableData;
    }

    /* 特定のテーブルのJSONデータをサーバから取得 */
    private static JSONArray getJSON(final String tableName) {

        final JSONArray[] jsonData = new JSONArray[1];

        // HTTP通信を実行
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    URL url = new URL(API_URL + tableName); // URLの作成
                    // 接続用HttpURLConnectionオブジェクト作成
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    // 本文の取得
                    InputStream in = connection.getInputStream();
                    String readString = readInputStream(in);

                    /* サーバ側のバグ対処用コード↓ */
                    if (tableName.equals(ENVIRONMENT_TABLE_NAME)) {
                        jsonData[0] = new JSONObject(readString).getJSONArray(SPOT_TABLE_NAME);
                    }
                    /* サーバ側のバグ対処用コード↑ */
                    else {
                        jsonData[0] = new JSONObject(readString).getJSONArray(tableName);
                    }
                    in.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // 通信開始
        thread.start();

        // 通信が終わるまで待つ
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return jsonData[0];
    }

    /* サーバから受け取ったデータを解析 */
    private static String readInputStream(InputStream in) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        String string = "";

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        while ((string = bufferedReader.readLine()) != null) {
            stringBuffer.append(string); // 取得した文字をバッファに追加
        }
        try {
            in.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return stringBuffer.toString();
    }

    /* レコードのJSONをMapオブジェクトに変換するメソッド */
    private static Map<String, Object> convertJsonToMap(JSONObject recordJson) throws JSONException, ParseException {

        // 変換後のデータを入れるMapオブジェクト
        Map<String, Object> recordData = new HashMap<String, Object>();

        // キーごとに処理
        Iterator<String> keyItr = recordJson.keys();
        while(keyItr.hasNext()) {
            String key = keyItr.next();

            // キーに対応する要素の型によって取り出し方を変える
            if (Arrays.asList(INTEGER_DATA).contains(key)) { // INTEGER型の要素のキーのとき
                Integer data = recordJson.getInt(key); // INTEGER型として取り出す
                recordData.put(key, data); // Mapオブジェクトに追加

            } else if (Arrays.asList(DOUBLE_DATA).contains(key)) { // DOUBLE型の要素のキーのとき
                Double data = recordJson.getDouble(key); // DOUBLE型として取り出す
                recordData.put(key, data); // Mapオブジェクトに追加

            } else { // STRING型の要素のキーのとき
                String data = recordJson.getString(key); // STRING型として取り出す
                recordData.put(key, data); // Mapオブジェクトに追加
            }

        }

        return recordData;

    }

}



