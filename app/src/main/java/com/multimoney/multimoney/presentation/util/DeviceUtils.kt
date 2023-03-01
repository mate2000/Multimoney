package com.multimoney.multimoney.presentation.util

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.multimoney.data.util.catalog.DeviceType
import com.multimoney.multimoney.BuildConfig
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Enumeration

fun getDeviceId(activity: AppCompatActivity): String {
    return Settings.Secure.getString(activity.contentResolver, Settings.Secure.ANDROID_ID)
}

fun getIpAddress(activity: FragmentActivity): String? {
    //check for wifi address first
    val wifiMgr = activity.getSystemService(Context.WIFI_SERVICE) as WifiManager
    if (wifiMgr.isWifiEnabled) {
        val wifiInfo = wifiMgr.connectionInfo
        val ip = wifiInfo.ipAddress
        return android.text.format.Formatter.formatIpAddress(ip)
    }

    //then checks for mobile data address
    val en: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
    while (en.hasMoreElements()) {
        val networkInterface: NetworkInterface = en.nextElement()
        val enumIpAddress: Enumeration<InetAddress> = networkInterface.inetAddresses
        while (enumIpAddress.hasMoreElements()) {
            val inetAddress: InetAddress = enumIpAddress.nextElement()
            if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                return inetAddress.getHostAddress()
            }
        }
    }
    return ""
}

fun getDeviceModel(): String {
    return Build.MODEL
}

fun getDeviceBrand(): String {
    return Build.BRAND
}


fun getAppVersion(): String {
    return BuildConfig.VERSION_NAME
}

fun checkIfEmulator(): Boolean {
    return (Build.MANUFACTURER.contains("Genymotion")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.toLowerCase().contains("droid4x")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86")
            || Build.HARDWARE == "goldfish"
            || Build.HARDWARE == "vbox86"
            || Build.HARDWARE.toLowerCase().contains("nox")
            || Build.FINGERPRINT.startsWith("generic")
            || Build.PRODUCT == "sdk"
            || Build.PRODUCT == "google_sdk"
            || Build.PRODUCT == "sdk_x86"
            || Build.PRODUCT == "vbox86p"
            || Build.PRODUCT.toLowerCase().contains("nox")
            || Build.BOARD.toLowerCase().contains("nox")
            || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")))
}


fun getDeviceName(context: Context): String? {
    try {
        if (Build.VERSION.SDK_INT <= 31) {
            return Settings.Secure.getString(
                context.contentResolver,
                "bluetooth_name"
            )
        }
        if (Build.VERSION.SDK_INT >= 25) {
            return Settings.Global.getString(
                context.contentResolver,
                Settings.Global.DEVICE_NAME
            )
        }
    } catch (e: Exception) {
        // same as default unknown return
    }
    return "unknown"
}

fun getDeviceType(context: Context): DeviceType {
    // Detect TVs via ui mode (Android TVs) or system features (Fire TV).
    if (context.packageManager.hasSystemFeature("amazon.hardware.fire_tv")) {
        return DeviceType.TV
    }
    val uiManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager?
    if (uiManager != null && uiManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION) {
        return DeviceType.TV
    }
    val deviceTypeFromConfig: DeviceType? = getDeviceTypeFromResourceConfiguration(context)
    return if (deviceTypeFromConfig != null && deviceTypeFromConfig !== DeviceType.UNKNOWN) {
        deviceTypeFromConfig
    } else getDeviceTypeFromPhysicalSize(context)
}

private fun getDeviceTypeFromResourceConfiguration(context: Context): DeviceType? {
    val smallestScreenWidthDp: Int = context.resources.configuration.smallestScreenWidthDp
    if (smallestScreenWidthDp == Configuration.SMALLEST_SCREEN_WIDTH_DP_UNDEFINED) {
        return DeviceType.UNKNOWN
    }
    return if (smallestScreenWidthDp >= 600) DeviceType.TABLET else DeviceType.HANDSET
}


private fun getDeviceTypeFromPhysicalSize(context: Context): DeviceType {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
    if (windowManager == null) {
        return DeviceType.UNKNOWN;
    }
    val metrics = DisplayMetrics()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        windowManager.getDefaultDisplay().getRealMetrics(metrics);
    } else {
        windowManager.getDefaultDisplay().getMetrics(metrics);
    }
    // Calculate physical size.
    // Calculate physical size.
    val widthInches = metrics.widthPixels / metrics.xdpi.toDouble()
    val heightInches = metrics.heightPixels / metrics.ydpi.toDouble()
    val diagonalSizeInches = Math.sqrt(Math.pow(widthInches, 2.0) + Math.pow(heightInches, 2.0))
    if (diagonalSizeInches >= 3.0 && diagonalSizeInches <= 6.9) {
        // Devices in a sane range for phones are considered to be Handsets.
        return DeviceType.HANDSET;
    } else if (diagonalSizeInches > 6.9 && diagonalSizeInches <= 18.0) {
        // Devices larger than handset and in a sane range for tablets are tablets.
        return DeviceType.TABLET;
    } else {
        // Otherwise, we don't know what device type we're on/
        return DeviceType.UNKNOWN;
    }
}