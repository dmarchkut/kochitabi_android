package jp.dmarch.kochitabi;

import android.content.Context;

import static java.lang.Double.NaN;


public class LocationAcquisition {

    private final double NO_DATA = NaN; // データが入っていない状態を示す
    private final int REQUEST_PERMISSION = 1000;

    private Context context;
    private Double[] currentLocation = {NO_DATA, NO_DATA}; // 現在地

    // コンストラクタ
    public LocationAcquisition(Context context) {
        this.context = context;
    }

    /* 現在地の計測を行うための設定及び、その後の計測を開始 */
    public void beginLocationAcquisition() {

    }

    /* 現在地の計測を終了 */
    public void endLocationAcquisition() {

    }

    public Double[] getCurrentLocation() {

        Double[] aaa = {11.11, 22.22};
        return  aaa;
    }

    /* 現在地と観光地との距離を計算 */
    public Double getDistance(Double[] currentLocation, Double[] spotLocation) {

        Double distance = 111.0;

        return distance;
    }
}
