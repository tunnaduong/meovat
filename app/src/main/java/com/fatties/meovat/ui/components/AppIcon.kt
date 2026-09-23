package com.fatties.meovat.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Tinted vector icon; every icon drawable is normalized to a 24dp box so [size] scales the whole glyph. */
@Composable
fun AppIcon(@DrawableRes id: Int, tint: Color, size: Dp = 24.dp, modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(id),
        contentDescription = null,
        tint = tint,
        modifier = modifier.size(size),
    )
}
