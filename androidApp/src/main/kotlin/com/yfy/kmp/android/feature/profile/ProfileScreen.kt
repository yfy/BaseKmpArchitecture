package com.yfy.kmp.android.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yfy.kmp.android.R
import com.yfy.kmp.core.designsystem.components.AppButton
import com.yfy.kmp.core.designsystem.components.AppScreenScaffold
import com.yfy.kmp.core.model.AuthUser
import com.yfy.kmp.feature.profile.presentation.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
    onGoPremium: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AppScreenScaffold(title = stringResource(R.string.profile_title), onBack = onBack) {
        when {
            state.isLoading -> CircularProgressIndicator()
            state.user != null -> ProfileContent(state.user!!)
            else -> Text(stringResource(R.string.profile_not_found))
        }
        AppButton(text = stringResource(R.string.profile_go_premium), onClick = onGoPremium)
        AppButton(text = stringResource(R.string.profile_settings), onClick = onOpenSettings)
    }
}

@Composable
private fun ProfileContent(user: AuthUser) {
    val name = user.displayName ?: user.username

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Avatar(name)
        Text(name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(user.email, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(
                onClick = {},
                label = {
                    Text(
                        if (user.isPremium) stringResource(R.string.profile_premium)
                        else stringResource(R.string.profile_member),
                    )
                },
                leadingIcon = {
                    Icon(Icons.Filled.WorkspacePremium, contentDescription = null, modifier = Modifier.size(18.dp))
                },
            )
            if (user.isVerified) {
                AssistChip(
                    onClick = {},
                    label = { Text(stringResource(R.string.profile_verified)) },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Verified,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp),
                        )
                    },
                )
            }
        }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                stringResource(R.string.profile_account),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
            )
            InfoRow(stringResource(R.string.profile_username), user.username)
            HorizontalDivider()
            InfoRow(stringResource(R.string.field_email), user.email)
        }
    }
}

@Composable
private fun Avatar(name: String) {
    Box(
        modifier = Modifier
            .size(88.dp)
            .background(MaterialTheme.colorScheme.primary, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            name.take(1).uppercase(),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Medium)
    }
}
