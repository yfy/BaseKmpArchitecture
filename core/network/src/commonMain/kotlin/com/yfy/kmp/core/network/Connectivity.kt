package com.yfy.kmp.core.network

import com.yfy.kmp.core.common.result.NoConnectivityException
import io.ktor.client.plugins.api.createClientPlugin

public interface ConnectivityChecker {
    public fun isOnline(): Boolean
}

internal class ConnectivityPluginConfig {
    var checker: ConnectivityChecker? = null
}

internal val ConnectivityPlugin = createClientPlugin("ConnectivityPlugin", ::ConnectivityPluginConfig) {
    val checker = pluginConfig.checker
    onRequest { _, _ ->
        if (checker?.isOnline() == false) throw NoConnectivityException()
    }
}
