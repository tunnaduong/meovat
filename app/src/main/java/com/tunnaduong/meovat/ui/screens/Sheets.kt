package com.tunnaduong.meovat.ui.screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tunnaduong.meovat.R
import com.tunnaduong.meovat.data.AppState
import com.tunnaduong.meovat.data.SavedList
import com.tunnaduong.meovat.data.SortOrder
import com.tunnaduong.meovat.data.Tip
import com.tunnaduong.meovat.ui.AppViewModel
import com.tunnaduong.meovat.ui.components.AppBottomSheet
import com.tunnaduong.meovat.ui.components.AppIcon
import com.tunnaduong.meovat.ui.components.FormField
import com.tunnaduong.meovat.ui.components.OptionList
import com.tunnaduong.meovat.ui.components.OptionRow
import com.tunnaduong.meovat.ui.components.PrimaryButton
import com.tunnaduong.meovat.ui.theme.AppColor
import com.tunnaduong.meovat.ui.theme.AppType

/** "Lưu mẹo vặt" bottom sheet: pick which saved lists contain the tip. */
@Composable
fun SaveToListSheet(tip: Tip, vm: AppViewModel, state: AppState, onDismiss: () -> Unit) {
    val l = state.l10n
    var selection by remember { mutableStateOf(vm.listIdsContaining(tip.id)) }
    var showCreate by remember { mutableStateOf(false) }

    AppBottomSheet(R.drawable.ic_bookmark_plus, l.saveTipTitle, onDismiss) {
        Text(l.chooseList, style = AppType.textMD, color = AppColor.Body)
        OptionList(state.lists) { list ->
            OptionRow(list.emoji ?: "📁", list.name, selected = list.id in selection) {
                selection = if (list.id in selection) selection - list.id else selection + list.id
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { showCreate = true },
        ) {
            AppIcon(R.drawable.ic_plus, AppColor.Primary, 16.dp)
            Text(l.createList, style = AppType.textSMMedium, color = AppColor.Primary)
        }
        PrimaryButton(l.saveChanges, onClick = {
            vm.setMembership(tip.id, selection)
            onDismiss()
        }, modifier = Modifier.padding(top = 8.dp))
    }

    if (showCreate) {
        ListFormSheet(vm, state, existing = null, onDismiss = { showCreate = false }, onCreated = { selection = selection + it.id })
    }
}

/** "Tạo danh sách" / "Cài đặt danh sách" bottom sheet. */
@Composable
fun ListFormSheet(
    vm: AppViewModel,
    state: AppState,
    existing: SavedList?,
    onDismiss: () -> Unit,
    onCreated: (SavedList) -> Unit = {},
) {
    val l = state.l10n
    var name by remember { mutableStateOf(existing?.name.orEmpty()) }
    var description by remember { mutableStateOf(existing?.description.orEmpty()) }
    var emoji by remember { mutableStateOf(existing?.emoji) }
    var showPicker by remember { mutableStateOf(false) }
    val isEdit = existing != null

    AppBottomSheet(
        icon = if (isEdit) R.drawable.ic_settings_2 else R.drawable.ic_clipboard_list,
        title = if (isEdit) l.listSettings else l.createList,
        onDismiss = onDismiss,
    ) {
        FormField(l.listName, l.listNamePlaceholder, name) { name = it }
        FormField(l.listDescription, l.listDescriptionPlaceholder, description) { description = it }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(l.icon, style = AppType.textMDMedium, color = AppColor.Heading)
            val shape = RoundedCornerShape(12.dp)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(shape)
                    .background(AppColor.Surface)
                    .border(1.dp, AppColor.Border, shape)
                    .clickable { showPicker = true },
            ) {
                val current = emoji
                if (current != null) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .background(AppColor.Surface, RoundedCornerShape(8.dp))
                            .border(1.dp, AppColor.Primary, RoundedCornerShape(8.dp)),
                    ) { Text(current, fontSize = 22.sp, lineHeight = 26.sp) }
                } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(36.dp).border(1.dp, AppColor.Primary, CircleShape),
                    ) { AppIcon(R.drawable.ic_smile_plus, AppColor.Primary, 22.dp) }
                }
                Text(if (current == null) l.addIcon else l.changeIcon, style = AppType.textSM, color = AppColor.Disabled)
            }
        }
        PrimaryButton(
            title = if (isEdit) l.saveChanges else l.create,
            enabled = name.isNotBlank(),
            modifier = Modifier.padding(top = 8.dp),
            onClick = {
                if (existing == null) {
                    onCreated(vm.createList(name.trim(), description.trim(), emoji))
                } else {
                    vm.updateList(existing.copy(name = name.trim(), description = description.trim(), emoji = emoji))
                }
                onDismiss()
            },
        )
    }

    if (showPicker) {
        EmojiPickerSheet(state, onPick = { emoji = it }, onDismiss = { showPicker = false })
    }
}

private val EmojiChoices = listOf(
    "🗒️", "🏥", "🚗", "🏠", "🍳", "🧹", "🧺", "🌿", "❤️", "🎁", "🧳", "👕",
    "🍎", "🧴", "💡", "🛠️", "🐶", "🌸", "📚", "🎨", "☕️", "🧼", "🪴", "🚲",
)

@Composable
fun EmojiPickerSheet(state: AppState, onPick: (String) -> Unit, onDismiss: () -> Unit) {
    AppBottomSheet(R.drawable.ic_smile_plus, state.l10n.chooseIcon, onDismiss) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(240.dp),
        ) {
            items(EmojiChoices) { emoji ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(52.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppColor.CanvasSecondary)
                        .clickable {
                            onPick(emoji)
                            onDismiss()
                        },
                ) { Text(emoji, fontSize = 26.sp, lineHeight = 32.sp) }
            }
        }
    }
}

/** "Sắp xếp" bottom sheet. */
@Composable
fun SortSheet(vm: AppViewModel, state: AppState, onDismiss: () -> Unit) {
    val l = state.l10n
    var selection by remember { mutableStateOf(state.settings.sortOrder) }
    val options = listOf(
        Triple(SortOrder.NEWEST, "🕒", l.sortNewest),
        Triple(SortOrder.ALPHABETICAL, "🔤", l.sortAlphabetical),
    )
    AppBottomSheet(R.drawable.ic_sort, l.sortTitle, onDismiss) {
        Text(l.sortChoose, style = AppType.textMD, color = AppColor.Body)
        OptionList(options) { (order, emoji, title) ->
            OptionRow(emoji, title, selected = selection == order) { selection = order }
        }
        PrimaryButton(l.saveChanges, onClick = {
            vm.setSortOrder(selection)
            onDismiss()
        }, modifier = Modifier.padding(top = 8.dp))
    }
}
