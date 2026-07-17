package com.yfy.kmp.feature.auth.domain

import com.yfy.kmp.core.model.AuthUser

public sealed interface LoginOutcome {
    public data class Success(val user: AuthUser) : LoginOutcome
    public data object InvalidCredentials : LoginOutcome
    public data class InvalidInput(val reason: InputError) : LoginOutcome
}

public enum class InputError {
    EMAIL_FORMAT,
    PASSWORD_TOO_SHORT,
    PASSWORD_MISMATCH,
    NAME_REQUIRED,
    CODE_FORMAT,
}
