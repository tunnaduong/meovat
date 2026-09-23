package com.tunnaduong.meovat.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tunnaduong.meovat.R
import com.tunnaduong.meovat.data.AppState
import com.tunnaduong.meovat.data.Photos
import com.tunnaduong.meovat.data.Tip
import com.tunnaduong.meovat.data.TipStep
import com.tunnaduong.meovat.ui.AppViewModel
import com.tunnaduong.meovat.ui.components.AppIcon
import com.tunnaduong.meovat.ui.components.IconCircleButton
import com.tunnaduong.meovat.ui.theme.AppColor
import com.tunnaduong.meovat.ui.theme.AppType

/** Step-by-step guide; steps expand as an accordion. */
@Composable
fun TipDetailScreen(tipId: String, vm: AppViewModel, state: AppState, onClose: () -> Unit) {
    val l = state.l10n
    val tip = vm.tip(tipId) ?: return
    var expanded by remember { mutableStateOf(setOf(0)) }
    var showSave by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(AppColor.CanvasSecondary)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColor.Canvas)
                .statusBarsPadding()
                .padding(20.dp),
        ) {
            IconCircleButton(R.drawable.ic_x, onClick = onClose, iconSize = 20.dp)
            Spacer(Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .height(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, AppColor.Primary, RoundedCornerShape(8.dp))
                    .clickable { showSave = true }
                    .padding(horizontal = 12.dp),
            ) {
                AppIcon(R.drawable.ic_square_plus, AppColor.Primary, 18.dp)
                Text(l.saveTip, style = AppType.textSMSemibold, color = AppColor.Primary)
            }
        }
        HorizontalDivider(color = AppColor.Divider, thickness = 1.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 40.dp)
                .navigationBarsPadding(),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(20.dp).padding(bottom = 4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppIcon(R.drawable.ic_clock, AppColor.Primary, 20.dp)
                    Text(l.minutes(tip.minutes), style = AppType.textSMMedium, color = AppColor.Caption)
                }
                Text(tip.title, style = AppType.headingMD, color = AppColor.Heading)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .background(AppColor.PrimarySubtle, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    AppIcon(R.drawable.ic_list_checks, AppColor.Primary, 20.dp)
                    Text(l.stepsLower(tip.steps.size), style = AppType.textMDMedium, color = AppColor.Primary)
                }
            }
            tip.steps.forEachIndexed { index, step ->
                StepSection(index, step, isOpen = index in expanded) {
                    expanded = if (index in expanded) expanded - index else expanded + index
                }
            }
        }
    }

    if (showSave) SaveToListSheet(tip, vm, state, onDismiss = { showSave = false })
}

@Composable
private fun StepSection(index: Int, step: TipStep, isOpen: Boolean, onToggle: () -> Unit) {
    val rotation by animateFloatAsState(if (isOpen) 180f else 0f, label = "chevron")
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColor.Canvas)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onToggle)
                .defaultMinSize(minHeight = 88.dp)
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(40.dp).background(AppColor.Primary, CircleShape)) {
                Text("${index + 1}", style = AppType.textLGMedium, color = Color.White)
            }
            Text(step.title, style = AppType.headingSM, color = AppColor.Heading, modifier = Modifier.weight(1f))
            AppIcon(R.drawable.ic_chevron_down, AppColor.Primary, 20.dp, Modifier.rotate(rotation))
        }
        AnimatedVisibility(visible = isOpen, enter = expandVertically(), exit = shrinkVertically()) {
            Column {
                step.image?.let { image ->
                    Image(
                        painter = painterResource(Photos.id(image)),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(260.dp),
                    )
                }
                Text(step.body, style = AppType.textMD.copy(lineHeight = AppType.textMD.lineHeight * 1.15f), color = AppColor.Body, modifier = Modifier.padding(20.dp))
            }
        }
    }
}
