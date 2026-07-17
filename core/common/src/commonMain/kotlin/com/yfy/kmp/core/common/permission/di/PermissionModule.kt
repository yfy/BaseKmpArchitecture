package com.yfy.kmp.core.common.permission.di

import org.koin.core.module.Module
import org.koin.dsl.module

internal expect val platformPermissionModule: Module

public val permissionModule: Module = module {
    includes(platformPermissionModule)
}
