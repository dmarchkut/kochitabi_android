package jp.dmarch.kochitabi;

import android.view.View;
import android.widget.SimpleAdapter;

/* 参考Webサイト
 * http://androyer.blogspot.jp/2013/04/simpleadapter.html
 */

class ColorFrameViewBinder implements SimpleAdapter.ViewBinder {
    @Override
    public boolean setViewValue(View view, Object data, String text) {
        // spot_frameに渡した天気情報によって色を設定する
        if (view.getId() == R.id.spot_frame) {
            if (text.equals("晴れ")) {
                view.setBackgroundResource(R.color.colorSun);
            }
            else if (text.equals("曇り")) {
                view.setBackgroundResource(R.color.colorCroud);
            }
            else {
                view.setBackgroundResource(R.color.colorRain);
            }

            return true;

        }
        return false;

    }
}
