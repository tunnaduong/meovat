package com.tunnaduong.meovat.data

import android.content.Context
import com.tunnaduong.meovat.data.Json.map
import org.json.JSONObject

/** Parses the bundled `assets/seed.json` (shared with the iOS app and the backend). */
object SeedLoader {
    fun load(context: Context): SeedData {
        val root = JSONObject(context.assets.open("seed.json").bufferedReader().use { it.readText() })
        val now = System.currentTimeMillis()
        val lists = root.getJSONArray("lists")
        return SeedData(
            categories = root.getJSONArray("categories").map(Json::category),
            // Stagger creation times so "newest first" keeps the seed order.
            lists = (0 until lists.length()).map { i -> Json.list(lists.getJSONObject(i), now - (lists.length() - i) * 60_000L) },
            tips = root.getJSONArray("tips").map(Json::tip),
        )
    }
}
