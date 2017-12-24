package jp.dmarch.kochitabi;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/*
 * Bluetoothによる距離推定
 * http://db-event.jpn.org/deim2011/proceedings/pdf/b9-4.pdf
 */

public class BluetoothAcquisition {

    private Context context;
    private BluetoothAdapter bluetoothAdapter; // アダプタ
    private BroadcastReceiver broadcastReceiver; // レシーバ
    private HashMap<String, Integer> deviceList; //  Bluetooth接続可能な端末のリスト

    private final static Double INNER_INTENSITY_OF_ACCESSPOINT = -65.0; // アクセスポイント内の電波強度の最低値

    public BluetoothAcquisition(Context context) {
        this.context = context;
    }

    /* Bluetooth接続可能な端末の検索開始 */
    public void beginSearchDevice() {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();  // Bluetoothアダプタ取得

        // 端末がBluetoothに対応していなければ
        if (bluetoothAdapter == null) {
            return;
        }

        // Bluetooth機能が無効の時、有効化を促すダイアログを表示
        if (!isBluetoothAcquisition()) {

            // 有効化を促すダイアログを表示
            new AlertDialog.Builder(context)
                    .setMessage("Bluetoothを有効にします")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            // Bluetooth機能の有効化
                            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            context.startActivity(intent);

                        }
                    })
                    .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    })
                    .show();

        }

        deviceList = new HashMap<String, Integer>(); // インスタンス化

        //  端末の検索設定
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();

                // 端末を発見したとき、デバイスリストに追加
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                    BluetoothDevice findingDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); // 見つかった端末
                    String findingDeviceName = findingDevice.getName(); // 端末名を取得

                    // 端末名が取得できれば電波強度を取得し、端末リストに追加
                    if (findingDeviceName != null) {

                        // 見つかった端末の電波強度を取得
                        int raspberrypiIntensity = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);

                        // 端末名と電波強度を端末リストに追加、既に取得済みの端末の場合、電波強度を更新
                        deviceList.put(findingDeviceName, raspberrypiIntensity);

                    }

                }

            }
        };

        // 検索中であれば一度止める
        if (bluetoothAdapter.isDiscovering()) {
            endSearchDevice();
        }

        IntentFilter filter = new IntentFilter(); // インテントフィルタを作成
        filter.addAction(BluetoothDevice.ACTION_FOUND);

        context.registerReceiver(broadcastReceiver, filter); // レシーバの登録

        bluetoothAdapter.startDiscovery(); // 端末の検索開始

    }

    /* Bluetooth接続可能な端末の検索終了 */
    public void endSearchDevice() {

        bluetoothAdapter.cancelDiscovery(); // 端末の検索終了
        context.unregisterReceiver(broadcastReceiver); // レシーバの解除

    }

    /* Bluetooh機能が有効であるかを判断 */
    private Boolean isBluetoothAcquisition() {

        // Bluetooth機能が有効ならtrue、無効ならfalse
        return bluetoothAdapter.isEnabled();

    }

    /* Bluetooth接続可能なRaspberryPi端末のリストを取得 */
    public ArrayList<String> getDeviceList() {

        ArrayList<String> raspberrypiNumberList = new ArrayList<String>(); // RaspberryPi識別番号のリスト
        Pattern pattern = Pattern.compile("^pi[0-9]+"); // piから始まる文字列のパターンを作成

        // こちたびARで用いるRaspberryPi端末の端末名を取得
        for (String deviceName : deviceList.keySet()) {

            // こちたびARで用いる端末の端末名ならばリストに追加
            if ((pattern.matcher(deviceName).find())) {
                raspberrypiNumberList.add(deviceName);
            }

        }

        return raspberrypiNumberList;

    }

    /* 特定のBluetooth接続可能な端末の電波強度を取得 */
    public int getIntensity(String raspberypiNumber) {

        // 取得済みの端末リストを検索
        for (String deviceName : deviceList.keySet()) {

            // リストから見つかれば電波強度を返す
            if (raspberypiNumber.equals(deviceName)) {
                int deviceintensity = deviceList.get(deviceName);
                return deviceintensity;
            }

        }

        // 見つからなかったため、-1を返す
        return -1;

    }

    /* アクセスポイント内にいるかを判断 */
    public String checkAccessPoint() {

        ArrayList<String> raspberrypiNumberList =  getDeviceList(); // RaspberryPi端末リスト

        // アクセスポイント内にいるか判断する
        for (String raspberrypiNumber : raspberrypiNumberList) {

            int raspberrypiIntensity = getIntensity(raspberrypiNumber); // 電波強度

            // アクセスポイント内にいるとき、端末名を返す
            if (raspberrypiIntensity >= INNER_INTENSITY_OF_ACCESSPOINT) {
                return raspberrypiNumber;
            }

        }

        return null;

    }

}
