package jp.dmarch.kochitabi;

import java.util.Calendar;
import android.content.Context;
import android.util.Log;

public class BluetoothAcqisition {
    private Context context;

    private Boolean isAccessPoint = false; // アクセスポイント内か

    protected String checkAccessPoint() {
        if (isAccessPoint) return "pi0001";
        else return null;
    }

    public BluetoothAcqisition(Context context) {
        this.context = context;
    }

    public void beginSearchDevice() {
        Log.d("Bluetooth", "beginSearchDevice");
    }
    public void endSearchDevice() {
        Log.d("Bluetooth", "endSearchDevice");
    }

    // アクセスポイントの中に入ったり出たりする
    public void changeAccessPoint() {
        this.isAccessPoint = !(this.isAccessPoint);
    }
}
