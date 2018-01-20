package jp.dmarch.kochitabi;

public class LocationAcqisition {
    private final double NO_DATA = NaN; // データが入っていない状態を示す
    private final int REQUEST_PERMISSION = 1000;

    private Context context;
    private Double[] currentLocation = {NO_DATA, NO_DATA}; // 現在地
    private LocationManager locationManager;
    
    // コンストラクタ
    public LocationAcquisition(Context context) {
        this.context = context;

        // Android 6.0以上の端末ならパーミッションチェックを行う
        if (Build.VERSION.SDK_INT >= 23) {

            // アプリでGPSの使用が許可されていないなら
            if (PermissionChecker.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && PermissionChecker.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // 再度、使用許可要求を出す必要があるか（一度拒否していたらtrue）
                if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // GPSの使用許可を要求
                    ActivityCompat.requestPermissions((Activity) context, new String[] {Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION);
                }

            }

        }
    }
}
