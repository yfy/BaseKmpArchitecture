package com.yfy.kmp.feature.auth.data

import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.yfy.kmp.core.common.CurrentActivityHolder
import com.yfy.kmp.feature.auth.domain.SocialAuthProvider
import com.yfy.kmp.feature.auth.domain.SocialProvider

// TODO(template): replace before release — Google server client id from the Google/Firebase console.
// It must be the WEB client id, not the Android one, or Credential Manager rejects the request.
public object GoogleAuthConfig {
    public const val SERVER_CLIENT_ID: String = "GOOGLE_SERVER_CLIENT_ID.apps.googleusercontent.com"
}

internal class GoogleAuthProvider : SocialAuthProvider {
    override suspend fun authenticate(provider: SocialProvider): String {
        if (provider != SocialProvider.GOOGLE) {
            return "dummy-${provider.name.lowercase()}-token"
        }
        val activity = CurrentActivityHolder.activity
            ?: error("Credential isteği için aktif Activity yok")
        val option = GetGoogleIdOption.Builder()
            .setServerClientId(GoogleAuthConfig.SERVER_CLIENT_ID)
            .setFilterByAuthorizedAccounts(false)
            .build()
        val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
        val response = CredentialManager.create(activity).getCredential(activity, request)
        return GoogleIdTokenCredential.createFrom(response.credential.data).idToken
    }
}
