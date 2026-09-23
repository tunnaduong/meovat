package com.tunnaduong.meovat.data

import java.util.UUID

data class TipCategory(val id: String, val emoji: String, val name: String, val description: String)

data class TipStep(val title: String, val body: String, val image: String?)

data class Tip(
    val id: String,
    val categoryId: String,
    val tag: String,
    val title: String,
    val subtitle: String,
    val minutes: Int,
    val image: String,
    val hero: String,
    val prep: List<String>,
    val steps: List<TipStep>,
)

data class SavedList(
    val id: String = UUID.randomUUID().toString(),
    val emoji: String?,
    val name: String,
    val description: String,
    val tipIds: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
)

enum class AppLanguage(val flag: String) {
    VI("🇻🇳"), EN("🇬🇧"), ZH("🇨🇳"), JA("🇯🇵");

    companion object {
        fun from(name: String?) = entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: VI
    }
}

enum class SortOrder {
    NEWEST, ALPHABETICAL;

    companion object {
        fun from(name: String?) = entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: NEWEST
    }
}

data class AppSettings(
    val notificationsEnabled: Boolean = true,
    val language: AppLanguage = AppLanguage.VI,
    val sortOrder: SortOrder = SortOrder.NEWEST,
)

data class SeedData(val categories: List<TipCategory>, val lists: List<SavedList>, val tips: List<Tip>)

/** Everything the user can change; persisted as JSON by [StateStorage]. */
data class AppState(
    val lists: List<SavedList> = emptyList(),
    val settings: AppSettings = AppSettings(),
    val checkedPrep: Map<String, Set<Int>> = emptyMap(),
) {
    val l10n: L10n get() = L10n(settings.language)
}
