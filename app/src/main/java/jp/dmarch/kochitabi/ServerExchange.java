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

    /* JSONをサーバから取得するメソッド */
    @SuppressLint("StaticFieldLeak")
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

                } catch (IOException | JSONException e) {
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

        // すべてのローカルデータベーステーブルのJSON
        JSONArray jsonData = getJSON(GET_LOCAL_DATABASE_TABLES_URL);
        // すべてのローカルデータベーステーブルのデータを管理するためのArrayListオブジェクト
        ArrayList<ArrayList<Map<String, Object>>> localDataBaseTables = new ArrayList<ArrayList<Map<String,Object>>>();

        // すべてのレコードに対してJSONからMapオブジェクトへの変換
        // テーブルごとに処置
        for (int i = 0; i < jsonData.length(); i++) {
            try {
                // 1つのテーブルのみを取り出し
                JSONArray jsonTableData = jsonData.getJSONArray(i);
                // テーブルデータ管理のArrayListオブジェクト
                ArrayList<Map<String, Object>> tableData = new ArrayList<Map<String, Object>>();

                // レコードごとに処理
                for (int j = 0; j < jsonTableData.length(); j++) {

                    JSONObject jsonObject = jsonTableData.getJSONObject(j); // 1レコードのみ取り出し
                    // 1レコードのデータを管理するためのMapオブジェクト
                    Map<String, Object> tableRecord = convertJsonToMap(jsonObject, TABLE_KEYS[i]);

                    // レコードデータ管理のMapオブジェクトを
                    // テーブルデータ管理のArrayListオブジェクトに追加
                    tableData.add(tableRecord);

                }

                // テーブルデータを管理するArrayListオブジェクトを
                // すべての観光地データを管理するArrayListオブジェクトに追加
                localDataBaseTables.add(tableData);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return localDataBaseTables;

    }

    /* サーバからローカル環境テーブルデータを取得 */
    public ArrayList<Map<String, Object>> getEnvironmentTable() {

        // ローカル環境テーブルのJSON
        JSONArray jsonData = getJSON(GET_ENVIRONMENT_TABLE_URL);
        // ローカル環境テーブルを管理するためのArrayListオブジェクト
        ArrayList<Map<String, Object>> environmentTable = new ArrayList<Map<String, Object>>();

        // すべてのレコードに対してJSONからMapオブジェクトへの変換
        // レコードごとに処理
        for (int i = 0; i < jsonData.length(); i++) {

            try {
                JSONObject jsonObject = jsonData.getJSONObject(i); // 1レコードを取り出す
                // 1レコードのデータを管理するためのMapオブジェクト
                Map<String, Object> environmentData = convertJsonToMap(jsonObject, TABLE_KEYS[ENVIRONMENT_TABLE]);

                // レコードデータを管理するMapオブジェクトを
                // テーブルデータを管理するArrayListオブジェクトに追加
                environmentTable.add(environmentData);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return environmentTable;

    }

    /* サーバからローカルキャラクターテーブルデータを取得 */
    public ArrayList<Map<String, Object>> getCharacterTable() {

        // ローカルキャラクターテーブルのJSON
        JSONArray jsonData = getJSON(GET_CHARACTER_TABLE_URL);
        // ローカルキャラクターテーブルを管理するためのArrayListオブジェクト
        ArrayList<Map<String, Object>> characterTable = new ArrayList<Map<String, Object>>();

        // すべてのレコードに対してJSONからMapオブジェクトへの変換
        // レコードごとに処理
        for (int i = 0; i < jsonData.length(); i++) {

            try {
                JSONObject jsonObject = jsonData.getJSONObject(i); // 1レコードを取り出す
                // 1レコードのデータを管理するためのMapオブジェクト
                Map<String, Object> characterData = convertJsonToMap(jsonObject, TABLE_KEYS[CHARACTER_TABLE]);

                // レコードデータを管理するMapオブジェクトを
                // テーブルデータを管理するArrayListオブジェクトに追加
                characterTable.add(characterData);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return characterTable;

    }


    /* レコードのJSONをMapオブジェクトに変換するメソッド */
    private Map<String, Object> convertJsonToMap(JSONObject recordJson, String[] keys) throws JSONException {

        // 変換後のデータを入れるMapオブジェクト
        Map<String, Object> recordData = new HashMap<String, Object>();

        // キーごとに処理
        for (String key : keys) {

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



