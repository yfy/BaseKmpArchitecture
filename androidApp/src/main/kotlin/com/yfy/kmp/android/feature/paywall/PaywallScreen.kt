package com.yfy.kmp.android.feature.paywall

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yfy.kmp.android.R
import com.yfy.kmp.core.designsystem.components.AppButton
import com.yfy.kmp.core.designsystem.components.AppErrorText
import com.yfy.kmp.core.designsystem.components.AppOutlinedButton
import com.yfy.kmp.core.designsystem.components.AppScreenScaffold
import com.yfy.kmp.core.designsystem.components.AppSuccessText
import com.yfy.kmp.feature.paywall.domain.PaywallProduct
import com.yfy.kmp.feature.paywall.presentation.PaywallUiError
import com.yfy.kmp.feature.paywall.presentation.PaywallViewModel

@Composable
fun PaywallScreen(
    viewModel: PaywallViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AppScreenScaffold(title = stringResource(R.string.paywall_title), onBack = onBack) {
        Text(stringResource(R.string.paywall_subtitle), color = MaterialTheme.colorScheme.onSurfaceVariant)

        if (state.purchased) AppSuccessText(stringResource(R.string.paywall_purchased))
        state.error?.let { AppErrorText(it.toMessage()) }

        state.products.forEach { product ->
            ProductCard(product, enabled = !state.isLoading) { viewModel.purchase(product.id) }
        }

        AppOutlinedButton(text = stringResource(R.string.paywall_restore), onClick = viewModel::restore, enabled = !state.isLoading)
    }
}

@Composable
private fun ProductCard(product: PaywallProduct, enabled: Boolean, onBuy: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(product.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(product.priceLabel, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            Text(product.description, color = MaterialTheme.colorScheme.onSurfaceVariant)
            AppButton(text = stringResource(R.string.paywall_purchase), onClick = onBuy, enabled = enabled)
        }
    }
}

@Composable
private fun PaywallUiError.toMessage(): String = when (this) {
    PaywallUiError.FAILED -> stringResource(R.string.paywall_failed)
}
