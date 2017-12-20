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

/* 参考Webサイト
 * http://rikisha-android.hatenablog.com/entry/2014/04/04/202207
 * http://androhi.hatenablog.com/entry/2015/06/17/083000
 * https://qiita.com/furu8ma/items/1602a4bbed4303fec5b1
 */

public class SpotActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setTitle("観光地一覧");
        setContentView(R.layout.activity_spot);     // xml読み込み

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);      // バックボタン追加

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {       // バックボタンタップ時処理
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
                return SpotFragment.newInstance(position + 1);
            }

            // タブ名称取得
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


        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // タブ左画面構成
        TabLayout.Tab tab1 = tabLayout.getTabAt(0);
        View tab1View = inflater.inflate(R.layout.spot_tab1, null);
        tab1.setCustomView(tab1View);

        // タブ右画面構成
        TabLayout.Tab tab2 = tabLayout.getTabAt(1);
        View tab2View = inflater.inflate(R.layout.spot_tab2, null);
        tab2.setCustomView(tab2View);

    }

}
