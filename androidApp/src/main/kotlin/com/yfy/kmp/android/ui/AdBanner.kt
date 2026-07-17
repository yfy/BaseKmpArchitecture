package com.yfy.kmp.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.yfy.kmp.android.BuildConfig
import com.yfy.kmp.shared.NativeIntegrations

// TODO(template): replace before release — AdMob banner unit id (Google's TEST id; safe for development).
private const val BANNER_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

@Composable
fun AdBanner(modifier: Modifier = Modifier) {
    if (!NativeIntegrations.admob || BuildConfig.APP_ENVIRONMENT != "PROD") return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        AndroidView(
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = BANNER_UNIT_ID
                    loadAd(AdRequest.Builder().build())
                }
            },
        )
    }
}
