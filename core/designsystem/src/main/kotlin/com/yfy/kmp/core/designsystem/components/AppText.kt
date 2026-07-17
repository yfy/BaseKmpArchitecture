package com.yfy.kmp.core.designsystem.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppErrorText(message: String, modifier: Modifier = Modifier) {
    Text(text = message, color = MaterialTheme.colorScheme.error, modifier = modifier)
}

@Composable
fun AppSuccessText(message: String, modifier: Modifier = Modifier) {
    Text(text = message, color = MaterialTheme.colorScheme.primary, modifier = modifier)
}
