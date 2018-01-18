package jp.dmarch.kochitabi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
 * DBの確認方法
 * https://www.crunchtimer.jp/blog/technology/android/3743/
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    private Context context;
    private ServerExchange serverExchange;
    private LocationAcquisition locationAcquisition;
    private ArrayList<ArrayList<Map<String, Object>>> localDataBaseDatas;

    private final static String DB_NAME = "kochitabi_db";   // データベース名
    private final static int DB_VERSION = 1;    // データベースのバージョン
    private final static String SPOT_TABLE_NAME = "local_spots"; // ローカル観光地テーブルの名前
    private final static String ACCESS_POINT_TABLE_NAME = "local_access_points"; // ローカルアクセスポイントテーブルの名前
    private final static String ENVIRONMENT_TABLE_NAME = "local_environments"; // ローカル環境テーブルの名前
    private final static String CHARACTER_TABLE_NAME = "local_characters"; // ローカルキャラクターテーブルの名前

    private final static String spotKeys[] // ローカル観光地テーブルのレコードのキー
            = new String[] {"spot_id", "environment_id", "spot_name", "spot_phoname", "street_address", "postal_code",
                            "latitude", "longitude", "photo_file_path", "text_data", "created_at", "updated_at"};
    private final static String accessPointKeys[] // ローカルアクセスポイントテーブルのレコードのキー
            = new String[] {"access_point_id", "spot_id", "access_point_name", "latitude", "longitude",
            "raspberry_pi_number", "text_data", "created_at", "updated_at"};
    private final static String environmentKeys[] // ローカル環境テーブルのレコードのキー
            = new String[] {"environment_id", "weather", "temperature", "created_at", "updated_at"};
    private final static String characterKeys[] // ローカルキャラクターテーブルのレコードのキー
            = new String[] {"access_point_id", "character_name", "character_file_path",
                            "created_at", "updated_at"};

    // コンストラクタ
    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        serverExchange = new ServerExchange();
        locationAcquisition = new LocationAcquisition(context);
    }

    // データベースがなかったときに場合に実行される
    @Override
    public void onCreate(SQLiteDatabase db) {
        setLocalDataBaseOption(db); // ４つのテーブル作成
    }

    // データベースをアップグレードする場合に実行される
    // 現在のデータベースのバージョンよりsuperの第3引数で与えたバージョンのほうが高い時に実行される
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {}

    /* 4つのデータテーブルを取得し、ローカルDBに登録 */
    public void setRegisterData() {

        // サーバから4つのテーブルデータを取得
        ArrayList<ArrayList<Map<String, Object>>> localDataBaseTables = serverExchange.getLocalDataBaseTables();

        setSpotTable(localDataBaseTables.get(0)); // ローカル観光地テーブルにデータを登録
        setAccessPointTable(localDataBaseTables.get(1)); // ローカルアクセスポイントテーブルにデータを登録
        setEnvironmentTable(localDataBaseTables.get(2)); // ローカル環境テーブルにデータを登録
        setCharacterTable(localDataBaseTables.get(3)); // ローカルキャラクターテーブルにデータを登録
    }

    /* ローカルDBの設定を行う */
    private void setLocalDataBaseOption(SQLiteDatabase db){
        // ローカル観光地テーブルを作成
        db.execSQL("CREATE TABLE "+SPOT_TABLE_NAME+" ("
                +spotKeys[0]+" TEXT PRIMARY KEY NOT NULL,"
                +spotKeys[1]+" TEXT NOT NULL ,"
                +spotKeys[2]+" TEXT NOT NULL ,"
                +spotKeys[3]+" TEXT NOT NULL ,"
                +spotKeys[4]+" TEXT NOT NULL ,"
                +spotKeys[5]+" INTEGER NOT NULL ,"
                +spotKeys[6]+" REAL NOT NULL ,"
                +spotKeys[7]+" REAL NOT NULL ,"
                +spotKeys[8]+" TEXT NOT NULL ,"
                +spotKeys[9]+" TEXT NOT NULL ,"
                +spotKeys[10]+" TEXT NOT NULL ,"
                +spotKeys[11]+" TEXT NOT NULL"
                +");"
        );

        // ローカルアクセスポイントテーブルを作成
        db.execSQL("CREATE TABLE "+ACCESS_POINT_TABLE_NAME+" ("
                +accessPointKeys[0]+" TEXT PRIMARY KEY NOT NULL ,"
                +accessPointKeys[1]+" TEXT NOT NULL ,"
                +accessPointKeys[2]+" TEXT NOT NULL ,"
                +accessPointKeys[3]+" REAL NOT NULL ,"
                +accessPointKeys[4]+" REAL NOT NULL ,"
                +accessPointKeys[5]+" TEXT NOT NULL ,"
                +accessPointKeys[6]+" TEXT NOT NULL ,"
                +accessPointKeys[7]+" TEXT NOT NULL ,"
                +accessPointKeys[8]+" TEXT NOT NULL"
                +");"
        );

        // ローカル環境テーブルを作成
        db.execSQL("CREATE TABLE "+ENVIRONMENT_TABLE_NAME+" ("
                +environmentKeys[0]+" TEXT PRIMARY KEY NOT NULL ,"
                +environmentKeys[1]+" TEXT ,"
                +environmentKeys[2]+" REAL ,"
                +environmentKeys[3]+" TEXT NOT NULL ,"
                +environmentKeys[4]+" TEXT NOT NULL"
                +");"
        );

        // ローカルキャラクターテーブルを作成
        db.execSQL("CREATE TABLE "+CHARACTER_TABLE_NAME+" ("
                +characterKeys[0]+" TEXT PRIMARY KEY NOT NULL ,"
                +characterKeys[1]+" TEXT NOT NULL ,"
                +characterKeys[2]+" TEXT NOT NULL ,"
                +characterKeys[3]+" TEXT NOT NULL ,"
                +characterKeys[4]+" TEXT NOT NULL"
                +");"
        );

    }

    /* ローカルDBのローカル観光地テーブルを登録 */
    private void setSpotTable(ArrayList<Map<String, Object>> spotTable) {

        SQLiteDatabase db = this.getWritableDatabase(); // データベースと接続

        ContentValues insertValues; // 追加するデータ
        Boolean insertFlag; // 追加しても良いレコードか否か

        for (Map<String, Object> spotData: spotTable) {

            // 1つのレコードの追加準備
            insertValues = new ContentValues();
            insertFlag = true;

            for (String key: spotData.keySet()) {

                Object obj = spotData.get(key);
                // Object型からそれぞれの型に変換し、追加データとして設定
                if (obj instanceof String) insertValues.put(key, (String)obj);
                else if (obj instanceof Double) insertValues.put(key, (Double)obj);
                else if (obj instanceof Integer) insertValues.put(key, (Integer)obj);
                else {
                    insertFlag = false; // 追加してはいけないデータとして設定
                    break;
                }
            }

            // すべてのキーの要素を設定できたか確認
            for (String key: spotKeys) {
                if (!(insertValues.containsKey(key))) {
                    insertFlag = false;
                    break;
                }
            }

            // キーの要素数が適切か確認
            if (insertValues.size() != spotKeys.length) insertFlag = false;

            // レコードの追加
            if (insertFlag) {
                try {
                    db.insert(SPOT_TABLE_NAME, null, insertValues);
                }
                catch (Error error) {}
            }
        }
        db.close(); // データベースと接続
    }

    /* ローカルDBのローカルアクセスポイントテーブルを登録 */
    private void setAccessPointTable(ArrayList<Map<String, Object>> accessPointTable) {

        SQLiteDatabase db = this.getWritableDatabase(); // データベースと接続

        ContentValues insertValues; // 追加するデータ
        Boolean insertFlag; // 追加しても良いレコードか否か

        for (Map<String, Object> accessPointData: accessPointTable) {

            // 1つのレコードの追加準備
            insertValues = new ContentValues();
            insertFlag = true;

            for (String key: accessPointData.keySet()) {
                Object obj = accessPointData.get(key);
                // Object型からそれぞれの型に変換し、追加データとして設定
                if (obj instanceof String) insertValues.put(key, (String)obj);
                else if (obj instanceof Double) insertValues.put(key, (Double)obj);
                else if (obj instanceof Integer) insertValues.put(key, (Integer)obj);
                else {
                    insertFlag = false; // 追加してはいけないデータとして設定
                    break;
                }
            }

            // すべてのキーの要素を設定できたか確認
            for (String key: accessPointKeys) {
                if (!(insertValues.containsKey(key))) insertFlag = false;
            }

            // キーの要素数が適切か確認
            if (insertValues.size() != accessPointKeys.length) insertFlag = false;

            // レコードの追加
            if (insertFlag) {
                try {
                    db.insert(ACCESS_POINT_TABLE_NAME, null, insertValues);
                }
                catch (Error error) {}
            }
        }
        db.close(); // データベースと接続
    }

    /* ローカルDBのローカル環境テーブルを登録 */
    private void setEnvironmentTable(ArrayList<Map<String, Object>> environmentTable) {

        SQLiteDatabase db = this.getWritableDatabase(); // データベースと接続

        ContentValues insertValues; // 追加するデータ
        Boolean insertFlag; // 追加しても良いレコードか否か

        for (Map<String, Object> environmentData: environmentTable) {

            // 1つのレコードの追加準備
            insertValues = new ContentValues();
            insertFlag = true;

            for (String key: environmentData.keySet()) {
                Object obj = environmentData.get(key);
                // Object型からそれぞれの型に変換し、追加データとして設定
                if (obj instanceof String) insertValues.put(key, (String)obj);
                else if (obj instanceof Double) insertValues.put(key, (Double)obj);
                else if (obj instanceof Integer) insertValues.put(key, (Integer)obj);
                else {
                    insertFlag = false; // 追加してはいけないデータとして設定
                    break;
                }
            }

            // すべてのキーの要素を設定できたか確認
            for (String key: environmentKeys) {
                if (!(insertValues.containsKey(key))) insertFlag = false;
            }

            // キーの要素数が適切か確認
            if (insertValues.size() != environmentKeys.length) insertFlag = false;

            // レコードの追加
            if (insertFlag) {
                try {
                    db.insert(ENVIRONMENT_TABLE_NAME, null, insertValues);
                }
                catch (Error error) {}
            }
        }
        db.close(); // データベースと接続
    }

    /* ローカルDBのローカルキャラクターテーブルを登録 */
    private void setCharacterTable(ArrayList<Map<String, Object>> characterTable) {

        SQLiteDatabase db = this.getWritableDatabase(); // データベースと接続

        ContentValues insertValues; // 追加するデータ
        Boolean insertFlag; // 追加しても良いレコードか否か

        for (Map<String, Object> characterData: characterTable) {

            // 1つのレコードの追加準備
            insertValues = new ContentValues();
            insertFlag = true;

            for (String key: characterData.keySet()) {
                Object obj = characterData.get(key);
                // Object型からそれぞれの型に変換し、追加データとして設定
                if (obj instanceof String) insertValues.put(key, (String)obj);
                else if (obj instanceof Integer) insertValues.put(key, (Integer)obj);
                else {
                    insertFlag = false; // 追加してはいけないデータとして設定
                    break;
                }
            }

            // すべてのキーの要素を設定できたか確認
            for (String key: characterKeys) {
                if (!(insertValues.containsKey(key))) insertFlag = false;
            }

            // キーの要素数が適切か確認
            if (insertValues.size() != characterKeys.length) insertFlag = false;

            // レコードの追加
            if (insertFlag) {
                try {
                    db.insert(CHARACTER_TABLE_NAME, null, insertValues);
                }
                catch (Error error) {}
            }
        }
        db.close(); // データベースと接続
    }

    /* ローカルDBのローカル環境テーブルを更新 */
    private void updateEnvironmentTable(ArrayList<Map<String, Object>> environmentTable) {

        SQLiteDatabase db = this.getWritableDatabase(); // データベースと接続

        ContentValues insertValues; // 更新するデータ
        Boolean insertFlag; // 更新しても良いレコードか否か

        for (Map<String, Object> environmentData: environmentTable) {

            // 1つのレコードの更新準備
            insertValues = new ContentValues();
            insertFlag = true;

            for (String key: environmentData.keySet()) {
                Object obj = environmentData.get(key);
                // Object型からそれぞれの型に変換し、追加データとして設定
                if (obj instanceof String) insertValues.put(key, (String)obj);
                else if (obj instanceof Integer) insertValues.put(key, (Integer)obj);
                else {
                    insertFlag = false; // 追加してはいけないデータとして設定
                    break;
                }
            }

            // すべてのキーの要素を設定できたか確認
            for (String key: environmentKeys) {
                if (!(insertValues.containsKey(key))) insertFlag = false;
                break;
            }

            // キーの要素数が適切か確認
            if (insertValues.size() != environmentKeys.length) insertFlag = false;

            // レコードの追加
            if (insertFlag) {
                try {
                    db.replace(ENVIRONMENT_TABLE_NAME,
                            null,
                            insertValues);
                }
                catch (Error error) {
                    error.printStackTrace();
                }

            }
        }
        db.close(); // データベースと接続

    }

    /* ローカルDBのローカルキャラクターテーブルを更新 */
    private void updateCharacterTable(ArrayList<Map<String, Object>> characterTable) {

        SQLiteDatabase db = this.getWritableDatabase(); // データベースと接続

        ContentValues insertValues; // 更新するデータ
        Boolean insertFlag; // 更新しても良いレコードか否か

        for (Map<String, Object> characterData: characterTable) {

            // 1つのレコードの更新準備
            insertValues = new ContentValues();
            insertFlag = true;

            for (String key: characterData.keySet()) {
                Object obj = characterData.get(key);
                // Object型からそれぞれの型に変換し、追加データとして設定
                if (obj instanceof String) insertValues.put(key, (String)obj);
                else if (obj instanceof Integer) insertValues.put(key, (Integer)obj);
                else {
                    insertFlag = false; // 追加してはいけないデータとして設定
                    break;
                }
            }

            // すべてのキーの要素を設定できたか確認
            for (String key: characterKeys) {
                if (!(insertValues.containsKey(key))) insertFlag = false;
                break;
            }

            // キーの要素数が適切か確認
            if (insertValues.size() != characterKeys.length) insertFlag = false;

            // レコードの追加
            if (insertFlag) {
                try {
                    db.replace(CHARACTER_TABLE_NAME,
                            null,
                            insertValues);
                }
                catch (Error error) {
                    error.printStackTrace();
                }

            }
        }
        db.close(); // データベースと接続
    }

    /* ローカル観光地テーブルからローカル観光地データ取得 */
    public ArrayList<Map<String, Object>> getSpotsData() {

        ArrayList<Map<String, Object>> spotsData = new ArrayList<Map<String, Object>>();
        SQLiteDatabase db = this.getWritableDatabase(); // DBへ接続

        try {
            // ローカル観光地テーブルから観光地データをすべて取得
            Cursor cursor = db.query(   SPOT_TABLE_NAME,
                                        new String[] {"spot_id", "environment_id", "spot_name", "spot_phoname", "street_address", "postal_code", "latitude", "longitude", "photo_file_path"},
                                        null,
                                        null,
                                        null, null, null
                                    );

            // 取得した数が0個であれば終了
            if (cursor.getCount() == 0) {
                cursor.close();
                return null;
            }

            boolean isEof = cursor.moveToFirst(); // カーソルを一番最初に持ってくる

            // 観光地データを追加
            while(isEof) {
                Map<String, Object> spotData = new HashMap<String, Object>();
                spotData.put("spot_id", cursor.getString(cursor.getColumnIndex("spot_id")));
                spotData.put("environment_id", cursor.getString(cursor.getColumnIndex("environment_id")));
                spotData.put("spot_name", cursor.getString(cursor.getColumnIndex("spot_name")));
                spotData.put("spot_phoname", cursor.getString(cursor.getColumnIndex("spot_phoname")));
                spotData.put("street_address", cursor.getString(cursor.getColumnIndex("street_address")));
                spotData.put("postal_code", cursor.getInt(cursor.getColumnIndex("postal_code")));
                spotData.put("latitude", cursor.getDouble(cursor.getColumnIndex("latitude")));
                spotData.put("longitude", cursor.getDouble(cursor.getColumnIndex("longitude")));
                spotData.put("photo_file_path", cursor.getString(cursor.getColumnIndex("photo_file_path")));
                spotsData.add(spotData);
                Log.d("getSpot", "add");

                isEof = cursor.moveToNext();
            }
            cursor.close();
        }
        finally {
            db.close(); // DBを切断
        }

        return  spotsData;
    }

    /* ローカル観光地テーブルからローカルテキストデータ取得 */
    public String getSpotText(String spotId) {

        String spotText;

        SQLiteDatabase db = this.getWritableDatabase(); // DBへ接続

        try {
            // ローカル観光地テーブルから特定の観光地テキストデータを取得
            Cursor cursor = db.query(   SPOT_TABLE_NAME,
                                        new String[] {"spot_id", "text_data"},
                                        "spot_id == ?",
                                        new String[] {spotId},
                                        null, null, null
            );

            // 取得した数が0個であれば終了
            if (cursor.getCount() == 0) {
                cursor.close();
                return null;
            }

            cursor.moveToFirst(); // カーソルを一番最初に持ってくる
            // 観光地テキストデータを取得
            spotText = cursor.getString(cursor.getColumnIndex("text_data"));
            cursor.close();
        }
        finally {
            db.close(); // DBを切断
        }

        return spotText;
    }

    /* ローカルアクセスポイントテーブルから特定の観光地内のAPの座標を取得 */
    public ArrayList<Double[]> getAccessPointLocations(String spotId) {

        ArrayList<Double[]> accessPointLocations = new ArrayList<Double[]>();

        SQLiteDatabase db = this.getWritableDatabase(); // DBへ接続

        try {
            // ローカルアクセスポイントテーブルから特定のアクセスポイントデータをすべて取得
            Cursor cursor = db.query(   ACCESS_POINT_TABLE_NAME,
                                        new String[]{"access_point_id", "spot_id", "latitude", "longitude"},
                                        "spot_id == ?",
                                        new String[] {spotId},
                                        null, null, null
            );

            // 取得した数が0個であれば終了
            if (cursor.getCount() == 0) {
                cursor.close();
                return null;
            }

            Boolean isEof = cursor.moveToFirst(); // カーソルを一番最初に持ってくる
            // 観光地データを追加
            while(isEof) {
                Double[] accessPointLocation = new Double[2];
                accessPointLocation[0] = cursor.getDouble(cursor.getColumnIndex("latitude"));
                accessPointLocation[1] = cursor.getDouble(cursor.getColumnIndex("longitude"));
                accessPointLocations.add(accessPointLocation);

                isEof = cursor.moveToNext();
            }
            cursor.close();
        }
        finally {
            db.close(); // DBを切断
        }

        return accessPointLocations;
    }

    /* ローカルアクセスポイントテーブルから特定のアクセスポイントの案内情報を取得 */
    public Map<String, Object> getAccessPointGuide(String raspberrypiNumber) {

        Map<String, Object> accessPointGuide = new HashMap<String, Object>();
        SQLiteDatabase db = this.getWritableDatabase(); // DBへ接続

        try {
            // ローカルアクセスポイントテーブルから特定のアクセスポイントの案内情報を取得
            Cursor cursor = db.query(   ACCESS_POINT_TABLE_NAME,
                                        new String[] {"access_point_id", "raspberry_pi_number", "text_data"},
                                        "raspberry_pi_number == ?",
                                        new String[] {raspberrypiNumber},
                                        null, null, null
            );

            // 取得した数が0個であれば終了
            if (cursor.getCount() == 0) {
                cursor.close();
                return null;
            }

            cursor.moveToFirst(); // カーソルを一番最初に持ってくる

            accessPointGuide.put("access_point_id", cursor.getString(cursor.getColumnIndex("access_point_id")));
            accessPointGuide.put("text_data", cursor.getString(cursor.getColumnIndex("text_data")));

            cursor.close();
        }
        finally {
            db.close(); // DBを切断
        }

        return accessPointGuide;
    }

    /* ローカル環境テーブルの特定の環境データを取得 */
    public Map<String, Object> getEnvironmentData(String environmentId) {

        // 現在のローカル環境テーブルのデータが10秒以上たっていれば
        // サーバからデータを取得する
        if (isEnvironmentTableTime()) {
            ArrayList<Map<String, Object>> environmentTableData = serverExchange.getEnvironmentTable();
            updateEnvironmentTable(environmentTableData);
        }

        Map<String, Object> environmentData = new HashMap<String, Object>();
        SQLiteDatabase db = this.getWritableDatabase(); // DBへ接続

        try {
            // ローカル環境テーブルの特定の環境データを取得
            Cursor cursor = db.query(   ENVIRONMENT_TABLE_NAME,
                    new String[] {"environment_id", "weather", "temperature"},
                    "environment_id == ?",
                    new String[] {environmentId},
                    null, null, null
            );

            // 取得した数が0個であれば終了
            if (cursor.getCount() == 0) {
                cursor.close();
                return null;
            }

            cursor.moveToFirst(); // カーソルを一番最初に持ってくる

            environmentData.put("environment_id", cursor.getString(cursor.getColumnIndex("environment_id")));
            environmentData.put("weather", cursor.getString(cursor.getColumnIndex("weather")));
            environmentData.put("temperature", cursor.getDouble(cursor.getColumnIndex("temperature")));

            cursor.close();
        }
        finally {
            db.close(); // DBを切断
        }

        return environmentData;
    }

    /* ローカルキャラクターテーブルから特定のキャラクターデータを取得 */
    public Map<String, Object> getCharacterData(String accessPointId) {

        // 現在のローカルキャラクターテーブルのデータが10秒以上たっていれば
        // サーバからデータを取得する
        if (isCharacterTableTime()) {
            ArrayList<Map<String, Object>> characterTableData = serverExchange.getCharacterTable();
            updateCharacterTable(characterTableData);
        }

        Map<String, Object> characterData = new HashMap<String, Object>();
        SQLiteDatabase db = this.getWritableDatabase(); // DBへ接続

        try {
            // ローカルキャラクターテーブルから特定のキャラクターデータを取得
            Cursor cursor = db.query(   CHARACTER_TABLE_NAME,
                                        new String[] {"access_point_id", "character_name", "character_file_path"},
                                        "access_point_id == ?",
                                        new String[] {accessPointId},
                                        null, null, null
            );

            // 取得した数が0個であれば終了
            if (cursor.getCount() == 0) {
                cursor.close();
                return null;
            }

            cursor.moveToFirst(); // カーソルを一番最初に持ってくる

            characterData.put("access_point_id", cursor.getString(cursor.getColumnIndex("access_point_id")));
            characterData.put("character_name", cursor.getString(cursor.getColumnIndex("character_name")));
            characterData.put("character_file_path", cursor.getString(cursor.getColumnIndex("character_file_path")));

            cursor.close();
        }
        finally {
            db.close(); // DBを切断
        }

        return characterData;
    }

    /* 天気・気温、距離情報を付加した観光地データを取得 */
    public ArrayList<Map<String, Object>> getSpotsEnvironmentDistanceData(Double[] currentLocation) {

        ArrayList<Map<String, Object>> spotsEnvironmentDistanceData = new ArrayList<Map<String, Object>>();

        ArrayList<Map<String, Object>> spotsData = getSpotsData(); // 全ての観光地データを取得

        // 1つの観光地データに対して処理
        for(Map<String, Object> spotData: spotsData) {

            // 観光地データの環境IDに対応した環境データを取得
            Map<String, Object> environmentData = getEnvironmentData((String)spotData.get("environment_id"));

            // 観光地の座標データを取得
            Double[] spotLocation = new Double[2];
            spotLocation[0] = (Double)spotData.get("latitude");
            spotLocation[1] = (Double)spotData.get("longitude");

            // 座標データが多いまたは少ない場合nullを返す
            if ((currentLocation.length != 2) || (currentLocation[0] == null) || (currentLocation[1] == null)) return null;

            // 現在地と観光地の距離を計算
            Double distance = locationAcquisition.getDistance(currentLocation, spotLocation);

            // 距離が計算できていなければnullを返す
            if (distance == null) return null;

            // 観光地データ、環境データ、距離を結合
            Map<String, Object> spotEnvironmentDistanceData = addSpotEnvironmentDistanceData(spotData, environmentData, distance);

            // 処理済み観光地データに追加
            spotsEnvironmentDistanceData.add(spotEnvironmentDistanceData);
        }

        return spotsEnvironmentDistanceData;
    }

    /* 案内テキストデータを付加したキャラクターデータを取得 */
    public Map<String, Object> getCharacterGuideData(String raspberrypiNumber) {

        Map<String, Object> characterGuideData;

        // RaspberryPi識別番号に対応したアクセスポイント案内データを取得
        Map<String, Object> accessPointGuideData = getAccessPointGuide(raspberrypiNumber);

        // アクセスポイントIDに対応したキャラクターデータを取得
        Map<String, Object> characterData = getCharacterData((String)accessPointGuideData.get("access_point_id"));

        // キャラクターデータと案内テキストを結合
        String guideText = (String)accessPointGuideData.get("text_data");
        characterGuideData = addCharacterGuideData(characterData, guideText);

        return characterGuideData;
    }

    /* ローカル環境テーブルの更新の可否を判定 */
    private Boolean isEnvironmentTableTime() {

        Boolean timeCourse = false;

        SQLiteDatabase db = this.getWritableDatabase(); // DBへ接続

        try {
            // ローカル環境テーブルの環境データを1つ取得
            Cursor cursor = db.query(   ENVIRONMENT_TABLE_NAME,
                                        new String[] {"environment_id", "created_at", "updated_at"},
                                        null,
                                        null,
                                        null, null, "environment_id DESC",
                                        "1"
            );

            // 取得した数が0個であれば取得
            if (cursor.getCount() == 0) {
                cursor.close();
                return true;
            }

            cursor.moveToFirst(); // カーソルを一番最初に持ってくる
            // ローカル環境テーブルのレコードの更新時間を取得
            SimpleDateFormat format = new SimpleDateFormat(ServerExchange.DATE_FORMAT);
            Calendar updateTime = Calendar.getInstance();
            Date date = format.parse(cursor.getString(cursor.getColumnIndex("updated_at")));
            updateTime.setTimeInMillis(date.getTime());
            cursor.close();

            // 現在時間を取得
            Calendar nowTime = Calendar.getInstance();
            nowTime.setTimeInMillis(System.currentTimeMillis());

            updateTime.add(Calendar.MINUTE, 10);// 更新時間に10分足す
            // (now >= update+10分なら更新)
            if (nowTime.compareTo(updateTime) >= 0) timeCourse = true;

        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            db.close(); // DBを切断
        }

        return timeCourse;
    }

    /* ローカルキャラクターテーブルの更新の可否を判定 */
    private Boolean isCharacterTableTime() {

        Boolean timeCourse = false;

        SQLiteDatabase db = this.getWritableDatabase(); // DBへ接続

        try {
            // ローカルキャラクターテーブルの環境データを1つ取得
            Cursor cursor = db.query(   CHARACTER_TABLE_NAME,
                    new String[] {"access_point_id", "created_at", "updated_at"},
                    null,
                    null,
                    null, null, "access_point_id DESC",
                    "1"
            );

            // 取得した数が0個であれば取得
            if (cursor.getCount() == 0) {
                cursor.close();
                return null;
            }

            cursor.moveToFirst(); // カーソルを一番最初に持ってくる

            // ローカルキャラクターテーブルのレコードの更新時間を取得
            SimpleDateFormat format = new SimpleDateFormat(ServerExchange.DATE_FORMAT);
            Calendar updateTime = Calendar.getInstance();
            Date date = format.parse(cursor.getString(cursor.getColumnIndex("updated_at")));
            updateTime.setTimeInMillis(date.getTime());
            cursor.close();

            // 現在時間を取得
            Calendar nowTime = Calendar.getInstance();
            nowTime.setTimeInMillis(System.currentTimeMillis());

            updateTime.add(Calendar.MINUTE, 10);// 更新時間に10分足す
            // (now >= update+10分なら更新)
            if (nowTime.compareTo(updateTime) >= 0) timeCourse = true;

        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            db.close(); // DBを切断
        }

        return timeCourse;
    }

    /* 観光地データ、環境データの天気・気温情報、距離情報を結合 */
    private Map<String, Object> addSpotEnvironmentDistanceData(Map<String, Object> spotData, Map<String, Object> environmentData, Double distance) {

        // 観光地データに観光地IDがなければ(違うデータを第一引数に渡しているなら)nullを返す
        if (!(spotData.containsKey("spot_id"))) return null;

        // 環境データに天気と気温がなければ（違うデータを第二引数に渡しているなら）nullを返す
        if (!(environmentData.containsKey("weather")) && !(environmentData.containsKey("temperature"))) return null;

        // 観光地データと環境データの環境IDが一致していなければnullを返す
        if (!(spotData.get("environment_id").equals(environmentData.get("environment_id")))) return null;

        spotData.put("weather", environmentData.get("weather")); // 天気データを追加
        spotData.put("temperature", environmentData.get("temperature")); // 気温データを追加
        spotData.put("distance", distance); // 距離データを追加

        return spotData;
    }

    /* キャラクターデータとアクセスポイント案内データの案内テキスト情報を結合 */
    private Map<String, Object> addCharacterGuideData(Map<String, Object> characterData, String guideText) {

        // キャラクターデータにキャラクター名がなければ(違うデータを第一引数に渡しているなら)nullを返す
        if (!(characterData.containsKey("character_name"))) return null;

        characterData.put("text_data", guideText);
        return characterData;
    }

    /* ローカルDBのデータをすべて削除 */
    public void deleteRegisterData() {

        SQLiteDatabase db = this.getWritableDatabase(); // データベースと接続

        try {
            db.deleteDatabase(context.getDatabasePath(this.getDatabaseName()));
        }
        catch(Error error){}

        db.close();
    }
}