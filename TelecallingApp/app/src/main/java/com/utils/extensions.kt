package com.telecalling.crm

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // For Android 5.0 (Lollipop) and above
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.run {
            hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                    hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)
        }
    } else {
        // For devices below Android 5.0, fallback to using NetworkInfo
        @Suppress("DEPRECATION")
        return (connectivityManager.activeNetworkInfo?.isConnected == true)
    }
}
