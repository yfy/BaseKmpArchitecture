package com.yfy.kmp.core.common.permission.di

import com.yfy.kmp.core.common.permission.IosPermissionController
import com.yfy.kmp.core.common.permission.PermissionController
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val platformPermissionModule: Module = module {
    single<PermissionController> { IosPermissionController() }
}
