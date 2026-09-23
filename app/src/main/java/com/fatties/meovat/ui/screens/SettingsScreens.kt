package com.fatties.meovat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fatties.meovat.R
import com.fatties.meovat.data.AppLanguage
import com.fatties.meovat.data.AppState
import com.fatties.meovat.ui.AppViewModel
import com.fatties.meovat.ui.components.AppIcon
import com.fatties.meovat.ui.components.PageHeader
import com.fatties.meovat.ui.components.RadioIndicator
import com.fatties.meovat.ui.components.SubPageHeader
import com.fatties.meovat.ui.theme.AppColor
import com.fatties.meovat.ui.theme.AppType

@Composable
fun SettingsScreen(vm: AppViewModel, state: AppState, onLanguage: () -> Unit, onAbout: () -> Unit) {
    val l = state.l10n
    Column(Modifier.fillMaxSize().background(AppColor.Canvas)) {
        PageHeader(l.settingsTitle, l.settingsSubtitle)
        Column(Modifier.verticalScroll(rememberScrollState()).padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 120.dp)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColor.CanvasSecondary, RoundedCornerShape(12.dp))
                    .padding(16.dp),
            ) {
                Text(l.basicSettings, style = AppType.textLGSemibold, color = AppColor.Primary)
                SettingsRow(R.drawable.ic_bell, l.notifications) {
                    Switch(
                        checked = state.settings.notificationsEnabled,
                        onCheckedChange = vm::setNotifications,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AppColor.Primary,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = AppColor.Border,
                            uncheckedBorderColor = Color.Transparent,
                        ),
                    )
                }
                HorizontalDivider(color = AppColor.Divider, thickness = 1.dp)
                SettingsRow(R.drawable.ic_languages, l.languageTitle, onClick = onLanguage) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(state.settings.language.flag, fontSize = 20.sp, lineHeight = 24.sp)
                        Text(l.languageName(state.settings.language), style = AppType.textSM, color = AppColor.Caption)
                        AppIcon(R.drawable.ic_chevron_right, AppColor.Primary, 20.dp)
                    }
                }
                HorizontalDivider(color = AppColor.Divider, thickness = 1.dp)
                SettingsRow(R.drawable.ic_info, l.about, onClick = onAbout) {
                    AppIcon(R.drawable.ic_chevron_right, AppColor.Primary, 20.dp)
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(icon: Int, title: String, onClick: (() -> Unit)? = null, trailing: @Composable () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .then(
                if (onClick != null) {
                    Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick)
                } else {
                    Modifier
                },
            ),
    ) {
        AppIcon(icon, AppColor.Body, 24.dp)
        Text(title, style = AppType.textMD, color = AppColor.Heading, modifier = Modifier.weight(1f))
        trailing()
    }
}

@Composable
fun LanguageScreen(vm: AppViewModel, state: AppState, onBack: () -> Unit) {
    val l = state.l10n
    Column(Modifier.fillMaxSize().background(AppColor.Canvas)) {
        SubPageHeader(title = l.languageTitle, onBack = onBack)
        Column(Modifier.verticalScroll(rememberScrollState()).padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 120.dp)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColor.CanvasSecondary, RoundedCornerShape(12.dp))
                    .padding(16.dp),
            ) {
                Text(l.chooseLanguage, style = AppType.textLGMedium, color = AppColor.Heading, modifier = Modifier.padding(bottom = 4.dp))
                AppLanguage.entries.forEachIndexed { index, language ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { vm.setLanguage(language) },
                    ) {
                        Text(language.flag, fontSize = 22.sp, lineHeight = 26.sp)
                        Text(l.languageName(language), style = AppType.textMD, color = AppColor.Heading, modifier = Modifier.weight(1f))
                        RadioIndicator(selected = state.settings.language == language)
                    }
                    if (index < AppLanguage.entries.lastIndex) HorizontalDivider(color = AppColor.Divider, thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
fun AboutScreen(state: AppState, onBack: () -> Unit) {
    val l = state.l10n
    val context = LocalContext.current
    val version = remember {
        runCatching {
            val info = context.packageManager.getPackageInfo(context.packageName, 0)
            "${info.versionName} (${info.longVersionCode})"
        }.getOrDefault("1.0")
    }
    Column(Modifier.fillMaxSize().background(AppColor.Canvas)) {
        SubPageHeader(title = l.about, onBack = onBack)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 32.dp, end = 32.dp, top = 40.dp, bottom = 120.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(88.dp).background(AppColor.Primary, RoundedCornerShape(20.dp)),
            ) { AppIcon(R.drawable.ic_bookmark_plus, Color.White, 40.dp) }
            Text("Mẹo Vặt", style = AppType.headingMD, color = AppColor.Heading)
            Text("${l.version} $version", style = AppType.textSM, color = AppColor.Caption)
            Text(
                l.aboutBody,
                style = AppType.textMD.copy(lineHeight = AppType.textMD.lineHeight * 1.15f),
                color = AppColor.Body,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
