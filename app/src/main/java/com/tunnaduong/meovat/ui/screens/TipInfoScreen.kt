package com.tunnaduong.meovat.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tunnaduong.meovat.R
import com.tunnaduong.meovat.data.AppState
import com.tunnaduong.meovat.data.Photos
import com.tunnaduong.meovat.ui.AppViewModel
import com.tunnaduong.meovat.ui.components.AppIcon
import com.tunnaduong.meovat.ui.components.CheckRow
import com.tunnaduong.meovat.ui.components.IconCircleButton
import com.tunnaduong.meovat.ui.components.PrimaryButton
import com.tunnaduong.meovat.ui.components.TagBadge
import com.tunnaduong.meovat.ui.components.TipPhoto
import com.tunnaduong.meovat.ui.components.card
import com.tunnaduong.meovat.ui.theme.AppColor
import com.tunnaduong.meovat.ui.theme.AppType

private val HeroHeight = 324.dp
private val CardOverlap = 88.dp

/** Hero image, overlapping summary card, preparation checklist and the "view guide" CTA. */
@Composable
fun TipInfoScreen(tipId: String, vm: AppViewModel, state: AppState, onBack: () -> Unit, onGuide: (String) -> Unit) {
    val l = state.l10n
    val tip = vm.tip(tipId) ?: return

    Box(Modifier.fillMaxSize().background(AppColor.Canvas)) {
        Box(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            TipPhoto(url = tip.heroUrl, fallback = tip.hero, modifier = Modifier.fillMaxWidth().height(HeroHeight))
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier
                    .padding(top = HeroHeight - CardOverlap)
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 120.dp)
                    .navigationBarsPadding(),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth().card(16.dp).padding(16.dp),
                ) {
                    TagBadge(tip.tag)
                    Text(tip.title, style = AppType.headingMD, color = AppColor.Heading, textAlign = TextAlign.Center)
                    Text(tip.subtitle, style = AppType.textMD, color = AppColor.Caption, textAlign = TextAlign.Center)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(top = 8.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AppIcon(R.drawable.ic_clock, AppColor.Primary, 20.dp)
                            Text(l.minutes(tip.minutes), style = AppType.textSMMedium, color = AppColor.Heading)
                        }
                        Box(Modifier.size(width = 1.dp, height = 20.dp).background(AppColor.Border))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AppIcon(R.drawable.ic_list_checks, AppColor.Primary, 20.dp)
                            Text(l.stepsUpper(tip.steps.size), style = AppType.textSMMedium, color = AppColor.Heading)
                        }
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(l.prepTitle, style = AppType.headingSM, color = AppColor.Primary)
                    tip.prep.forEachIndexed { index, item ->
                        CheckRow(item, checked = index in state.checkedPrep[tip.id].orEmpty()) {
                            vm.togglePrep(tip.id, index)
                        }
                    }
                }
            }
        }

        Box(Modifier.statusBarsPadding().padding(20.dp)) {
            IconCircleButton(R.drawable.ic_chevron_left, onClick = onBack, iconSize = 20.dp, background = Color.White)
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(AppColor.Canvas)
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 8.dp)
                .navigationBarsPadding(),
        ) {
            PrimaryButton(l.viewGuide, onClick = { onGuide(tip.id) }, icon = R.drawable.ic_book_open)
        }
    }
}
