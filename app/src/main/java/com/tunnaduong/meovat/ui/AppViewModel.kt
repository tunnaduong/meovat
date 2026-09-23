package com.tunnaduong.meovat.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tunnaduong.meovat.data.AppState
import com.tunnaduong.meovat.data.SavedList
import com.tunnaduong.meovat.data.SeedLoader
import com.tunnaduong.meovat.data.SortOrder
import com.tunnaduong.meovat.data.StateStorage
import com.tunnaduong.meovat.data.Tip
import com.tunnaduong.meovat.data.TipCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Single source of truth: bundled seed content + user state persisted through [StateStorage]. */
class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val storage = StateStorage(application)
    private val seed = SeedLoader.load(application)

    val categories: List<TipCategory> = seed.categories
    val tips: List<Tip> = seed.tips

    private val _state = MutableStateFlow(storage.load() ?: AppState(lists = seed.lists))
    val state: StateFlow<AppState> = _state

    private fun mutate(transform: (AppState) -> AppState) {
        val next = _state.updateAndGet(transform)
        viewModelScope.launch(Dispatchers.IO) { storage.save(next) }
    }

    private fun MutableStateFlow<AppState>.updateAndGet(transform: (AppState) -> AppState): AppState {
        update(transform)
        return value
    }

    // Content queries

    fun category(id: String) = categories.firstOrNull { it.id == id }
    fun tip(id: String) = tips.firstOrNull { it.id == id }
    fun list(id: String) = _state.value.lists.firstOrNull { it.id == id }

    fun tipsIn(category: TipCategory) = tips.filter { it.categoryId == category.id }

    fun tagsIn(category: TipCategory) = tipsIn(category).map { it.tag }.distinct()

    /** Tips of a list, most recently saved first. */
    fun tipsIn(list: SavedList) = list.tipIds.asReversed().mapNotNull(::tip)

    fun applySort(tips: List<Tip>, order: SortOrder) = when (order) {
        SortOrder.NEWEST -> tips
        SortOrder.ALPHABETICAL -> tips.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title })
    }

    // Saved lists

    fun listIdsContaining(tipId: String): Set<String> =
        _state.value.lists.filter { tipId in it.tipIds }.map { it.id }.toSet()

    fun setMembership(tipId: String, listIds: Set<String>) = mutate { s ->
        s.copy(lists = s.lists.map { list ->
            val contains = tipId in list.tipIds
            when {
                list.id in listIds && !contains -> list.copy(tipIds = list.tipIds + tipId)
                list.id !in listIds && contains -> list.copy(tipIds = list.tipIds - tipId)
                else -> list
            }
        })
    }

    fun removeFromList(tipId: String, listId: String) = mutate { s ->
        s.copy(lists = s.lists.map { if (it.id == listId) it.copy(tipIds = it.tipIds - tipId) else it })
    }

    fun createList(name: String, description: String, emoji: String?): SavedList {
        val list = SavedList(emoji = emoji, name = name, description = description)
        mutate { it.copy(lists = it.lists + list) }
        return list
    }

    fun updateList(list: SavedList) = mutate { s ->
        s.copy(lists = s.lists.map { if (it.id == list.id) list else it })
    }

    fun deleteList(id: String) = mutate { s -> s.copy(lists = s.lists.filterNot { it.id == id }) }

    // Settings

    fun setNotifications(enabled: Boolean) = mutate { it.copy(settings = it.settings.copy(notificationsEnabled = enabled)) }
    fun setLanguage(language: com.tunnaduong.meovat.data.AppLanguage) = mutate { it.copy(settings = it.settings.copy(language = language)) }
    fun setSortOrder(order: SortOrder) = mutate { it.copy(settings = it.settings.copy(sortOrder = order)) }

    // Preparation checklist

    fun togglePrep(tipId: String, index: Int) = mutate { s ->
        val current = s.checkedPrep[tipId].orEmpty()
        val next = if (index in current) current - index else current + index
        s.copy(checkedPrep = s.checkedPrep + (tipId to next))
    }
}
