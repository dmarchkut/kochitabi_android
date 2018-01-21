package jp.dmarch.kochitabi;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
import static android.bluetooth.BluetoothDevice.ACTION_FOUND;
import static android.util.Half.NaN;

/*
 * Bluetoothによる距離推定
 * http://db-event.jpn.org/deim2011/proceedings/pdf/b9-4.pdf
 */

public class BluetoothAcquisition {

    private Context context;
    private BluetoothAdapter bluetoothAdapter; // アダプタ
    private BroadcastReceiver broadcastReceiver; // レシーバ
    private HashMap<String, Integer> deviceList; //  Bluetooth接続可能な端末のリスト
    IntentFilter filter; // 検索に用いるフィルタ
    Boolean restartFlag; // 終了後に再検索開始するか示すフラグ

    private final static int INNER_INTENSITY_OF_ACCESSPOINT = -65; // アクセスポイント内の電波強度の最低値

    public BluetoothAcquisition(Context context) {
        this.context = context;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();  // Bluetoothアダプタ取得
    }

    /* Bluetooth接続可能な端末の検索開始 */
    public void beginSearchDevice() {

        // 端末がBluetoothに対応していなければ何もせずに終わる
        if (bluetoothAdapter == null) return;

        // Bluetooth機能が無効の時、有効化を促すダイアログを表示
        if (!isBluetoothAcquisition()) {

            // Bluetooth機能の有効化
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivity(intent);
            return;

        }

        //  端末の検索設定
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();

                // 端末を発見したとき、デバイスリストに追加
                if (ACTION_FOUND.equals(action)) {

                    BluetoothDevice findingDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); // 見つかった端末
                    String findingDeviceName = findingDevice.getName(); // 端末名を取得

                    // 端末名が取得できれば電波強度を取得し、端末リストに追加
                    if (findingDeviceName != null) {

                        // 見つかった端末の電波強度を取得
                        int raspberrypiIntensity = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);

                        // 端末名と電波強度を端末リストに追加、既に取得済みの端末の場合、電波強度を更新
                        deviceList.put(findingDeviceName, raspberrypiIntensity);

                    }

                    // デバイス検索終了時
                } else if (ACTION_DISCOVERY_FINISHED.equals(action)) {

                    try {

                        // レシーバの解除
                        context.unregisterReceiver(broadcastReceiver);

                        // endSearchDeviceから呼び出されてなければ
                        if (restartFlag) {

                            // もう一度開始
                            context.registerReceiver(broadcastReceiver, filter); // レシーバの登録
                            bluetoothAdapter.startDiscovery(); // 端末の検索開始

                        }
                    } catch (Exception error) {
                        error.printStackTrace();
                    }

                }

            }
        };

        // 検索中であれば一度止める
        if (bluetoothAdapter.isDiscovering()) {
            // endSearchDeviceから呼び出されたことを示すフラグをfalseに
            restartFlag = false;
            bluetoothAdapter.cancelDiscovery(); // 端末の検索終了
        }

        // 再実行が必要であるフラグをtrueに
        restartFlag = true;

        deviceList = new HashMap<String, Integer>(); // インスタンス化

        filter = new IntentFilter(); // インテントフィルタを作成
        filter.addAction(ACTION_FOUND);
        filter.addAction(ACTION_DISCOVERY_FINISHED);

        context.registerReceiver(broadcastReceiver, filter); // レシーバの登録

        bluetoothAdapter.startDiscovery(); // 端末の検索開始

    }

    /* Bluetooth接続可能な端末の検索終了 */
    public void endSearchDevice() {

        // もし検索中なければ何もせずに終わる
        if (!(bluetoothAdapter.isDiscovering())) return;

        // endSearchDeviceから呼び出されたことを示すフラグをtrueに
        restartFlag = false;

        bluetoothAdapter.cancelDiscovery(); // 端末の検索終了

    }

    /* Bluetooth機能が有効であるかを判断 */
    private Boolean isBluetoothAcquisition() {

        // Bluetooth機能が有効ならtrue、無効ならfalse
        return bluetoothAdapter.isEnabled();

    }

    /* Bluetooth接続可能なRaspberryPi端末のリストを取得 */
    public ArrayList<String> getDeviceList() {

        ArrayList<String> raspberrypiNumberList = new ArrayList<String>(); // RaspberryPi識別番号のリスト

        // もし検索中なければ何もせずに終わる
        if (!(bluetoothAdapter.isDiscovering())) return raspberrypiNumberList;

        Pattern pattern = Pattern.compile("^pi[0-9]+"); // piから始まる文字列のパターンを作成

        // こちたびARで用いるRaspberryPi端末の端末名を取得
        for (String deviceName : deviceList.keySet()) {

            // こちたびARで用いる端末の端末名ならばリストに追加
            if (pattern.matcher(deviceName).find()) {
                raspberrypiNumberList.add(deviceName);
            }

        }

        return raspberrypiNumberList;

    }

    /* 特定のBluetooth接続可能な端末の電波強度を取得 */
    public int getIntensity(String raspberrypiNumber) {

        // 電波強度を取得する端末がRaspberryPi端末でなければNaNを返す
        Pattern pattern = Pattern.compile("^pi[0-9]+"); // piから始まる文字列のパターンを作成
        if (!pattern.matcher(raspberrypiNumber).find()) return NaN;

        // 取得済みの端末リストを検索
        for (String deviceName : deviceList.keySet()) {

            // リストから見つかれば電波強度を返す
            if (raspberrypiNumber.equals(deviceName)) {
                int deviceIntensity = deviceList.get(deviceName);
                return deviceIntensity;
            }

        }

        // 見つからなかったため、NaNを返す
        return NaN;

    }

    /* アクセスポイント内にいるかを判断 */
    public String checkAccessPoint() {

        // まだ検索を始めていなければ
        if (!bluetoothAdapter.isDiscovering()) {
            // BluetoothがONならば検索を開始する
            if (isBluetoothAcquisition()) beginSearchDevice();
            return null;
        }

        ArrayList<String> raspberrypiNumberList =  getDeviceList(); // RaspberryPi端末リスト

        // RaspberryPi端末をまだ発見していなければnullを返す
        if (raspberrypiNumberList.size() == 0) return null;

        // アクセスポイント内にいるか判断する
        for (String raspberrypiNumber : raspberrypiNumberList) {

            int raspberrypiIntensity = getIntensity(raspberrypiNumber); // 電波強度

            // 電波強度が取得できていなければ次のアクセスポイントの判定へ
            if (raspberrypiIntensity == NaN) continue;

            // アクセスポイント内にいるとき、端末名を返す
            if (raspberrypiIntensity >= INNER_INTENSITY_OF_ACCESSPOINT) {
                return raspberrypiNumber;
            }

        }

        return null;

    }

}
