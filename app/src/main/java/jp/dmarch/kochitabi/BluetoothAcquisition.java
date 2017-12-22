package jp.dmarch.kochitabi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

public class BluetoothAcquisition {

    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver broadcastReceiver;
    private ArrayList<BluetoothDevice> deviceList;

    public BluetoothAcquisition(Context context) {
        this.context = context;
    }

    private void beginSearchDevice() {
        //broadcastReceiver =
    }

}
