package com.tunnaduong.meovat.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tunnaduong.meovat.R
import com.tunnaduong.meovat.data.Photos
import com.tunnaduong.meovat.data.SavedList
import com.tunnaduong.meovat.data.Tip
import com.tunnaduong.meovat.data.TipCategory
import com.tunnaduong.meovat.ui.theme.AppColor
import com.tunnaduong.meovat.ui.theme.AppType

/** White card with 1dp border, 12dp radius and the "Raised" shadow. */
fun Modifier.card(cornerRadius: Dp = 12.dp): Modifier {
    val shape = RoundedCornerShape(cornerRadius)
    return this
        .shadow(3.dp, shape, ambientColor = Color.Black.copy(alpha = 0.10f), spotColor = Color.Black.copy(alpha = 0.10f))
        .background(AppColor.Surface, shape)
        .border(1.dp, AppColor.Border, shape)
        .clip(shape)
}

@Composable
fun CategoryCard(category: TipCategory, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .card()
            .clickable(onClick = onClick)
            .height(92.dp)
            .padding(horizontal = 16.dp),
    ) {
        Text(category.emoji, fontSize = 36.sp, lineHeight = 44.sp)
        Column(modifier = Modifier.weight(1f)) {
            Text(category.name, style = AppType.textLGMedium, color = AppColor.Heading)
            Text(category.description, style = AppType.textSM, color = AppColor.Caption, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        AppIcon(R.drawable.ic_arrow_right, AppColor.Primary, 24.dp)
    }
}

data class MenuAction(val label: String, val destructive: Boolean = false, val onClick: () -> Unit)

@Composable
fun TipCard(tip: Tip, onClick: () -> Unit, actions: List<MenuAction>, modifier: Modifier = Modifier) {
    var menuOpen by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .card()
            .clickable(onClick = onClick)
            .height(96.dp)
            .padding(start = 6.dp, top = 6.dp, bottom = 6.dp, end = 10.dp),
    ) {
        TipPhoto(
            url = tip.imageUrl,
            fallback = tip.image,
            modifier = Modifier
                .size(width = 64.dp, height = 84.dp)
                .clip(RoundedCornerShape(8.dp)),
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(tip.title, style = AppType.textMDMedium, color = AppColor.Heading, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(tip.subtitle, style = AppType.textSM, color = AppColor.Caption, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Box {
            IconCircleButton(R.drawable.ic_more_vertical, onClick = { menuOpen = true }, tint = AppColor.Caption, iconSize = 20.dp)
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }, containerColor = AppColor.Surface) {
                actions.forEach { action ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                action.label,
                                style = AppType.textMD,
                                color = if (action.destructive) Color(0xFFD92D20) else AppColor.Heading,
                            )
                        },
                        onClick = {
                            menuOpen = false
                            action.onClick()
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SavedListCard(list: SavedList, onClick: () -> Unit, onLongClick: (() -> Unit)? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .card()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .height(92.dp)
            .padding(horizontal = 16.dp),
    ) {
        Text(list.emoji ?: "📁", fontSize = 36.sp, lineHeight = 44.sp)
        Column(modifier = Modifier.weight(1f)) {
            Text(list.name, style = AppType.textLGMedium, color = AppColor.Heading)
            Text(list.description, style = AppType.textSM, color = AppColor.Caption, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .background(AppColor.PrimarySubtle, CircleShape),
        ) {
            Text("${list.tipIds.size}", style = AppType.textSMMedium, color = AppColor.Primary)
        }
    }
}
