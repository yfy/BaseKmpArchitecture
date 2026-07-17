package com.yfy.kmp.core.network

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Network.nw_path_get_status
import platform.Network.nw_path_monitor_create
import platform.Network.nw_path_monitor_set_queue
import platform.Network.nw_path_monitor_set_update_handler
import platform.Network.nw_path_monitor_start
import platform.Network.nw_path_status_satisfied
import platform.darwin.dispatch_get_global_queue
import kotlin.concurrent.Volatile

// NWPathMonitor only reports asynchronously, so status is cached; the monitor is never cancelled
// and must outlive this init, hence it is intentionally left running for the process lifetime.
@OptIn(ExperimentalForeignApi::class)
internal class IosConnectivityChecker : ConnectivityChecker {

    @Volatile
    private var online: Boolean = true

    init {
        val monitor = nw_path_monitor_create()
        nw_path_monitor_set_update_handler(monitor) { path ->
            online = nw_path_get_status(path) == nw_path_status_satisfied
        }
        nw_path_monitor_set_queue(monitor, dispatch_get_global_queue(0, 0u))
        nw_path_monitor_start(monitor)
    }

    override fun isOnline(): Boolean = online
}
