package com.fatties.meovat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fatties.meovat.R
import com.fatties.meovat.data.AppState
import com.fatties.meovat.data.SavedList
import com.fatties.meovat.data.Tip
import com.fatties.meovat.ui.AppViewModel
import com.fatties.meovat.ui.components.AppIcon
import com.fatties.meovat.ui.components.IconCircleButton
import com.fatties.meovat.ui.components.MenuAction
import com.fatties.meovat.ui.components.PageHeader
import com.fatties.meovat.ui.components.SavedListCard
import com.fatties.meovat.ui.components.SearchRow
import com.fatties.meovat.ui.components.SubPageHeader
import com.fatties.meovat.ui.components.TipCard
import com.fatties.meovat.ui.theme.AppColor
import com.fatties.meovat.ui.theme.AppType

@Composable
fun SavedScreen(vm: AppViewModel, state: AppState, onList: (String) -> Unit) {
    val l = state.l10n
    var showCreate by remember { mutableStateOf(false) }
    var listToEdit by remember { mutableStateOf<SavedList?>(null) }
    var listToDelete by remember { mutableStateOf<SavedList?>(null) }
    var menuFor by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().background(AppColor.Canvas)) {
        PageHeader(l.savedTitle, l.savedSubtitle) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(AppColor.Primary)
                    .clickable { showCreate = true },
            ) { AppIcon(R.drawable.ic_plus, Color.White, 20.dp) }
        }
        LazyColumn(
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(state.lists, key = { it.id }) { list ->
                Box {
                    SavedListCard(list, onClick = { onList(list.id) }, onLongClick = { menuFor = list.id })
                    DropdownMenu(expanded = menuFor == list.id, onDismissRequest = { menuFor = null }, containerColor = AppColor.Surface) {
                        DropdownMenuItem(
                            text = { Text(l.edit, style = AppType.textMD, color = AppColor.Heading) },
                            onClick = { menuFor = null; listToEdit = list },
                        )
                        DropdownMenuItem(
                            text = { Text(l.delete, style = AppType.textMD, color = Color(0xFFD92D20)) },
                            onClick = { menuFor = null; listToDelete = list },
                        )
                    }
                }
            }
        }
    }

    if (showCreate) ListFormSheet(vm, state, existing = null, onDismiss = { showCreate = false })
    listToEdit?.let { list -> ListFormSheet(vm, state, existing = list, onDismiss = { listToEdit = null }) }
    listToDelete?.let { list ->
        AlertDialog(
            onDismissRequest = { listToDelete = null },
            containerColor = AppColor.Surface,
            text = { Text(l.deleteConfirm, style = AppType.textMD, color = AppColor.Heading) },
            confirmButton = {
                TextButton(onClick = { vm.deleteList(list.id); listToDelete = null }) {
                    Text(l.delete, style = AppType.textMDSemibold, color = Color(0xFFD92D20))
                }
            },
            dismissButton = {
                TextButton(onClick = { listToDelete = null }) {
                    Text(l.cancel, style = AppType.textMDSemibold, color = AppColor.Caption)
                }
            },
        )
    }
}

/** Contents of one saved list; the filter button opens the sort sheet, the gear edits the list. */
@Composable
fun SavedListDetailScreen(listId: String, vm: AppViewModel, state: AppState, onBack: () -> Unit, onTip: (String) -> Unit) {
    val l = state.l10n
    val list = state.lists.firstOrNull { it.id == listId } ?: return
    var query by rememberSaveable { mutableStateOf("") }
    var showSort by remember { mutableStateOf(false) }
    var showEdit by remember { mutableStateOf(false) }
    var tipToSave by remember { mutableStateOf<Tip?>(null) }

    val tips = vm.applySort(vm.tipsIn(list), state.settings.sortOrder).filter { it.matches(query) }

    Column(Modifier.fillMaxSize().background(AppColor.Canvas)) {
        SubPageHeader(
            title = list.name,
            onBack = onBack,
            trailing = { IconCircleButton(R.drawable.ic_settings_2, onClick = { showEdit = true }, iconSize = 22.dp) },
        ) {
            SearchRow(query, { query = it }, l.searchPlaceholder) { showSort = true }
        }
        LazyColumn(
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(tips, key = { it.id }) { tip ->
                TipCard(
                    tip = tip,
                    onClick = { onTip(tip.id) },
                    actions = listOf(
                        MenuAction(l.saveTipTitle) { tipToSave = tip },
                        MenuAction(l.removeFromList, destructive = true) { vm.removeFromList(tip.id, list.id) },
                    ),
                )
            }
            if (tips.isEmpty()) {
                item {
                    Text(
                        if (query.isEmpty()) l.emptyList else l.noResults,
                        style = AppType.textMD,
                        color = AppColor.Caption,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                    )
                }
            }
        }
    }

    if (showSort) SortSheet(vm, state, onDismiss = { showSort = false })
    if (showEdit) ListFormSheet(vm, state, existing = list, onDismiss = { showEdit = false })
    tipToSave?.let { tip -> SaveToListSheet(tip, vm, state, onDismiss = { tipToSave = null }) }
}
