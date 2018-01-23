package jp.dmarch.kochitabi;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ServerExchange {

    /* サンプルデータ */
    String date = "2018-01-19 00:00:00.624643";
    String date2 = "2018-01-19 18:50:00.624643";
    public final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private String[] spot_id = {"sp0001", "sp0002", "sp0003", "sp0004", "sp0005", "sp0006", "sp0007"};
    private String[] environment_id = {"en0001", "en0002", "en0003", "en0004", "en0005", "en0006", "en0007"};
    private String[] spot_name = {"高知工科大学", "高知城", "ひろめ市場", "室戸岬", "四万十川", "桂浜", "早明浦ダム"};
    private String[] spot_phoname = {"コウチコウカダイガク", "コウチジョウ", "ヒロメイチバ", "ムロトミサキ", "シマントガワ", "カツラハマ", "サメウラダム"};
    private String[] street_address = {"土佐山田", "高知", "高知中央", "室戸", "四万十", "高知南岸", "土佐・本山"};
    private Integer[] postal_code = {7830001, 7830002, 7830003, 7830004, 7830005, 7830006, 783007};
    private Double[] latitude = {33.620623, 33.560801, 33.560635, 33.2415162, 33.132971, 33.497154, 33.757142};
    private Double[] longitude = {133.719825, 133.531500, 133.535728, 134.176223, 132.980917, 133.574927, 133.550383};
    private String[] photo_file_path = {"kut", "kochi", "hirome", "muroto", "shimanto", "katurahama", "sameura"};
    private String[] text_data = {
            "本学は「大学のあるべき姿を常に追求し、世界一流の大学をめざす」という高い志を掲げ、高知県が設置し学校法人が運営する公設民営大学として開学しました。先進的な教育システムをいち早く取り入れ、大学としての存在感を発揮し続けています。平成21年4月からは公立大学法人として新たな一歩を踏み出し、公立大学法人という強固な基盤の上に積み重ねてきた自主・自律的な運営ノウハウを生かした革新的な大学運営を進めています。",
            "土佐24万石を襲封した山内一豊によって創建されて以来、約400年余りの歴史を有する南海の名城として名高い",
            "平成１０年、「この土地を商店街活性化の核に」と地元から提案されたプランに、所有者である財団法人民間都市開発推進機構、ミサワホーム株式会社、地元建設会社等が賛同し「ひろめ市場」が建設されました。",
            "壮大な岩、荒々しい海、空海が残した数々の伝説、パワースポットなど盛りだくさん　地球の営みを体感できるスポット！",
            "津野町不入山に端を発し、中土佐町、四万十町、四万十市を流れる四国最長の大河（全長196km）。『最後の清流』として知られ、火振り漁や柴づけ漁など現在でも伝統的な漁が行われている。上流から下流に数多く残っている沈下橋は、欄干がなく川の増水時に水面下に沈むことで流失しないように作られた橋で、今も住民の生活道であるとともに、四万十川の風物詩となっている。",
            "高知県を代表する景勝地の一つ。浦戸湾口、龍頭［りゅうず］岬と龍王岬の間に弓状に広がる海岸で、背後に茂り合う松の緑と、海浜の五色の小砂利、紺碧の海が箱庭のように調和する見事な景勝地。古来より月の名所として知られ、“月の名所は桂浜・・・”と「よさこい節」にも唄われている。東端の龍頭岬では、幕末の志士坂本龍馬の銅像が太平洋を見下ろしている。海浜一帯は「桂浜公園」となっており、水族館や、山手には「坂本龍馬記念館」もある。",
            "高知県長岡郡本山町と土佐郡土佐町にまたがる、一級河川・吉野川本流上流部に建設されたダムである。吉野川の治水と四国地方全域の利水を目的に建設され、このダムの水運用は四国地方の経済・市民生活に極めて多大な影響を及ぼす。このため「四国のいのち」とも呼ばれ、四国地方の心臓的な役割を果たす。"
    };
    private String[] created_at = {date, date, date, date, date, date, date};
    private String[] updated_at = {date2, date2, date2, date2, date2, date2, date2};

    private String[] weather = {"雨", "晴れ", "曇り", "曇り", "晴れ", "晴れ", "霧雨"};
    private Double[] temperature = {20.0, 20.6, 19.8, 14.4, 16.0, 23.6, 17.4};

    private String[] access_point_id = {"la0001", "la0002", "la0003"};
    private String[] access_point_name = {"地域連携棟", "ドミトリー", "食堂"};
    private Double[] access_point_latitude = {33.621136, 33.620288, 33.623013};
    private Double[] access_point_longitude = {133.717958, 133.720973, 133.720061};
    private String[] access_point_text_data = {"よく何をするか分かってないんだ、すまない", "一年生の間だけ入れるよ！学校と近いというのは便利だよね！門限と料理ができないのとお店が無いのが悩みかな！", "食堂は1Fでは主菜副菜飯丼麺色々あるよ。2Fは珈琲パスタパンケーキがあるね。"};
    private String[] raspberry_pi_number = {"pi0001", "pi0002", "pi0003"};

    private String[] character_name = {"山田のかかし", "カツオ人間", "しんじょーくん"};
    private String[] character_file_path = {"kakashi", "kathuo", "shinjo"};

    /* サーバからすべてのテーブルデータを取得 */
    public ArrayList<ArrayList<Map<String, Object>>> getLocalDataBaseTables() {

        // すべてのローカルデータベーステーブルのデータを管理するためのArrayListオブジェクト
        ArrayList<ArrayList<Map<String, Object>>> localDataBaseTables = new ArrayList<ArrayList<Map<String,Object>>>();

        // ローカル観光地テーブルデータ
        ArrayList<Map<String, Object>> localSpotTableData = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < spot_id.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("spot_id", spot_id[i]);
            map.put("environment_id", environment_id[i]);
            map.put("spot_name", spot_name[i]);
            map.put("spot_phoname", spot_phoname[i]);
            map.put("street_address", street_address[i]);
            map.put("postal_code", postal_code[i]);
            map.put("latitude", latitude[i]);
            map.put("longitude", longitude[i]);
            map.put("photo_file_path",photo_file_path[i]);
            map.put("text_data", text_data[i]);
            map.put("created_at", created_at[i]);
            map.put("updated_at", updated_at[i]);
            localSpotTableData.add(map);
        }
        localDataBaseTables.add(localSpotTableData);

        // ローカルアクセスポイントテーブルデータ
        ArrayList<Map<String, Object>> localAccessPointTableData = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < access_point_id.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("access_point_id", access_point_id[i]);
            map.put("spot_id", spot_id[1]);
            map.put("access_point_name", access_point_name[i]);
            map.put("latitude", access_point_latitude[i]);
            map.put("longitude", access_point_longitude[i]);
            map.put("raspberry_pi_number", raspberry_pi_number[i]);
            map.put("text_data", access_point_text_data[i]);
            map.put("created_at", created_at[i]);
            map.put("updated_at", updated_at[i]);
            localAccessPointTableData.add(map);
        }
        localDataBaseTables.add(localAccessPointTableData);

        // ローカル環境テーブルデータ
        ArrayList<Map<String, Object>> localEnvironmentTableData = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < environment_id.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("environment_id", environment_id[i]);
            map.put("weather", weather[i]);
            map.put("temperature", temperature[i]);
            map.put("created_at", created_at[i]);
            map.put("updated_at", updated_at[i]);
            localEnvironmentTableData.add(map);
        }
        localDataBaseTables.add(localEnvironmentTableData);

        // ローカルキャラクターテーブルデータ
        ArrayList<Map<String, Object>> localCharacterTableData = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < access_point_id.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("access_point_id", access_point_id[i]);
            map.put("character_name", character_name[i]);
            map.put("character_file_path", character_file_path[i]);
            map.put("created_at", created_at[i]);
            map.put("updated_at", updated_at[i]);
            localCharacterTableData.add(map);
        }
        localDataBaseTables.add(localCharacterTableData);

        return localDataBaseTables;

    }

    /* サーバからローカル環境テーブルデータを取得 */
    public ArrayList<Map<String, Object>> getEnvironmentTable() {

        // ローカル環境テーブルデータ
        ArrayList<Map<String, Object>> environmentTable = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < environment_id.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("environment_id", environment_id[i]);
            map.put("weather", weather[i]);
            map.put("temperature", temperature[i]);
            map.put("created_at", created_at[i]);
            map.put("updated_at", updated_at[i]);
            environmentTable.add(map);
        }

        return environmentTable;

    }

    /* サーバからローカルキャラクターテーブルデータを取得 */
    public ArrayList<Map<String, Object>> getCharacterTable() {

        // ローカルキャラクターテーブルデータ
        ArrayList<Map<String, Object>> characterTable = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < access_point_id.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("access_point_id", access_point_id[i]);
            map.put("character_name", character_name[i]);
            map.put("character_file_path", character_file_path[i]);
            map.put("created_at", created_at[i]);
            map.put("updated_at", updated_at[i]);
            characterTable.add(map);
        }

        return characterTable;

    }

}