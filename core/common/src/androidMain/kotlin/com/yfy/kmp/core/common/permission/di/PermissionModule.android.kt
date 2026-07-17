package com.yfy.kmp.core.common.permission.di

import com.yfy.kmp.core.common.permission.AndroidPermissionController
import com.yfy.kmp.core.common.permission.AndroidPermissionRequester
import com.yfy.kmp.core.common.permission.PermissionController
import org.koin.core.module.Module
import org.koin.dsl.binds
import org.koin.dsl.module

internal actual val platformPermissionModule: Module = module {
    single { AndroidPermissionController(get()) } binds
        arrayOf(PermissionController::class, AndroidPermissionRequester::class)
}
