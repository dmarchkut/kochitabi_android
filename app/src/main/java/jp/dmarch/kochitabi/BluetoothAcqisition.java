package jp.dmarch.kochitabi;

import java.util.Calendar;
import android.content.Context;

public class BluetoothAcqisition {
    private Context context;

    protected String checkAccessPoint() {
        Calendar now = Calendar.getInstance(); //インスタンス化
        int minute = now.get(now.MINUTE);     //分を取得
        minute = minute % 2;

        if (minute == 1) {
            return "pi0001";
        }
        return null;
    }

    public BluetoothAcqisition(Context context) {
        this.context = context;
    }

    public void beginSearchDevice() {
    }
    public void endSearchDevice() {
    }
}