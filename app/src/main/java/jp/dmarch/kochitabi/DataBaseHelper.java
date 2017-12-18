package jp.dmarch.kochitabi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DataBaseHelper extends SQLiteOpenHelper{
    private Context context;
    private ServerExchange serverExchange;
    private SQLiteDatabase db;

    private final static String DB_NAME = "kochitabi_db";   // データベース名
    private final static int DB_VERSION = 1;    // データベースのバージョン
    private final static String SPOT_TABLE_NAME = "local_spots"; // ローカル観光地テーブルの名前
    private final static String ACCESS_POINT_TABLE_NAME = "local_access_points"; // ローカルアクセスポイントテーブルの名前
    private final static String ENVIRONMENT_TABLE_NAME = "local_environments"; // ローカル環境テーブルの名前
    private final static String CHARACTER_TABLE_NAME = "local_characters"; // ローカルキャラクターテーブルの名前

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        serverExchange = new ServerExchange();
    }

    // データベースがなかったときに場合に実行される
    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        setRegisterData();
    }

    // データベースをアップグレードする場合に実行される
    // 現在のデータベースのバージョンよりsuperの第3引数で与えたバージョンのほうが高い時に実行される
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {}

    /* 4つのデータテーブルを取得し、ローカルDBに登録 */
    void setRegisterData() {
        setLocalDataBaseOption(); // ４つのテーブル作成
        // サーバから4つのテーブルデータを取得
        ArrayList<ArrayList<Map<String, Object>>> localDataBaseTables = serverExchange.getLocalDataBaseTables();
        setSpotTable(localDataBaseTables[0]); // ローカル観光地テーブルにデータを登録
        setAccessPointTable(localDataBaseTables[1]); // ローカルアクセスポイントテーブルにデータを登録
        setEnvironmentTable(localDataBaseTables[2]); // ローカル環境テーブルにデータを登録
        setCharacterTable(localDataBaseTables[3]); // ローカルキャラクターテーブルにデータを登録
    }

    /* ローカルDBの設定を行う */
    public void setLocalDataBaseOption(){
        // ローカル観光地テーブルを作成
        db.execSQL("CREATE TABLE "+ SPOT_TABLE_NAME +" ("
                +"spot_id TEXT PRIMARY KEY NOT NULL,"
                +"environment_id TEXT NOT NULL ,"
                +"spot_name TEXT NOT NULL ,"
                +"spot_phoname TEXT NOT NULL ,"
                +"street_address TEXT NOT NULL ,"
                +"latitude REAL NOT NULL ,"
                +"longitude REAL NOT NULL ,"
                +"photo_file_path TEXT NOT NULL ,"
                +"text_data TEXT NOT NULL ,"
                +"created_at INTEGER NOT NULL ,"
                +"updated_at INTEGER NOT NULL"
                +");"
        );

        // ローカルアクセスポイントテーブルを作成
        db.execSQL("CREATE TABLE "+ACCESS_POINT_TABLE_NAME+" ("
                +"access_point_id TEXT PRIMARY KEY NOT NULL ,"
                +"spot_id TEXT NOT NULL ,"
                +"access_point_name TEXT NOT NULL ,"
                +"latitude REAL NOT NULL ,"
                +"longitude REAL NOT NULL ,"
                +"raspberry_pi_number TEXT NOT NULL ,"
                +"text_data TEXT NOT NULL ,"
                +"created_at INTEGER NOT NULL ,"
                +"updated_at INTEGER NOT NULL"
                +");"
        );

        // ローカル環境テーブルを作成
        db.execSQL("CREATE TABLE "+ENVIRONMENT_TABLE_NAME+" ("
                +"environment_id TEXT PRIMARY KEY NOT NULL ,"
                +"weather TEXT ,"
                +"temperature REAL ,"
                +"created_at INTEGER NOT NULL ,"
                +"updated_at INTEGER NOT NULL"
                +");"
        );

        // ローカルキャラクターテーブルを作成
        db.execSQL("CREATE TABLE "+CHARACTER_TABLE_NAME+" ("
                +"access_point_id TEXT PRIMARY KEY NOT NULL ,"
                +"character_name TEXT NOT NULL ,"
                +"character_file_path TEXT NOT NULL ,"
                +"created_at INTEGER NOT NULL ,"
                +"updated_at INTEGER NOT NULL"
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

    /* ローカル観光地テーブルからローカル観光地データ取得 */
    public ArrayList<Map<String, Object>> getSpotsData() {

        ArrayList<Map<String, Object>> spotsData = new ArrayList<Map<String, Object>>();
        SQLiteDatabase db = this.getWritableDatabase(); // DBへ接続

        try {
            // ローカル観光地テーブルから観光地データをすべて取得
            Cursor cursor = db.query(   SPOT_TABLE_NAME,
                                        new String[]{"spot_id","environment_id","spot_name","spot_phoname","street_address","postal_code","latitude","longitude","photo_file_path"},
                                        null,
                                        null,
                                        null,null,null
                                    );

            cursor.moveToFirst(); // カーソルを一番最初に持ってくる
            // 観光地データを追加
            while(cursor.moveToNext()) {
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
        String spotText = null;

        SQLiteDatabase db = this.getWritableDatabase(); // DBへ接続

        try {
            // ローカル観光地テーブルから特定の観光地テキストデータを取得
            Cursor cursor = db.query(   SPOT_TABLE_NAME,
                    new String[]{"spot_id","text_data"},
                    "spot_id == ?",
                    new String[]{spotId},
                    null,null,null
            );

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

    /* ローカル環境テーブルの特定の環境データを取得 */
    public Map<String, Object> getEnvironmentData(String environmentId) {
        Map<String, Object> environmentData = new HashMap<String, Object>();
        SQLiteDatabase db = this.getWritableDatabase(); // DBへ接続

        try {
            // ローカル環境テーブルの特定の環境データを取得
            Cursor cursor = db.query(   ENVIRONMENT_TABLE_NAME,
                    new String[]{"environment_id","weather","temperature"},
                    "environment_id == ?",
                    new String[]{environmentId},
                    null,null,null
            );

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

    /* ローカルアクセスポイントテーブルから特定の観光地内のAPの座標を取得 */
    public ArrayList<Double[]> getAccessPointLocations(String spotId) {
        ArrayList<Double[]> accessPointLocations = new ArrayList<Double[]>();

        SQLiteDatabase db = this.getWritableDatabase(); // DBへ接続

        try {
            // ローカル観光地テーブルから観光地データをすべて取得
            Cursor cursor = db.query(   ACCESS_POINT_TABLE_NAME,
                    new String[]{"access_point_id","spot_id","latitude","longitude"},
                    "spot_id == ?",
                    new String[] {spotId},
                    null,null,null
            );

            cursor.moveToFirst(); // カーソルを一番最初に持ってくる
            // 観光地データを追加
            while(cursor.moveToNext()) {
                Double[] accessPointLocation = new Double[2];
                accessPointLocation[0] = cursor.getDouble(cursor.getColumnIndex("latitude"));
                accessPointLocation[1] = cursor.getDouble(cursor.getColumnIndex("longitude"));
                accessPointLocations.add(accessPointLocation);
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
                    new String[]{"access_point_id","raspberry_pi_number","text_data"},
                    "raspberry_pi_number == ?",
                    new String[] {raspberrypiNumber},
                    null,null,null
            );

            cursor.moveToFirst(); // カーソルを一番最初に持ってくる

            accessPointGuide.put("spot_id", cursor.getString(cursor.getColumnIndex("spot_id")));
            accessPointGuide.put("text_data", cursor.getString(cursor.getColumnIndex("text_data")));

            cursor.close();
        }
        finally {
            db.close(); // DBを切断
        }

        return accessPointGuide;
    }

    /* ローカルキャラクターテーブルから特定のキャラクターデータを取得 */
    public Map<String, Object> getCharacterData(String accessPointId) {
        Map<String, Object> characterData = new HashMap<String, Object>();
        SQLiteDatabase db = this.getWritableDatabase(); // DBへ接続

        try {
            // ローカルキャラクターテーブルから特定のキャラクターデータを取得
            Cursor cursor = db.query(   CHARACTER_TABLE_NAME,
                    new String[]{"access_point_id","character_name","character_file_path"},
                    "access_point_id == ?",
                    new String[]{accessPointId},
                    null,null,null
            );

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
            spotLocation[1] = (Double)spotData.get("latitude");
            // 現在地と観光地の距離を計算
            Double distance = LocationAcqisition.getDistance(currentLocation, spotLocation);

            // 観光地データ、環境データ、距離を結合
            Map<String, Object> spotEnvironmentDistanceData = addSpotEnvironmentDistanceData(spotData, environmentData, distance);

            // 処理済み観光地データに追加
            spotsEnvironmentDistanceData.add(spotEnvironmentDistanceData);
        }

        return spotsEnvironmentDistanceData;
    }

    /* 案内テキストデータを付加したキャラクターデータを取得 */
    public Map<String, Object> getCharacterGuide(String raspberrypiNumber) {
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
                    new String[]{"environment_id","created_at","updated_at"},
                    null,
                    null,
                    null,null,null,"limit 1"
            );

            cursor.moveToFirst(); // カーソルを一番最初に持ってくる
            // ローカル環境テーブルのレコードの更新時間を取得
            Calendar updateTime = Calendar.getInstance();
            updateTime.setTimeInMillis(cursor.getInt(cursor.getColumnIndex("update_at")));
            cursor.close();
            // 現在時間を取得
            Calendar nowTime = Calendar.getInstance();
            nowTime.setTimeInMillis(System.currentTimeMillis());

            updateTime.add(Calendar.MINUTE, 10);// 更新時間に10分足す
            // (now >= update+10分なら更新)
            if (nowTime.compareTo(updateTime) >= 0) timeCourse = true;
        }
        finally {
            db.close(); // DBを切断
        }

        return timeCourse;
    }

    /* ローカルキャラクターテーブルの更新の可否を判定 */
    private Boolean isCharacterTableTime() {
        Boolean timeCourse = false;

        SQLiteDatabase db = this.getWritableDatabase(); // DBへ接続

        try {
            // ローカルキャラクターテーブルのキャラクターデータを1つ取得
            Cursor cursor = db.query(   CHARACTER_TABLE_NAME,
                    new String[]{"access_point_id","created_at","updated_at"},
                    null,
                    null,
                    null,null,null,"limit 1"
            );

            cursor.moveToFirst(); // カーソルを一番最初に持ってくる
            // ローカルキャラクターテーブルのレコードの更新時間を取得
            Calendar updateTime = Calendar.getInstance();
            updateTime.setTimeInMillis(cursor.getInt(cursor.getColumnIndex("update_at")));
            cursor.close();
            // 現在時間を取得
            Calendar nowTime = Calendar.getInstance();
            nowTime.setTimeInMillis(System.currentTimeMillis());

            updateTime.add(Calendar.MINUTE, 10);// 更新時間に10分足す
            // (now >= update+10分なら更新)
            if (nowTime.compareTo(updateTime) >= 0) timeCourse = true;
        }
        finally {
            db.close(); // DBを切断
        }
        return timeCourse;
    }

    /* 観光地データ、環境データの天気・気温情報、距離情報を結合 */
    private Map<String, Object> addSpotEnvironmentDistanceData(Map<String, Object> spotData, Map<String, Object> environmentData, Double distance) {
        spotData.put("weather", environmentData.get("weather"));
        spotData.put("temperature", environmentData.get("temperature"));
        spotData.put("distance", distance);
        return spotData;
    }

    /* キャラクターデータとアクセスポイント案内データの案内テキスト情報を結合 */
    private Map<String, Object> addCharacterGuideData(Map<String, Object> characterData, String guideText) {
        characterData.put("text_data", guideText);
        return characterData;
    }

    /* ローカルDBのデータをすべて削除 */
    public void deleteRegisterData() {
        try {
            db.deleteDatabase(context.getDatabasePath(this.getDatabaseName()));
        }
        catch(Error error){}
    }
}
