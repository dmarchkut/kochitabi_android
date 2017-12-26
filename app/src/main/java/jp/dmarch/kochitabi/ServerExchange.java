package jp.dmarch.kochitabi;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

    // ローカルDBのデータすべてを取得するためのAPIのURL
    private final static String GET_LOCAL_DATABASE_TABLES_URL = "http://dmarch.jp/get_local_database_tables.py";
    // ローカル環境テーブルのデータを取得するためのAPIのURL
    private final static String GET_ENVIRONMENT_TABLE_URL = "http://dmarch.jp/get_local_environment_table.py";
    // ローカルキャラクターテーブルのデータを取得するためのAPIのURL
    private final static String GET_CHARACTER_TABLE_URL = "http://dmarch.jp/get_local_character_table.py";

    // テーブルのキー(観光地、環境、アクセスポイント、キャラクター)
    private final static String[][] TABLE_KEYS = {
            new String[] {  "spot_id", "environment_id", "spot_name", "spot_phoname", "street_address", "postal_code",
                            "latitude", "longitude", "photo_file_path", "text_data", "created_at", "updated_at"},
            new String[] {  "environment_id", "weather", "temperature", "created_at", "updated_at"},
            new String[] {  "access_point_id", "spot_id", "access_point_name", "latitude", "longitude",
                            "raspberry_pi_number", "text_data", "created_at", "updated_at"},
            new String[] {  "access_point_id", "character_name", "character_file_path", "text_data", "created_at", "updated_at"}
    };

    // キーに対応するデータの種類
    private final static String[] DOUBLE_DATA = {"latitude", "longitude", "temperature"};
    private final static String[] INTEGER_DATA = {"created_at", "updated_at", "postal_code"};

    // 各テーブルの添え字
    private final static int SPOT_TABLE = 0;
    private final static int ENVIRONMENT_TABLE = 1;
    private final static int ACCESS_POINT_TABLE = 2;
    private final static int CHARACTER_TABLE = 3;

    /* JSONを取得 */
    private String readInputStream(InputStream in) throws IOException, UnsupportedEncodingException {
        StringBuffer stringBuffer = new StringBuffer();
        String string = "";

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        while ((string = bufferedReader.readLine()) != null) {
            stringBuffer.append(string);
        }
        try {
            in.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return stringBuffer.toString();
    }

    private JSONArray getJSON(final String urlString) {

        final JSONArray[] json = new JSONArray[0];

        // HTTP通信を非同期処理で実行
        new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected JSONArray doInBackground(Void... params) {

                try {
                    URL url = new URL(urlString); // URLの作成
                    // 接続用HttpURLConnectionオブジェクト作成
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    // リクエストメソッドの設定
                    connection.setRequestMethod("POST");
                    // リダイレクトを自動で許可しない設定
                    connection.setInstanceFollowRedirects(false);
                    // URL接続からデータを読み取る場合はtrue
                    connection.setDoInput(true);
                    // URL接続にデータを書き込む場合はtrue
                    connection.setDoOutput(true);

                    connection.connect(); // 通信開始

                    // 本文の取得(setDoInput(true)にする必要あり)
                    InputStream in = connection.getInputStream();
                    String readString = readInputStream(in);
                    JSONArray jsonData = new JSONObject(readString).getJSONArray("オブジェクト名");
                    in.close();

                    json[0] = jsonData;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(JSONArray jsonData) {
                json[0] = jsonData;
            }
        }.execute(); // 別スレッドで実行

        return json[0];

    }

    /* サーバからすべてのテーブルデータを取得 */
    public ArrayList<ArrayList<Map<String, Object>>> getLocalDataBaseOption() {

        ArrayList<ArrayList<Map<String, Object>>> localDataBaseTables = new ArrayList<ArrayList<Map<String,Object>>>();

        JSONArray jsonData = getJSON(GET_LOCAL_DATABASE_TABLES_URL);

        for (int i = 0; i < jsonData.length(); i++) {
            try {
                JSONArray jsonTableData = jsonData.getJSONArray(i);
                ArrayList<Map<String, Object>> tableData = new ArrayList<Map<String, Object>>();

                for (int j = 0; j < jsonTableData.length(); j++) {
                    JSONObject jsonObject = jsonTableData.getJSONObject(j);

                    Map<String, Object> tableRecord = new HashMap<String, Object>();

                    for (String key : TABLE_KEYS[i]) {

                        if (Arrays.asList(INTEGER_DATA).contains(key)) {
                            Integer data = jsonObject.getInt(key);
                            tableRecord.put(key, data);
                        } else if (Arrays.asList(DOUBLE_DATA).contains(key)) {
                            Double data = jsonObject.getDouble(key);
                            tableRecord.put(key, data);
                        } else {
                            String data = jsonObject.getString(key);
                            tableRecord.put(key, data);
                        }

                    }

                    // データを追加
                    tableData.add(tableRecord);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return localDataBaseTables;

    }

    /* サーバからローカル環境テーブルデータを取得 */
    public ArrayList<Map<String, Object>> getEnvironmentTable() {

        ArrayList<Map<String, Object>> environmentTable = new ArrayList<Map<String, Object>>();

        JSONArray jsonData = getJSON(GET_ENVIRONMENT_TABLE_URL);

        // すべてのレコードに対してJSONからMapオブジェクトへの変換
        for (int i = 0; i < jsonData.length(); i++) {

            try {
                // JSONデータからそれぞれの要素を取得
                JSONObject jsonObject = jsonData.getJSONObject(i);
                Map<String, Object> environmentData = new HashMap<String, Object>();

                for (String key : TABLE_KEYS[ENVIRONMENT_TABLE]) {

                    if (Arrays.asList(INTEGER_DATA).contains(key)) {
                        Integer data = jsonObject.getInt(key);
                        environmentData.put(key, data);
                    } else if (Arrays.asList(DOUBLE_DATA).contains(key)) {
                        Double data = jsonObject.getDouble(key);
                        environmentData.put(key, data);
                    } else {
                        String data = jsonObject.getString(key);
                        environmentData.put(key, data);
                    }

                }

                // データを追加
                environmentTable.add(environmentData);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return environmentTable;

    }

    /* サーバからローカルキャラクターテーブルデータを取得 */
    public ArrayList<Map<String, Object>> getCharacterTable() {

        ArrayList<Map<String, Object>> characterTable = new ArrayList<Map<String, Object>>();

        JSONArray jsonData = getJSON(GET_CHARACTER_TABLE_URL);

        // すべてのレコードに対してJSONからMapオブジェクトへの変換
        for (int i = 0; i < jsonData.length(); i++) {

            try {
                // JSONデータからそれぞれの要素を取得
                JSONObject jsonObject = jsonData.getJSONObject(i);
                Map<String, Object> characterData = new HashMap<String, Object>();

                for (String key : TABLE_KEYS[CHARACTER_TABLE]) {

                    if (Arrays.asList(INTEGER_DATA).contains(key)) {
                        Integer data = jsonObject.getInt(key);
                        characterData.put(key, data);
                    } else if (Arrays.asList(DOUBLE_DATA).contains(key)) {
                        Double data = jsonObject.getDouble(key);
                        characterData.put(key, data);
                    } else {
                        String data = jsonObject.getString(key);
                        characterData.put(key, data);
                    }

                }

                    // データを追加
                characterTable.add(characterData);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return characterTable;

    }

}
