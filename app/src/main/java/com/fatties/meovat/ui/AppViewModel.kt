package com.fatties.meovat.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fatties.meovat.data.ApiClient
import com.fatties.meovat.data.AppConfig
import com.fatties.meovat.data.AppLanguage
import com.fatties.meovat.data.AppSettings
import com.fatties.meovat.data.AppState
import com.fatties.meovat.data.DeviceIdentity
import com.fatties.meovat.data.SavedList
import com.fatties.meovat.data.SeedLoader
import com.fatties.meovat.data.SortOrder
import com.fatties.meovat.data.StateStorage
import com.fatties.meovat.data.Tip
import com.fatties.meovat.data.TipCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Single source of truth for the UI.
 *
 * Offline-first: bundled seed content and the last persisted state render immediately, then
 * [refresh] replaces them with the server's copy. Mutations apply locally right away and are
 * pushed to the API in the background; if the server can't be reached the change stays on the
 * device until the next successful refresh (server state wins).
 */
class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val storage = StateStorage(application)
    private val api = ApiClient(AppConfig.API_BASE_URL, DeviceIdentity.id(application))

    private val _state: MutableStateFlow<AppState>
    val state: StateFlow<AppState>

    init {
        val seed = SeedLoader.load(application)
        val content = storage.loadContent()
        val user = storage.loadUserState()
        _state = MutableStateFlow(
            AppState(
                categories = content?.categories ?: seed.categories,
                tips = content?.tips ?: seed.tips,
                lists = user?.lists ?: seed.lists,
                settings = user?.settings ?: AppSettings(),
                checkedPrep = user?.checkedPrep.orEmpty(),
            ),
        )
        state = _state
        refresh()
    }

    // Sync

    /** Pulls content and this device's state from the API. */
    fun refresh() {
        if (_state.value.isSyncing) return
        _state.update { it.copy(isSyncing = true) }
        viewModelScope.launch {
            try {
                val (categories, tips, me) = coroutineScope {
                    val c = async { api.categories() }
                    val t = async { api.tips() }
                    val m = async { api.me() }
                    Triple(c.await(), t.await(), m.await())
                }
                _state.update {
                    it.copy(categories = categories, tips = tips, lists = me.lists, settings = me.settings, checkedPrep = me.checklist, syncError = null)
                }
                withContext(Dispatchers.IO) {
                    storage.saveContent(StateStorage.Content(categories, tips))
                    persist()
                }
            } catch (e: Exception) {
                _state.update { it.copy(syncError = e.message ?: e.javaClass.simpleName) }
            } finally {
                _state.update { it.copy(isSyncing = false) }
            }
        }
    }

    /** Applies a local change, persists it and pushes it to the API in the background. */
    private fun mutate(transform: (AppState) -> AppState, push: (suspend () -> Unit)? = null) {
        _state.update(transform)
        viewModelScope.launch {
            withContext(Dispatchers.IO) { persist() }
            if (push == null) return@launch
            try {
                push()
                _state.update { it.copy(syncError = null) }
            } catch (e: Exception) {
                _state.update { it.copy(syncError = e.message ?: e.javaClass.simpleName) }
            }
        }
    }

    private fun persist() {
        val s = _state.value
        storage.saveUserState(StateStorage.UserState(s.lists, s.settings, s.checkedPrep))
    }

    // Content queries

    val categories: List<TipCategory> get() = _state.value.categories
    val tips: List<Tip> get() = _state.value.tips

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

    fun setMembership(tipId: String, listIds: Set<String>) = mutate({ s ->
        s.copy(lists = s.lists.map { list ->
            val contains = tipId in list.tipIds
            when {
                list.id in listIds && !contains -> list.copy(tipIds = list.tipIds + tipId)
                list.id !in listIds && contains -> list.copy(tipIds = list.tipIds - tipId)
                else -> list
            }
        })
    }) {
        val remote = api.setMembership(tipId, listIds)
        _state.update { it.copy(lists = remote) }
    }

    fun removeFromList(tipId: String, listId: String) = mutate({ s ->
        s.copy(lists = s.lists.map { if (it.id == listId) it.copy(tipIds = it.tipIds - tipId) else it })
    }) { api.removeTip(listId, tipId) }

    fun createList(name: String, description: String, emoji: String?): SavedList {
        val list = SavedList(emoji = emoji, name = name, description = description)
        mutate({ it.copy(lists = it.lists + list) }) { api.createList(list) }
        return list
    }

    fun updateList(list: SavedList) = mutate({ s ->
        s.copy(lists = s.lists.map { if (it.id == list.id) list else it })
    }) { api.updateList(list) }

    fun deleteList(id: String) = mutate({ s -> s.copy(lists = s.lists.filterNot { it.id == id }) }) { api.deleteList(id) }

    // Settings

    fun setNotifications(enabled: Boolean) = updateSettings { it.copy(notificationsEnabled = enabled) }
    fun setLanguage(language: AppLanguage) = updateSettings { it.copy(language = language) }
    fun setSortOrder(order: SortOrder) = updateSettings { it.copy(sortOrder = order) }

    private fun updateSettings(transform: (AppSettings) -> AppSettings) {
        val next = transform(_state.value.settings)
        mutate({ it.copy(settings = next) }) { api.updateSettings(next) }
    }

    // Preparation checklist

    fun togglePrep(tipId: String, index: Int) {
        val current = _state.value.checkedPrep[tipId].orEmpty()
        val next = if (index in current) current - index else current + index
        mutate({ it.copy(checkedPrep = it.checkedPrep + (tipId to next)) }) { api.setChecklist(tipId, next) }
    }
}
