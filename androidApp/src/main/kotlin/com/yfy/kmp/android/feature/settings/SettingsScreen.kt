package com.yfy.kmp.android.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yfy.kmp.android.R
import com.yfy.kmp.core.datastore.AppLanguage
import com.yfy.kmp.core.datastore.AppThemeMode
import com.yfy.kmp.core.designsystem.components.AppScreenScaffold
import com.yfy.kmp.feature.settings.presentation.LanguageViewModel
import com.yfy.kmp.feature.settings.presentation.SettingsViewModel
import com.yfy.kmp.feature.settings.presentation.ThemeViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    themeViewModel: ThemeViewModel,
    languageViewModel: LanguageViewModel,
    onBack: () -> Unit,
    onChangePassword: () -> Unit,
    onTwoFactor: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val themeMode by themeViewModel.state.collectAsStateWithLifecycle()
    val language by languageViewModel.state.collectAsStateWithLifecycle()

    AppScreenScaffold(title = stringResource(R.string.settings_title), onBack = onBack) {
        Text(stringResource(R.string.settings_theme), style = MaterialTheme.typography.titleSmall)
        ThemeSelector(themeMode, themeViewModel::setMode)

        HorizontalDivider()

        Text(stringResource(R.string.settings_language), style = MaterialTheme.typography.titleSmall)
        LanguageSelector(language, languageViewModel::setLanguage)

        HorizontalDivider()

        SettingRow(
            stringResource(R.string.settings_notifications),
            state.notificationsEnabled,
            viewModel::setNotificationsEnabled,
        )

        HorizontalDivider()

        NavRow(stringResource(R.string.change_password_title), onChangePassword)
        NavRow(stringResource(R.string.two_factor_title), onTwoFactor)

        HorizontalDivider()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(stringResource(R.string.settings_version))
            Text("1.0", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun NavRow(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(role = Role.Button, onClick = onClick).padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label)
        Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = null)
    }
}

@Composable
private fun ThemeSelector(mode: AppThemeMode, onSelect: (AppThemeMode) -> Unit) {
    val options = listOf(
        AppThemeMode.SYSTEM to R.string.theme_system,
        AppThemeMode.LIGHT to R.string.theme_light,
        AppThemeMode.DARK to R.string.theme_dark,
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { (value, label) ->
            FilterChip(
                selected = mode == value,
                onClick = { onSelect(value) },
                label = { Text(stringResource(label)) },
            )
        }
    }
}

@Composable
private fun LanguageSelector(language: AppLanguage, onSelect: (AppLanguage) -> Unit) {
    val options = listOf(
        AppLanguage.SYSTEM to R.string.language_system,
        AppLanguage.TR to R.string.language_turkish,
        AppLanguage.EN to R.string.language_english,
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { (value, label) ->
            FilterChip(
                selected = language == value,
                onClick = { onSelect(value) },
                label = { Text(stringResource(label)) },
            )
        }
    }
}

@Composable
private fun SettingRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
