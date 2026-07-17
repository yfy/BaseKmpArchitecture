package com.yfy.kmp.android

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yfy.kmp.android.navigation.AppNavHost
import com.yfy.kmp.core.common.permission.AndroidPermissionRequester
import com.yfy.kmp.core.datastore.AppLanguage
import com.yfy.kmp.core.datastore.AppThemeMode
import com.yfy.kmp.core.designsystem.components.AppUiHost
import com.yfy.kmp.core.designsystem.theme.AppTheme
import com.yfy.kmp.core.model.AppRoute
import com.yfy.kmp.core.model.parseAppRoute
import com.yfy.kmp.core.notification.NOTIFICATION_ROUTE_KEY
import com.yfy.kmp.feature.settings.presentation.LanguageViewModel
import com.yfy.kmp.feature.settings.presentation.ThemeViewModel
import java.util.Locale
import org.koin.android.ext.android.get
import org.koin.compose.getKoin

class MainActivity : ComponentActivity() {

    private val deepLink = mutableStateOf<AppRoute?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        var keepSplash = true
        splashScreen.setKeepOnScreenCondition { keepSplash }
        super.onCreate(savedInstanceState)
        window.decorView.postDelayed({ keepSplash = false }, 900L)
        wirePermissionSeam()
        deepLink.value = routeFromIntent(intent)
        setContent {
            enableEdgeToEdge()
            val koin = getKoin()
            val themeViewModel = remember(koin) { koin.get<ThemeViewModel>() }
            val languageViewModel = remember(koin) { koin.get<LanguageViewModel>() }
            val mode by themeViewModel.state.collectAsStateWithLifecycle()
            val language by languageViewModel.state.collectAsStateWithLifecycle()
            val darkTheme = when (mode) {
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
            }
            LocalizedApp(language) {
                AppTheme(darkTheme = darkTheme) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        AppUiHost {
                            AppNavHost(
                                deepLink = deepLink.value,
                                onDeepLinkHandled = { deepLink.value = null },
                            )
                        }
                    }
                }
            }
        }
    }
    private fun wirePermissionSeam() {
        val requester = get<AndroidPermissionRequester>()
        val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            requester.onResult(granted)
        }
        requester.requestLauncher = { permission -> launcher.launch(permission) }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        routeFromIntent(intent)?.let { deepLink.value = it }
    }

    private fun routeFromIntent(intent: Intent?): AppRoute? {
        val uri = intent?.data?.toString()
        val routeExtra = intent?.getStringExtra(NOTIFICATION_ROUTE_KEY)
        return parseAppRoute(uri = uri ?: routeExtra)
    }
}

@Composable
private fun LocalizedApp(language: AppLanguage, content: @Composable () -> Unit) {
    val baseContext = LocalContext.current
    val baseConfiguration = LocalConfiguration.current
    val code = language.code
    if (code == null) {
        content()
        return
    }
    val locale = remember(code) { Locale(code) }
    val localizedContext = remember(locale, baseContext, baseConfiguration) {
        val config = Configuration(baseConfiguration).apply { setLocale(locale) }
        baseContext.createConfigurationContext(config)
    }
    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalConfiguration provides localizedContext.resources.configuration,
    ) {
        content()
    }
}
