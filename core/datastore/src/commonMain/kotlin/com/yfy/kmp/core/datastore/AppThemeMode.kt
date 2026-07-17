package com.yfy.kmp.core.datastore

public enum class AppThemeMode {
    SYSTEM, LIGHT, DARK;

    public companion object {
        public fun parse(raw: String?): AppThemeMode =
            entries.firstOrNull { it.name == raw } ?: SYSTEM
    }
}
