package com.yfy.kmp.android.feature.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yfy.kmp.android.R
import com.yfy.kmp.android.AndroidIcons
import com.yfy.kmp.core.designsystem.components.AppButton
import com.yfy.kmp.core.designsystem.theme.LocalAppExtraColors
import com.yfy.kmp.core.designsystem.components.AppErrorText
import com.yfy.kmp.core.designsystem.components.AppOrDivider
import com.yfy.kmp.core.designsystem.components.AppOutlinedButton
import com.yfy.kmp.core.designsystem.components.AppPasswordField
import com.yfy.kmp.core.designsystem.components.AppTextField
import com.yfy.kmp.feature.auth.domain.SocialProvider
import com.yfy.kmp.feature.auth.presentation.LoginUiError
import com.yfy.kmp.feature.auth.presentation.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoggedIn: () -> Unit,
    onGoSignup: () -> Unit,
    onGoForgot: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val extra = LocalAppExtraColors.current

    LaunchedEffect(state.user) { if (state.user != null) onLoggedIn() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(extra.gradientTop, extra.gradientBottom))),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(56.dp))
            Image(
                painter = painterResource(R.drawable.app_logo),
                contentDescription = null,
                modifier = Modifier.size(88.dp),
            )
            Text(
                stringResource(R.string.brand_name),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                stringResource(R.string.login_welcome_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                stringResource(R.string.login_welcome_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 28.dp),
            )

            AppTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChange,
                label = stringResource(R.string.field_email),
                keyboardType = KeyboardType.Email,
            )
            Spacer(Modifier.height(12.dp))
            AppPasswordField(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                label = stringResource(R.string.field_password),
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(checked = state.rememberMe, onCheckedChange = viewModel::onRememberMeChange)
                Text(stringResource(R.string.remember_me))
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onGoForgot) {
                    Text(stringResource(R.string.login_go_forgot), color = MaterialTheme.colorScheme.primary)
                }
            }

            state.error?.let { AppErrorText(it.toMessage(), modifier = Modifier.padding(bottom = 4.dp)) }

            AppButton(
                text = stringResource(R.string.login_submit),
                onClick = viewModel::login,
                loading = state.isLoading,
                modifier = Modifier.padding(top = 4.dp),
            )

            AppOrDivider(stringResource(R.string.or_divider), modifier = Modifier.padding(vertical = 16.dp))

            BrandSocialButton(
                text = stringResource(R.string.continue_with_google),
                onClick = { viewModel.socialLogin(SocialProvider.GOOGLE) },
                enabled = !state.isLoading,
                container = extra.socialGoogleBg,
                content = extra.socialGoogleText,
                border = extra.socialGoogleBorder,
                leading = {
                    Image(
                        painter = AndroidIcons.googlePainter(),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                },
            )
            Spacer(Modifier.height(12.dp))
            BrandSocialButton(
                text = stringResource(R.string.continue_with_apple),
                onClick = { viewModel.socialLogin(SocialProvider.APPLE) },
                enabled = !state.isLoading,
                container = extra.socialAppleBg,
                content = extra.socialAppleText,
                border = null,
                leading = {
                    Icon(
                        painter = painterResource(R.drawable.ic_apple),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                },
            )
            Spacer(Modifier.height(12.dp))
            AppOutlinedButton(
                text = stringResource(R.string.continue_with_instagram),
                onClick = { viewModel.socialLogin(SocialProvider.INSTAGRAM) },
                enabled = !state.isLoading,
                leadingContent = {
                    Icon(
                        painter = painterResource(R.drawable.ic_instagram),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                },
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.login_no_account))
                TextButton(onClick = onGoSignup) {
                    Text(stringResource(R.string.signup_link), color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun BrandSocialButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    container: Color,
    content: Color,
    border: Color?,
    leading: @Composable () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(27.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = content,
            disabledContainerColor = container.copy(alpha = 0.6f),
            disabledContentColor = content.copy(alpha = 0.6f),
        ),
        border = border?.let { BorderStroke(1.dp, it) },
    ) {
        leading()
        Spacer(Modifier.size(10.dp))
        Text(text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun LoginUiError.toMessage(): String = when (this) {
    LoginUiError.EMAIL_FORMAT -> stringResource(R.string.error_email_format)
    LoginUiError.PASSWORD_TOO_SHORT -> stringResource(R.string.error_password_too_short)
    LoginUiError.INVALID_CREDENTIALS -> stringResource(R.string.error_invalid_credentials)
    LoginUiError.NETWORK -> stringResource(R.string.error_network)
    LoginUiError.SERVER -> stringResource(R.string.error_server)
    LoginUiError.UNKNOWN -> stringResource(R.string.error_unknown)
}
