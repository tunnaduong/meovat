package com.fatties.meovat.data

import android.content.Context
import com.fatties.meovat.data.Json.map
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/** Persists the user's state and the last content fetched from the API as JSON files in private storage. */
class StateStorage(context: Context) {
    private val stateFile = File(context.filesDir, "state.json")
    private val contentFile = File(context.filesDir, "content.json")

    data class UserState(val lists: List<SavedList>, val settings: AppSettings, val checkedPrep: Map<String, Set<Int>>)
    data class Content(val categories: List<TipCategory>, val tips: List<Tip>)

    fun loadUserState(): UserState? = runCatching {
        if (!stateFile.exists()) return null
        val root = JSONObject(stateFile.readText())
        UserState(
            lists = root.getJSONArray("lists").map(Json::list),
            settings = Json.settings(root.getJSONObject("settings")),
            checkedPrep = Json.checklist(root.optJSONObject("checkedPrep")),
        )
    }.getOrNull()

    fun saveUserState(state: UserState) {
        val root = JSONObject()
            .put("lists", JSONArray(state.lists.map(Json::listJson)))
            .put("settings", Json.settingsJson(state.settings))
            .put("checkedPrep", JSONObject().apply { state.checkedPrep.forEach { (tipId, set) -> put(tipId, JSONArray(set.sorted())) } })
        writeAtomically(stateFile, root.toString())
    }

    fun loadContent(): Content? = runCatching {
        if (!contentFile.exists()) return null
        val root = JSONObject(contentFile.readText())
        Content(root.getJSONArray("categories").map(Json::category), root.getJSONArray("tips").map(Json::tip))
            .takeIf { it.tips.isNotEmpty() }
    }.getOrNull()

    fun saveContent(content: Content) {
        val root = JSONObject()
            .put("categories", JSONArray(content.categories.map(Json::categoryJson)))
            .put("tips", JSONArray(content.tips.map(Json::tipJson)))
        writeAtomically(contentFile, root.toString())
    }

    private fun writeAtomically(file: File, text: String) {
        val tmp = File(file.parentFile, file.name + ".tmp")
        tmp.writeText(text)
        tmp.renameTo(file)
    }
}
