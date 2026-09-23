package com.tunnaduong.meovat.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/** Parses the bundled `assets/seed.json` (shared with the iOS app). */
object SeedLoader {
    fun load(context: Context): SeedData {
        val root = JSONObject(context.assets.open("seed.json").bufferedReader().use { it.readText() })
        return SeedData(
            categories = root.getJSONArray("categories").map { c ->
                TipCategory(c.getString("id"), c.getString("emoji"), c.getString("name"), c.getString("description"))
            },
            lists = root.getJSONArray("lists").mapIndexed { index, l ->
                SavedList(
                    id = l.getString("id"),
                    emoji = l.optString("emoji").ifEmpty { null },
                    name = l.getString("name"),
                    description = l.optString("description"),
                    tipIds = l.getJSONArray("tipIds").strings(),
                    // Stagger creation times so "newest first" keeps the seed order.
                    createdAt = System.currentTimeMillis() + index * 60_000L,
                )
            },
            tips = root.getJSONArray("tips").map { t ->
                Tip(
                    id = t.getString("id"),
                    categoryId = t.getString("categoryId"),
                    tag = t.getString("tag"),
                    title = t.getString("title"),
                    subtitle = t.getString("subtitle"),
                    minutes = t.getInt("minutes"),
                    image = t.getString("image"),
                    hero = t.getString("hero"),
                    prep = t.getJSONArray("prep").strings(),
                    steps = t.getJSONArray("steps").map { s ->
                        TipStep(s.getString("title"), s.getString("body"), s.optString("image").ifEmpty { null })
                    },
                )
            },
        )
    }

    private fun <T> JSONArray.map(transform: (JSONObject) -> T): List<T> = (0 until length()).map { transform(getJSONObject(it)) }
    private fun <T> JSONArray.mapIndexed(transform: (Int, JSONObject) -> T): List<T> = (0 until length()).map { transform(it, getJSONObject(it)) }
    private fun JSONArray.strings(): List<String> = (0 until length()).map { getString(it) }
}
