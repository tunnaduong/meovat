package com.tunnaduong.meovat.ui.theme

import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.tunnaduong.meovat.R

val BeVietnamPro = FontFamily(
    Font(R.font.be_vietnam_pro_regular, FontWeight.Normal),
    Font(R.font.be_vietnam_pro_medium, FontWeight.Medium),
    Font(R.font.be_vietnam_pro_semibold, FontWeight.SemiBold),
    Font(R.font.be_vietnam_pro_bold, FontWeight.Bold),
)

val Inter = FontFamily(Font(R.font.inter_semibold, FontWeight.SemiBold))

/** Typography tokens: "Be Vietnam Pro" text styles plus the Inter "Title 5" display style. */
object AppType {
    private fun style(size: Int, lineHeight: Int, weight: FontWeight, family: FontFamily = BeVietnamPro) = TextStyle(
        fontFamily = family,
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = lineHeight.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
        lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
    )

    val title5 = style(48, 52, FontWeight.SemiBold, Inter).copy(letterSpacing = (-1.5).sp)

    val textXS = style(12, 16, FontWeight.Normal)
    val textXSMedium = style(12, 16, FontWeight.Medium)
    val textSM = style(14, 20, FontWeight.Normal)
    val textSMMedium = style(14, 20, FontWeight.Medium)
    val textSMSemibold = style(14, 20, FontWeight.SemiBold)
    val textMD = style(16, 24, FontWeight.Normal)
    val textMDMedium = style(16, 24, FontWeight.Medium)
    val textMDSemibold = style(16, 24, FontWeight.SemiBold)
    val textLGMedium = style(18, 28, FontWeight.Medium)
    val textLGSemibold = style(18, 28, FontWeight.SemiBold)
    val headingSM = style(20, 28, FontWeight.SemiBold)
    val headingMD = style(24, 32, FontWeight.SemiBold)
}
