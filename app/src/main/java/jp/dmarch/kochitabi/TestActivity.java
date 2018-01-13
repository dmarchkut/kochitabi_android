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
import android.view.View;

/**
 * Created by monki on 2018/01/11.
 */

public class TestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setSpotFragment();     // setSpotFragment呼び出し
    }

    /* SpotFragmentクラスを使用するための設定を行うメソッド */
    private void setSpotFragment() {
        // xml要素読み込み
        TabLayout tabLayout = (TabLayout)findViewById(R.id.test_tabs);
        ViewPager viewPager = (ViewPager)findViewById(R.id.test_pager);

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
