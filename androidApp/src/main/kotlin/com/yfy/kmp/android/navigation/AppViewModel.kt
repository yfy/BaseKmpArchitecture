package com.yfy.kmp.android.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.compose.getKoin

@Composable
internal inline fun <reified VM : ViewModel> appViewModel(): VM {
    val koin = getKoin()
    return viewModel { koin.get<VM>() }
}
