package com.afollestad.udpdiscovery;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author Aidan Follestad (afollestad)
 */
class Util {

    public static boolean isWifiConnected(@NonNull Context context) {
        if (!hasInternetPermission(context)) {
            throw new IllegalStateException("Cannot check internet connectivity without INTERNET permission!");
        }
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifi.isConnectedOrConnecting();
    }

    public static boolean hasInternetPermission(@NonNull Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) ==
                PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasAccessNetworkState(@NonNull Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE) ==
                PackageManager.PERMISSION_GRANTED;
    }

    public static void handleError(@Nullable final ErrorListener listener, @NonNull final Throwable t,
                                   @Nullable final String message, @NonNull final Handler handler) {
        handler.post(new Runnable() {
            @Override public void run() {
                handleError(listener, t, message);
            }
        });
    }

    public static void handleError(@Nullable ErrorListener listener, @NonNull Throwable t, @Nullable String message) {
        if (message != null) {
            t = new RuntimeException(message, t);
        }
        if (listener != null) {
            listener.onError(t);
        } else {
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            throw new RuntimeException(t);
        }
    }

    @NonNull
    public static InetAddress getBroadcastAddress(@NonNull Context context) throws IOException {
        if (!hasAccessNetworkState(context)) {
            throw new IllegalStateException("Cannot get broadcast address without ACCESS_WIFI_STATE permission!");
        }
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        if (dhcp == null) {
            throw new IllegalStateException("No DHCP info!");
        }
        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

    @Nullable
    public static String getWifiIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().contains("wlan")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                            .hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()
                                && (inetAddress.getAddress().length == 4)) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
