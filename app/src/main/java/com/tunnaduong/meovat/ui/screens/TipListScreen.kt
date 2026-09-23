package com.tunnaduong.meovat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tunnaduong.meovat.data.AppState
import com.tunnaduong.meovat.data.Tip
import com.tunnaduong.meovat.ui.AppViewModel
import com.tunnaduong.meovat.ui.components.FilterChip
import com.tunnaduong.meovat.ui.components.MenuAction
import com.tunnaduong.meovat.ui.components.SearchRow
import com.tunnaduong.meovat.ui.components.SubPageHeader
import com.tunnaduong.meovat.ui.components.TipCard
import com.tunnaduong.meovat.ui.theme.AppColor
import com.tunnaduong.meovat.ui.theme.AppType

/** Tips of one category with search, tag chips and a sort sheet behind the filter button. */
@Composable
fun TipListScreen(categoryId: String, vm: AppViewModel, state: AppState, onBack: () -> Unit, onTip: (String) -> Unit) {
    val l = state.l10n
    val category = vm.category(categoryId) ?: return
    var query by rememberSaveable { mutableStateOf("") }
    var selectedTag by rememberSaveable { mutableStateOf<String?>(null) }
    var tipToSave by remember { mutableStateOf<Tip?>(null) }
    var showSort by remember { mutableStateOf(false) }

    val tags = vm.tagsIn(category)
    val tips = vm.applySort(vm.tipsIn(category), state.settings.sortOrder).filter { tip ->
        (selectedTag == null || tip.tag == selectedTag) && tip.matches(query)
    }

    Column(Modifier.fillMaxSize().background(AppColor.Canvas)) {
        SubPageHeader(title = l.categoryTitle(category.name), onBack = onBack) {
            SearchRow(query, { query = it }, l.searchPlaceholder) { showSort = true }
        }
        LazyColumn(
            contentPadding = PaddingValues(top = 20.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp),
                ) {
                    items(tags) { tag ->
                        FilterChip(tag, selected = selectedTag == tag) {
                            selectedTag = if (selectedTag == tag) null else tag
                        }
                    }
                }
            }
            items(tips, key = { it.id }) { tip ->
                TipCard(
                    tip = tip,
                    onClick = { onTip(tip.id) },
                    actions = listOf(MenuAction(l.saveTipTitle) { tipToSave = tip }),
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }
            if (tips.isEmpty()) {
                item {
                    Text(
                        l.noResults,
                        style = AppType.textMD,
                        color = AppColor.Caption,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                    )
                }
            }
        }
    }

    tipToSave?.let { tip -> SaveToListSheet(tip, vm, state, onDismiss = { tipToSave = null }) }
    if (showSort) SortSheet(vm, state, onDismiss = { showSort = false })
}

internal fun Tip.matches(query: String): Boolean {
    val q = query.trim()
    return q.isEmpty() || title.contains(q, ignoreCase = true) || subtitle.contains(q, ignoreCase = true)
}
