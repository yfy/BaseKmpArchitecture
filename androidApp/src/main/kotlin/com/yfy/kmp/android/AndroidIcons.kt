package com.yfy.kmp.android

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

object AndroidIcons {
    val Back: ImageVector get() = Icons.AutoMirrored.Filled.ArrowBack

    @Composable
    fun googlePainter(): Painter = painterResource(id = R.drawable.ic_google)

    fun Modifier.smallIcon(size: Dp = 20.dp): Modifier = this.size(size)
}


