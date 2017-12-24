package jp.dmarch.kochitabi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/* 参考Webサイト
 * http://kuwalab.hatenablog.jp/entry/20101221/p1
 * http://javait.blog.fc2.com/blog-entry-154.html
 * https://teratail.com/questions/49896
 * https://java-reference.com/java_collection_sort_original.html
 * http://d.hatena.ne.jp/nattou_curry_2/20090829/1251558484#sort
 */

public class SpotFragment extends Fragment {

    // タブ切り替えページ
    private static final String ARG_PARAM = "page";

    // メンバ変数
    private ArrayList<Map<String, Object>> spotsData;
    private LocationAcqisition locationAcqisition;

    // サンプルデータ
    private String[] spot_name = {"高知工科大学", "高知城", "ひろめ市場", "室戸岬", "四万十川", "桂浜", "早明浦ダム"};
    private String[] spot_photoname = {"コウチコウカダイガク", "コウチジョウ", "ヒロメイチバ", "ムロトミサキ", "シマントガワ", "カツラハマ", "サメウラダム"};
    private String[] photo_file_path = {""};
    private Double[] distance = {0.6, 20.6, 19.8, 74.4, 116.0, 23.6, 47.4};
    private String[] weather = {"雨", "晴", "曇", "曇", "晴", "晴", "曇", "雨"};

    // コンストラクタ
    public SpotFragment() {

    }

    // フラグメント利用準備
    public static SpotFragment newInstance(int page) {
        SpotFragment fragment = new SpotFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM, page);
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spot, container, false);     // xml読み込み
        return view;

    }

    public void onResume() {
        super.onResume();

        int page = getArguments().getInt(ARG_PARAM, 0);     // タブページ

        // spotsDataサンプルデータ代入
        spotsData = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < spot_name.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("spot_name", spot_name[i]);
            map.put("spot_photoname", spot_photoname[i]);
            map.put("distance", distance[i]);
            map.put("weather", weather[i]);

            spotsData.add(map);

        }

        if(page == 1) {
            this.sortSyllabary(spotsData);      // タブが1ページ(五十音順)ならsortSyllabary呼び出し
        }
        else if(page == 2) {
            this.sortDistance(spotsData);       // タブが2ページなら(近い順)ならsortDistance呼び出し
        }

        this.displaySpotsList(spotsData);       // displaySpotsList呼び出し

    }

    /* 観光地一覧を設定、表示するためのメソッド */
    private void displaySpotsList(ArrayList<Map<String, Object>> spotsData) {
        ListView spotsList = (ListView) getView().findViewById(R.id.spot_list);     // ListView読み込み

        // ListViewにArrayListの中身を表示させるためのAdapter
        // SimpleAdapterの中身は(Context, ArrayList, ウィジェット定義resource, ArrayListキー項目, 対応ウィジェット)
        SimpleAdapter adapter = new SimpleAdapter (
                getActivity(),
                spotsData,
                R.layout.spot_item,
                new String[]{ "spot_name", "distance", "weather"},
                new int[] { R.id.spot_name, R.id.spot_distance, R.id.spot_weather}

        );

        spotsList.setAdapter(adapter);      // ListViewにAdapterを設定

    }

    /* フリガナを参照し五十音順に並べ替えるメソッド */
    private ArrayList<Map<String, Object>> sortSyllabary(ArrayList<Map<String, Object>> spotsData) {
        Collections.sort(spotsData, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> data1, Map<String, Object> data2) {
                // spot_photoname取得
                String photoname1 = data1.get("spot_photoname").toString();
                String photoname2 = data2.get("spot_photoname").toString();

                // 辞書順で昇順比較し返り値に
                return photoname1.compareTo(photoname2);

            }
        });

        return spotsData;

    }

    /* 距離情報を参照し近距離順に並べ替えるメソッド */
    private ArrayList<Map<String, Object>> sortDistance(ArrayList<Map<String, Object>> spotsData) {
        Collections.sort(spotsData, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> data1, Map<String, Object> data2) {
                // distance取得
                String distance1 = data1.get("distance").toString();
                Double distance11 = new Double(distance1);      // Double型に変換
                String distance2 = data2.get("distance").toString();
                Double distance22 = new Double(distance2);      // Double型に変換

                // 大小昇順比較し返り値に
                if (distance11 > distance22) {
                    return 1;
                }
                else if (distance11 == distance22) {
                    return 0;
                }
                else {
                    return -1;
                }

            }
        });

        return spotsData;

    }

}
