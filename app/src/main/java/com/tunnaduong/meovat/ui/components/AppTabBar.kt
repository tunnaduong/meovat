package com.tunnaduong.meovat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tunnaduong.meovat.R
import com.tunnaduong.meovat.data.L10n
import com.tunnaduong.meovat.ui.theme.AppColor
import com.tunnaduong.meovat.ui.theme.AppType

enum class AppTab(val icon: Int, val route: String) {
    HOME(R.drawable.ic_house, "home_graph"),
    SAVED(R.drawable.ic_bookmark_check, "saved_graph"),
    SETTINGS(R.drawable.ic_settings, "settings_graph"),
}

/** Floating pill tab bar ("Frame 90" in the design). */
@Composable
fun AppTabBar(selected: AppTab, l10n: L10n, onSelect: (AppTab) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .height(68.dp)
            .background(AppColor.CanvasSecondary, CircleShape)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppTab.entries.forEach { tab ->
            val isSelected = tab == selected
            val tint = if (isSelected) AppColor.Primary else AppColor.Disabled
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .width(97.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) AppColor.PrimarySubtle else AppColor.CanvasSecondary)
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onSelect(tab) }
                    .padding(vertical = 4.dp),
            ) {
                AppIcon(tab.icon, tint, 28.dp)
                Text(
                    text = when (tab) {
                        AppTab.HOME -> l10n.tabHome
                        AppTab.SAVED -> l10n.tabSaved
                        AppTab.SETTINGS -> l10n.tabSettings
                    },
                    style = if (isSelected) AppType.textXSMedium else AppType.textXS,
                    color = tint,
                )
            }
        }
    }
}
