package com.vince.networkservice.request;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/*
 *  @描述：    获取设备信息
 */
public class DeviceData {
    private static final String TAG = "DeviceData";

    public static String getDeviceModel() {

        return android.os.Build.MODEL;
    }

    public static String getVersionName( Context context ) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo    packInfo;
        try {
            packInfo = packageManager.getPackageInfo( context.getPackageName(), 0 );

            return packInfo.versionName;
        } catch ( PackageManager.NameNotFoundException e ) {
            Log.e( TAG, e.toString() );
        }
        return "unknown_version";
    }

    public static String getVersionCode( Context context ) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo( context.getPackageName(), 0 );

            return String.valueOf(packInfo.versionCode);
        } catch ( PackageManager.NameNotFoundException e ) {
            Log.e(TAG, e.toString() );
        }

        return "1";
    }

    public static String getPackageName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);

            return packInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.toString());
        }
        return "unknown_packagename";
    }
}
