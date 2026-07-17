package com.yfy.kmp.core.datastore

public enum class AppLanguage {
    SYSTEM, TR, EN;

    public val code: String?
        get() = when (this) {
            SYSTEM -> null
            TR -> "tr"
            EN -> "en"
        }

    public companion object {
        public fun parse(raw: String?): AppLanguage =
            entries.firstOrNull { it.name == raw } ?: SYSTEM
    }
}
