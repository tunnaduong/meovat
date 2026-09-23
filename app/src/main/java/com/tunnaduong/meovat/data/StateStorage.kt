package com.tunnaduong.meovat.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/** Persists [AppState] as a small JSON file in the app's private storage. */
class StateStorage(context: Context) {
    private val file = File(context.filesDir, "state.json")

    fun load(): AppState? {
        if (!file.exists()) return null
        return runCatching {
            val root = JSONObject(file.readText())
            val lists = root.getJSONArray("lists").let { arr ->
                (0 until arr.length()).map { i ->
                    val l = arr.getJSONObject(i)
                    SavedList(
                        id = l.getString("id"),
                        emoji = l.optString("emoji").ifEmpty { null },
                        name = l.getString("name"),
                        description = l.optString("description"),
                        tipIds = l.getJSONArray("tipIds").let { ids -> (0 until ids.length()).map { ids.getString(it) } },
                        createdAt = l.optLong("createdAt"),
                    )
                }
            }
            val s = root.getJSONObject("settings")
            val settings = AppSettings(
                notificationsEnabled = s.optBoolean("notificationsEnabled", true),
                language = AppLanguage.from(s.optString("language")),
                sortOrder = SortOrder.from(s.optString("sortOrder")),
            )
            val prep = root.optJSONObject("checkedPrep") ?: JSONObject()
            val checked = prep.keys().asSequence().associateWith { key ->
                prep.getJSONArray(key).let { arr -> (0 until arr.length()).map { arr.getInt(it) }.toSet() }
            }
            AppState(lists, settings, checked)
        }.getOrNull()
    }

    fun save(state: AppState) {
        val root = JSONObject()
        root.put("lists", JSONArray().apply {
            state.lists.forEach { l ->
                put(JSONObject().apply {
                    put("id", l.id)
                    put("emoji", l.emoji ?: "")
                    put("name", l.name)
                    put("description", l.description)
                    put("tipIds", JSONArray(l.tipIds))
                    put("createdAt", l.createdAt)
                })
            }
        })
        root.put("settings", JSONObject().apply {
            put("notificationsEnabled", state.settings.notificationsEnabled)
            put("language", state.settings.language.name.lowercase())
            put("sortOrder", state.settings.sortOrder.name.lowercase())
        })
        root.put("checkedPrep", JSONObject().apply {
            state.checkedPrep.forEach { (tipId, set) -> put(tipId, JSONArray(set.sorted())) }
        })
        val tmp = File(file.parentFile, "state.json.tmp")
        tmp.writeText(root.toString())
        tmp.renameTo(file)
    }
}
