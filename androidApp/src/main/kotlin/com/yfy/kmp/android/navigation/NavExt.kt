package com.yfy.kmp.android.navigation

import com.yfy.kmp.core.common.ext.toSnakeCase

internal fun screenNameOf(route: String?): String? = route
    ?.substringBefore('/')
    ?.substringAfterLast('.')
    ?.removeSuffix("Route")
    ?.takeIf { it.isNotBlank() }
    ?.toSnakeCase()
