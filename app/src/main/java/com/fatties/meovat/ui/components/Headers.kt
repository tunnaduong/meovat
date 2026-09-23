package com.fatties.meovat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fatties.meovat.R
import com.fatties.meovat.ui.theme.AppColor
import com.fatties.meovat.ui.theme.AppType

/** Large grey header with the 48sp Inter title (Home / Saved / Settings roots). */
@Composable
fun PageHeader(title: String, subtitle: String, trailing: @Composable () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColor.CanvasSecondary)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
            Text(title, style = AppType.title5, color = AppColor.Primary)
            Text(subtitle, style = AppType.textMD, color = AppColor.Caption)
        }
        trailing()
    }
}

/** Grey header with a back chevron and a 24sp orange title, plus optional extra rows (search etc.). */
@Composable
fun SubPageHeader(
    title: String,
    onBack: () -> Unit,
    trailing: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColor.CanvasSecondary)
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconCircleButton(R.drawable.ic_chevron_left, onClick = onBack)
            Text(
                title,
                style = AppType.headingMD,
                color = AppColor.Primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            trailing()
        }
        content()
    }
}

/** 36dp tappable icon without a visible background (back / close / gear). */
@Composable
fun IconCircleButton(iconId: Int, onClick: () -> Unit, tint: androidx.compose.ui.graphics.Color = AppColor.Primary, iconSize: androidx.compose.ui.unit.Dp = 24.dp, background: androidx.compose.ui.graphics.Color? = null) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .then(if (background != null) Modifier.background(background) else Modifier)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick),
    ) {
        AppIcon(iconId, tint, iconSize)
    }
}

/** Search field + round filter button row used under sub-page titles. */
@Composable
fun SearchRow(text: String, onTextChange: (String) -> Unit, placeholder: String, onFilter: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .background(AppColor.Surface, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp),
        ) {
            AppIcon(R.drawable.ic_search, AppColor.Disabled, 20.dp)
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                singleLine = true,
                textStyle = AppType.textMD.copy(color = AppColor.Heading),
                cursorBrush = SolidColor(AppColor.Primary),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier.weight(1f),
                decorationBox = { inner ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (text.isEmpty()) Text(placeholder, style = AppType.textMD, color = AppColor.Disabled)
                        inner()
                    }
                },
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(AppColor.Surface)
                .clickable(onClick = onFilter),
        ) {
            AppIcon(R.drawable.ic_filter, AppColor.Primary, 20.dp)
        }
    }
}
