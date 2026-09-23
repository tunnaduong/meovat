package com.fatties.meovat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fatties.meovat.R
import com.fatties.meovat.ui.theme.AppColor
import com.fatties.meovat.ui.theme.AppType

@Composable
fun PrimaryButton(title: String, onClick: () -> Unit, icon: Int? = null, enabled: Boolean = true, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(CircleShape)
            .background(AppColor.Primary.copy(alpha = if (enabled) 1f else 0.4f))
            .clickable(enabled = enabled, onClick = onClick),
    ) {
        if (icon != null) AppIcon(icon, Color.White, 22.dp)
        Text(title, style = AppType.textMDSemibold, color = Color.White)
    }
}

@Composable
fun RadioIndicator(selected: Boolean) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(24.dp)
            .background(if (selected) AppColor.Primary else Color.White, CircleShape)
            .then(if (selected) Modifier else Modifier.border(1.dp, AppColor.Border, CircleShape)),
    ) {
        if (selected) Box(Modifier.size(10.dp).background(Color.White, CircleShape))
    }
}

@Composable
fun FilterChip(title: String, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(8.dp)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(36.dp)
            .clip(shape)
            .background(if (selected) AppColor.Primary else AppColor.Surface)
            .then(if (selected) Modifier else Modifier.border(1.dp, AppColor.Border, shape))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
    ) {
        Text(title, style = AppType.textSMMedium, color = if (selected) Color.White else AppColor.Heading)
    }
}

@Composable
fun CheckRow(title: String, checked: Boolean, onToggle: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onToggle),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(24.dp)) {
            if (checked) {
                AppIcon(R.drawable.ic_check, AppColor.Primary, 24.dp)
            } else {
                Box(Modifier.size(24.dp).background(Color.White, CircleShape).border(1.dp, AppColor.Border, CircleShape))
            }
        }
        Text(title, style = AppType.textMD, color = AppColor.Heading)
    }
}

/** Small "Phân loại 1" outline badge. */
@Composable
fun TagBadge(text: String) {
    val shape = RoundedCornerShape(6.dp)
    Text(
        text,
        style = AppType.textXSMedium,
        color = AppColor.Primary,
        modifier = Modifier
            .background(AppColor.Surface, shape)
            .border(1.dp, AppColor.Primary, shape)
            .padding(horizontal = 12.dp, vertical = 4.dp),
    )
}

/** Grey rounded container listing selectable rows with inset dividers. */
@Composable
fun <T> OptionList(items: List<T>, row: @Composable (T) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColor.CanvasSecondary, RoundedCornerShape(12.dp)),
    ) {
        items.forEachIndexed { index, item ->
            row(item)
            if (index < items.lastIndex) {
                HorizontalDivider(color = AppColor.Divider, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
fun OptionRow(emoji: String, title: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick)
            .padding(horizontal = 16.dp),
    ) {
        Text(emoji, fontSize = 20.sp, lineHeight = 24.sp)
        Text(title, style = AppType.textMD, color = if (selected) AppColor.Primary else AppColor.Heading, modifier = Modifier.weight(1f))
        RadioIndicator(selected)
    }
}

@Composable
fun FormField(label: String, placeholder: String, value: String, onValueChange: (String) -> Unit) {
    val shape = RoundedCornerShape(8.dp)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = AppType.textMDMedium, color = AppColor.Heading)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = AppType.textMD.copy(color = AppColor.Heading),
            cursorBrush = SolidColor(AppColor.Primary),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(AppColor.Surface, shape)
                .border(1.dp, AppColor.Border, shape)
                .padding(horizontal = 16.dp),
            decorationBox = { inner ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) Text(placeholder, style = AppType.textMD, color = AppColor.Disabled)
                    inner()
                }
            },
        )
    }
}
