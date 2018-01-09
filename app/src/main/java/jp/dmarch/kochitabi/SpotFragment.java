package jp.dmarch.kochitabi;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
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
 * http://android.tecc0.com/?p=170
 * https://qiita.com/t-kashima/items/9462af782fb5f1a2a7da
 */

public class SpotFragment extends Fragment {

    // タブ切り替えページ
    private static final String ARG_PARAM = "page";

    // メンバ変数
    private ArrayList<Map<String, Object>> spotsData;
    private ArrayList<Map<String, Object>> spotsListData;
    private LocationAcquisition locationAcquisition;

    /* サンプルデータ
    private String[] spot_name = {"高知工科大学", "高知城", "ひろめ市場", "室戸岬", "四万十川", "桂浜", "早明浦ダム"};
    private String[] spot_phoname = {"コウチコウカダイガク", "コウチジョウ", "ヒロメイチバ", "ムロトミサキ", "シマントガワ", "カツラハマ", "サメウラダム"};
    private String[] photo_file_path = {"kut",
                                        "kochi",
                                        "hirome",
                                        "muroto",
                                        "shimanto",
                                        "katurahama",
                                        null };
    private Double[] distance = {0.6, 20.6, 19.8, 74.4, 116.0, 23.6, 47.4};
    private String[] weather = {"雨", "晴", "曇", "曇", "晴", "晴", "曇", "雨"};
    */

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
        locationAcquisition = new LocationAcquisition(getActivity());
        locationAcquisition.beginLocationAcquisition();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spot, container, false);     // xml読み込み
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();

        int page = getArguments().getInt(ARG_PARAM, 0);     // タブページ

        /* サンプル
        // spotsDataデータ代入
        spotsData = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < spot_name.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("spot_name", spot_name[i]);
            map.put("spot_phoname", spot_phoname[i]);
            map.put("distance", distance[i]);
            map.put("weather", weather[i]);
            map.put("photo_file_path",photo_file_path[i]);

            spotsData.add(map);

        }
        */

        //　現在地情報取得
        Double[] currentLocation = locationAcquisition.getCurrentLocation();

        // 観光地情報取得
        spotsData = new DataBaseHelper(getActivity()).getSpotsEnvironmentDistanceData(currentLocation);

        if(page == 1) {
            this.sortSyllabary(spotsData);      // タブが1ページ(五十音順)ならsortSyllabary呼び出し
        }
        else if(page == 2) {
            this.sortDistance(spotsData);       // タブが2ページなら(近い順)ならsortDistance呼び出し
        }

        this.displaySpotsList(spotsData);       // displaySpotsList呼び出し

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationAcquisition.endLocationAcquisition();
    }

    /* 観光地一覧を設定、表示するためのメソッド */
    private void displaySpotsList(final ArrayList<Map<String, Object>> spotsData) {
        ListView spotsList = (ListView) getView().findViewById(R.id.spot_list);     // ListView読み込み

        // リスト表示用ArrayList作成
        spotsListData = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < spotsData.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("spot_name", (((HashMap<String, Object>)spotsData.get(i)).get("spot_name")).toString());
            map.put("spot_phoname", (((HashMap<String, Object>)spotsData.get(i)).get("spot_phoname")).toString());
            map.put("distance", (((HashMap<String, Object>)spotsData.get(i)).get("distance")).toString());
            map.put("weather", (((HashMap<String, Object>)spotsData.get(i)).get("weather")).toString());
            // 画像はnull値発生の恐れがあるためエラー用画像をnull条件分岐で設定
            if (((HashMap<String, Object>)spotsData.get(i)).get("photo_file_path") != null) {
                map.put("photo_file_path", this.getResources().getIdentifier((((HashMap<String, Object>)spotsData.get(i)).get("photo_file_path")).toString(), "drawable", "jp.dmarch.kochitabi"));
            }
            else {
                map.put("photo_file_path", this.getResources().getIdentifier("noimage", "drawable", "jp.dmarch.kochitabi"));
            }

            spotsListData.add(map);

        }

        // ListViewにArrayListの中身を表示させるためのAdapter
        // SimpleAdapterの中身は(Context, ArrayList, ウィジェット定義resource, ArrayListキー項目, 対応ウィジェット)
        SimpleAdapter adapter = new SimpleAdapter (
                getActivity(),
                spotsListData,
                R.layout.spot_item,
                new String[]{ "photo_file_path", "spot_name", "distance", "weather", "weather" },
                new int[] { R.id.spot_image, R.id.spot_name, R.id.spot_distance, R.id.spot_weather, R.id.spot_frame }

        );

        adapter.setViewBinder(new ColorFrameViewBinder());      // フレーム色変更

        spotsList.setAdapter(adapter);      // ListViewにAdapterを設定

        // 項目タップ時Detailへ画面遷移
        spotsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View view, int pos, long id) {

                Intent intent = new Intent(getActivity(), SpotDetailActivity.class);        // 遷移Activity設定

                // Mapからデータ取り出し
                Map<String, Object> spotData = spotsData.get(pos);
                String spotId = spotData.get("spot_id").toString();
                String environmentId = spotData.get("environment_id").toString();
                String spotName = spotData.get("spot_name").toString();
                String spotPhoname = spotData.get("spot_phoname").toString();
                String streetAddress = spotData.get("street_address").toString();
                Integer postalCode = new Integer(spotData.get("postal_code").toString()).intValue();
                Double latitude = new Double(spotData.get("latitude").toString()).doubleValue();
                Double longitude = new Double(spotData.get("longitude").toString()).doubleValue();
                String photoFilePath;
                if (spotData.get("photo_file_path") == null) {
                    photoFilePath = null;
                }
                else {
                    photoFilePath = spotData.get("photo_file_path").toString();
                }

                // 渡すデータ付与
                intent.putExtra("spot_id", spotId);
                intent.putExtra("environment_id", environmentId);
                intent.putExtra("spot_name", spotName);
                intent.putExtra("spot_phoname", spotPhoname);
                intent.putExtra("street_address", streetAddress);
                intent.putExtra("postal_code", postalCode);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("photo_file_path", photoFilePath);

                startActivity(intent);      // 画面遷移

            }
        });

    }

    /* フリガナを参照し五十音順に並べ替えるメソッド */
    private ArrayList<Map<String, Object>> sortSyllabary(ArrayList<Map<String, Object>> spotsData) {
        Collections.sort(spotsData, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> data1, Map<String, Object> data2) {
                // spot_photoname取得
                String phoname1 = data1.get("spot_phoname").toString();
                String phoname2 = data2.get("spot_phoname").toString();

                // 辞書順で昇順比較し返り値に
                return phoname1.compareTo(phoname2);

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
