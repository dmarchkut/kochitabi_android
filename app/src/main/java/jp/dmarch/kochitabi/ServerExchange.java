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

        JSONArray jsonData = getJSON("http://dmarch.jp/get_local_database_tables.py");

        return localDataBaseTables;

    }

    /* サーバからローカル環境テーブルデータを取得 */
    public ArrayList<Map<String, Object>> getEnviornmentTable() {

        ArrayList<Map<String, Object>> environmentTable = new ArrayList<Map<String, Object>>();

        JSONArray jsonData = getJSON("http://dmarch.jp/get_local_environment_table.py");

        // すべてのレコードに対してJSONからMapオブジェクトへの変換
        for (int i = 0; i < jsonData.length(); i++) {

            try {
                // JSONデータからそれぞれの要素を取得
                JSONObject jsonObject = jsonData.getJSONObject(i);
                String environmentId = jsonObject.getString("environment_id");
                String weather = jsonObject.getString("weather");
                Double temperature = jsonObject.getDouble("temperature");
                Integer createAt = jsonObject.getInt("create_at");
                Integer updateAt = jsonObject.getInt("update_at");

                // Mapオブジェクトに入れる
                Map<String, Object> environmentData = new HashMap<String, Object>();
                environmentData.put("environment_id", environmentId);
                environmentData.put("weather", weather);
                environmentData.put("temperature", temperature);
                environmentData.put("create_at", createAt);
                environmentData.put("update_at", updateAt);

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

        JSONArray jsonData = getJSON("http://dmarch.jp/get_local_character_table.py");

        // すべてのレコードに対してJSONからMapオブジェクトへの変換
        for (int i = 0; i < jsonData.length(); i++) {

            try {
                // JSONデータからそれぞれの要素を取得
                JSONObject jsonObject = jsonData.getJSONObject(i);
                String accessPointId = jsonObject.getString("access_point_id");
                String characterName = jsonObject.getString("character_name");
                String characterFilePath = jsonObject.getString("character_file_path");
                Integer createAt = jsonObject.getInt("create_at");
                Integer updateAt = jsonObject.getInt("update_at");

                // Mapオブジェクトに入れる
                Map<String, Object> characterData = new HashMap<String, Object>();
                characterData.put("access_point_id", accessPointId);
                characterData.put("character_name", characterName);
                characterData.put("character_file_path", characterFilePath);
                characterData.put("create_at", createAt);
                characterData.put("update_at", updateAt);

                // データを追加
                characterTable.add(characterData);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return characterTable;

    }

}
