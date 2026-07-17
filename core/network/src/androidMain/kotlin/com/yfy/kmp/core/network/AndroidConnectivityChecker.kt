package com.yfy.kmp.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

internal class AndroidConnectivityChecker(private val context: Context) : ConnectivityChecker {
    override fun isOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return true
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
