package com.yfy.kmp.core.common.ext

private val camelCaseBoundary = Regex("(?<=[a-z0-9])(?=[A-Z])")

public fun String.toSnakeCase(): String = camelCaseBoundary.replace(this, "_").lowercase()

public fun String.isValidEmail(): Boolean = isNotBlank() && contains("@")
