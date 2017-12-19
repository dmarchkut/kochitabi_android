package jp.dmarch.kochitabi;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

/*
 * http://androhi.hatenablog.com/entry/2015/06/17/083000
 * https://qiita.com/furu8ma/items/1602a4bbed4303fec5b1
 */

public class SpotActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_spot);     // xml読み込み

    }

    @Override
    public void onResume() {
        super.onResume();
        setSpotFragment();      // setSpotFragment呼び出し

    }

    /* SpotFragmenクラスを使用するための設定を行うメソッド */
    private void setSpotFragment() {
        // xml要素読み込み
        TabLayout tabLayout = (TabLayout)findViewById(R.id.spotTabs);
        ViewPager viewPager = (ViewPager)findViewById(R.id.spotPager);

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
                return pageTitle[position];
            }

            // タブ長さ（名称設定の個数に応じて変動）
            @Override
            public int getCount() {
                return pageTitle.length;
            }

        };

        // 調整するよ！
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

    }

}
