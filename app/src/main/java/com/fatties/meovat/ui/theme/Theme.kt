package com.fatties.meovat.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = AppColor.Primary,
    onPrimary = AppColor.Canvas,
    primaryContainer = AppColor.PrimarySubtle,
    onPrimaryContainer = AppColor.Primary,
    background = AppColor.Canvas,
    onBackground = AppColor.Heading,
    surface = AppColor.Surface,
    onSurface = AppColor.Heading,
    surfaceVariant = AppColor.CanvasSecondary,
    onSurfaceVariant = AppColor.Caption,
    outline = AppColor.Border,
)

@Composable
fun MeoVatTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography(
            bodyLarge = AppType.textMD,
            bodyMedium = AppType.textSM,
            labelLarge = AppType.textMDSemibold,
        ),
        content = content,
    )
}
