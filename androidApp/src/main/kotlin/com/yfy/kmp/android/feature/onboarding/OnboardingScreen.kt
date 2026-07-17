package com.yfy.kmp.android.feature.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yfy.kmp.android.R
import com.yfy.kmp.core.designsystem.components.AppPageIndicator
import com.yfy.kmp.core.designsystem.theme.AppOnboardingBg
import com.yfy.kmp.feature.onboarding.presentation.OnboardingViewModel

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onFinished: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.completed) { if (state.completed) onFinished() }

    val pages = listOf(
        Triple(R.string.onboarding_page1_title, R.string.onboarding_page1_desc, Icons.Filled.AutoAwesome),
        Triple(R.string.onboarding_page2_title, R.string.onboarding_page2_desc, Icons.Filled.Bolt),
        Triple(R.string.onboarding_page3_title, R.string.onboarding_page3_desc, Icons.Filled.CheckCircle),
    )
    val index = state.pageIndex.coerceIn(0, pages.lastIndex)
    val page = pages[index]
    val isLast = index == viewModel.pageCount - 1

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.58f)
                .background(AppOnboardingBg),
        ) {
            Icon(
                imageVector = page.third,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(140.dp).align(Alignment.Center),
            )
            TextButton(
                onClick = viewModel::skip,
                modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(8.dp),
            ) {
                Text(stringResource(R.string.onboarding_skip), color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.42f)
                .navigationBarsPadding()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                stringResource(page.first),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                stringResource(page.second),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp),
            )
            AppPageIndicator(
                count = viewModel.pageCount,
                selectedIndex = index,
                modifier = Modifier.padding(top = 24.dp),
            )
            Box(modifier = Modifier.fillMaxWidth().padding(top = 32.dp), contentAlignment = Alignment.CenterEnd) {
                Button(
                    onClick = viewModel::next,
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier.height(52.dp),
                ) {
                    Text(
                        stringResource(if (isLast) R.string.onboarding_start else R.string.onboarding_next),
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}
