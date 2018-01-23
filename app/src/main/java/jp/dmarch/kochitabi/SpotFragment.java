package jp.dmarch.kochitabi;

import android.content.Intent;
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

import static java.lang.Double.NaN;

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
    private int page;

    // メンバ変数
    private ArrayList<Map<String, Object>> spotsData;
    private ArrayList<Map<String, Object>> spotsListData;

    // コンストラクタ
    public SpotFragment() {

    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spot, container, false);     // xml読み込み

        // データ取得
        Bundle bundle = getArguments();
        page = bundle.getInt("page");
        spotsData = new ArrayList<Map<String, Object>>();
        int dataSize = bundle.getInt("data_size");
        for (int i = 0; i < dataSize; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("spot_id", bundle.getString("spot_id"+String.valueOf(i)));
            map.put("environment_id", bundle.getString("environment_id"+String.valueOf(i)));
            map.put("spot_name", bundle.getString("spot_name"+String.valueOf(i)));
            map.put("spot_phoname", bundle.getString("spot_phoname"+String.valueOf(i)));
            map.put("street_address", bundle.getString("street_address"+String.valueOf(i)));
            map.put("postal_code", bundle.getInt("postal_code"+String.valueOf(i)));
            map.put("latitude", bundle.getDouble("latitude"+String.valueOf(i)));
            map.put("longitude", bundle.getDouble("longitude"+String.valueOf(i)));
            map.put("photo_file_path", bundle.getString("photo_file_path"+String.valueOf(i)));
            map.put("weather", bundle.getString("weather"+String.valueOf(i)));
            map.put("temperature", bundle.getDouble("temperature"+String.valueOf(i)));
            map.put("distance", bundle.getDouble("distance"+String.valueOf(i)));
            spotsData.add(map);
        }

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();

        if(page == 1) {
            this.sortSyllabary(spotsData);      // タブが1ページ(五十音順)ならsortSyllabary呼び出し
        }
        else if(page == 2) {
            this.sortDistance(spotsData);       // タブが2ページなら(近い順)ならsortDistance呼び出し
        }

        this.displaySpotsList(spotsData);       // displaySpotsList呼び出し

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
            map.put("weather", (((HashMap<String, Object>)spotsData.get(i)).get("weather")).toString());
            // 距離はNaN発生の恐れがあるためエラー用ハイフンを条件分岐で設定
            if (!((HashMap<String, Object>)spotsData.get(i)).get("distance").equals(NaN)) {
                map.put("distance", String.format("%.1f", ((HashMap<String, Object>)spotsData.get(i)).get("distance")) + " km");
            }
            else {
                map.put("distance", " - ");
            }
            // 画像はnull値発生の恐れがあるためエラー用画像をnull条件分岐で設定
            if (((HashMap<String, Object>)spotsData.get(i)).get("photo_file_path") != null) {
                map.put("photo_file_path", this.getResources().getIdentifier((((HashMap<String, Object>)spotsData.get(i)).get("photo_file_path")).toString(), "drawable", "jp.dmarch.kochitabi"));
                if (map.get("photo_file_path").equals(0)) {     // drawableにリソースが存在しなかった場合
                    map.put("photo_file_path", this.getResources().getIdentifier("noimage", "drawable", "jp.dmarch.kochitabi"));
                }
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
                Integer postalCode = Integer.valueOf(spotData.get("postal_code").toString());
                Double latitude = Double.valueOf(spotData.get("latitude").toString());
                Double longitude = Double.valueOf(spotData.get("longitude").toString());
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
                Double distance1 = Double.valueOf(data1.get("distance").toString());      // Double型に変換
                Double distance2 = Double.valueOf(data2.get("distance").toString());      // Double型に変換

                // 大小昇順比較し返り値に
                if (distance1 > distance2) {
                    return 1;
                }
                else if (distance1.equals(distance2)) {
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