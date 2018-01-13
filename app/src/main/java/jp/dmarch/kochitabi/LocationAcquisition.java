package jp.dmarch.kochitabi;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;

import static java.lang.Double.NaN;
import static java.lang.Math.sin;

/* 距離計算(getDistance)
 * https://qiita.com/a_nishimura/items/6c2642343c0af832acd4
 */


public class LocationAcquisition {

    // コンストラクタ
    public LocationAcquisition(Context context) {
    }
}
