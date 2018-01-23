package jp.dmarch.kochitabi;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Map;

/* 参考Webサイト
 * http://rikisha-android.hatenablog.com/entry/2014/04/04/202207
 * http://androhi.hatenablog.com/entry/2015/06/17/083000
 * https://qiita.com/furu8ma/items/1602a4bbed4303fec5b1
 */

public class SpotActivity extends AppCompatActivity {

    private LocationAcquisition locationAcquisition;
    private ArrayList<Map<String, Object>> spotsData;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setTitle("観光地一覧");
        setContentView(R.layout.activity_spot);     // xml読み込み
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);      // バックボタン追加

        // LocationAcquisionインスタンス化 & 始動設定
        locationAcquisition = new LocationAcquisition(this);
        locationAcquisition.beginLocationAcquisition();

    }

    // バックボタンタップ時処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onResume() {
        super.onResume();
        this.setSpotFragment();     // setSpotFragment呼び出し

        //　現在地情報取得
        Double[] currentLocation = locationAcquisition.getCurrentLocation();

        // 観光地情報取得
        spotsData = new DataBaseHelper(this).getSpotsEnvironmentDistanceData(currentLocation);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationAcquisition.endLocationAcquisition();
    }

    /* SpotFragmenクラスを使用するための設定を行うメソッド */
    private void setSpotFragment() {
        // xml要素読み込み
        TabLayout tabLayout = (TabLayout)findViewById(R.id.spot_tabs);
        ViewPager viewPager = (ViewPager)findViewById(R.id.spot_pager);

        // タブ名称設定
        final String[] pageTitle = {"五十音順", "近い順"};

        // Fragment管理
        FragmentManager manager = getSupportFragmentManager();

        // Fragment接続
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(manager) {
            // Fragment要素取得
            @Override
            public Fragment getItem(int position) {
                // spotsDataをBundleに格納
                Bundle bundle = new Bundle();
                bundle.putInt("page", position+1);
                bundle.putInt("data_size", spotsData.size());
                for (int i = 0; i < spotsData.size(); i++) {
                    bundle.putString("spot_id"+String.valueOf(i), spotsData.get(i).get("spot_id").toString());
                    bundle.putString("environment_id"+String.valueOf(i), spotsData.get(i).get("environment_id").toString());
                    bundle.putString("spot_name"+String.valueOf(i), spotsData.get(i).get("spot_name").toString());
                    bundle.putString("spot_phoname"+String.valueOf(i), spotsData.get(i).get("spot_phoname").toString());
                    bundle.putString("street_address"+String.valueOf(i), spotsData.get(i).get("street_address").toString());
                    bundle.putInt("postal_code"+String.valueOf(i), Integer.valueOf(spotsData.get(i).get("postal_code").toString()));
                    bundle.putDouble("latitude"+String.valueOf(i), Double.valueOf(spotsData.get(i).get("latitude").toString()));
                    bundle.putDouble("longitude"+String.valueOf(i), Double.valueOf(spotsData.get(i).get("longitude").toString()));
                    bundle.putString("photo_file_path"+String.valueOf(i), spotsData.get(i).get("photo_file_path").toString());
                    bundle.putString("weather"+String.valueOf(i), spotsData.get(i).get("weather").toString());
                    bundle.putDouble("temperature"+String.valueOf(i), Double.valueOf(spotsData.get(i).get("temperature").toString()));
                    bundle.putDouble("distance"+String.valueOf(i), Double.valueOf(spotsData.get(i).get("distance").toString()));

                }

                // FragmentにBundleセット
                SpotFragment spotFragment = new SpotFragment();
                spotFragment.setArguments(bundle);

                return spotFragment;
            }

            // タブ内容取得
            @Override
            public CharSequence getPageTitle(int position) {
                return null;
            }

            // タブ個数（名称設定の個数に応じて変動）
            @Override
            public int getCount() {
                return pageTitle.length;
            }

        };

        // 画面表示
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // タブ左画面構成
        TabLayout.Tab tab1 = tabLayout.getTabAt(0);
        View tab1View = inflater.inflate(R.layout.spot_tab_left, null);
        tab1.setCustomView(tab1View);

        // タブ右画面構成
        TabLayout.Tab tab2 = tabLayout.getTabAt(1);
        View tab2View = inflater.inflate(R.layout.spot_tab_right, null);
        tab2.setCustomView(tab2View);

    }

}