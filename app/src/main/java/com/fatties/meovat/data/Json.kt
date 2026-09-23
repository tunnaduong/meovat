package com.fatties.meovat.data

import org.json.JSONArray
import org.json.JSONObject

/** JSON (de)serialisation shared by the bundled seed, the on-device cache and the API. */
object Json {
    fun <T> JSONArray.map(transform: (JSONObject) -> T): List<T> = (0 until length()).map { transform(getJSONObject(it)) }
    fun JSONArray.strings(): List<String> = (0 until length()).map { getString(it) }
    fun JSONArray.ints(): List<Int> = (0 until length()).map { getInt(it) }
    private fun JSONObject.optNullableString(key: String): String? = if (isNull(key)) null else optString(key).ifEmpty { null }

    fun category(o: JSONObject) = TipCategory(o.getString("id"), o.getString("emoji"), o.getString("name"), o.optString("description"))

    fun tip(o: JSONObject) = Tip(
        id = o.getString("id"),
        categoryId = o.getString("categoryId"),
        tag = o.getString("tag"),
        title = o.getString("title"),
        subtitle = o.optString("subtitle"),
        minutes = o.optInt("minutes"),
        image = o.getString("image"),
        hero = o.optString("hero").ifEmpty { o.getString("image") },
        prep = o.optJSONArray("prep")?.strings().orEmpty(),
        steps = o.optJSONArray("steps")?.map { s ->
            TipStep(s.getString("title"), s.optString("body"), s.optNullableString("image"), s.optNullableString("imageUrl"))
        }.orEmpty(),
        imageUrl = o.optNullableString("imageUrl"),
        heroUrl = o.optNullableString("heroUrl"),
    )

    fun list(o: JSONObject, createdAtFallback: Long = System.currentTimeMillis()) = SavedList(
        id = o.getString("id"),
        emoji = o.optNullableString("emoji"),
        name = o.getString("name"),
        description = o.optString("description"),
        tipIds = o.optJSONArray("tipIds")?.strings().orEmpty(),
        createdAt = if (o.has("createdAt") && !o.isNull("createdAt")) o.getLong("createdAt") else createdAtFallback,
    )

    fun settings(o: JSONObject) = AppSettings(
        notificationsEnabled = o.optBoolean("notificationsEnabled", true),
        language = AppLanguage.from(o.optString("language")),
        sortOrder = SortOrder.from(o.optString("sortOrder")),
    )

    fun checklist(o: JSONObject?): Map<String, Set<Int>> =
        o?.keys()?.asSequence()?.associateWith { key -> o.getJSONArray(key).ints().toSet() }.orEmpty()

    fun categoryJson(c: TipCategory) = JSONObject().put("id", c.id).put("emoji", c.emoji).put("name", c.name).put("description", c.description)

    fun tipJson(t: Tip): JSONObject = JSONObject()
        .put("id", t.id).put("categoryId", t.categoryId).put("tag", t.tag).put("title", t.title).put("subtitle", t.subtitle)
        .put("minutes", t.minutes).put("image", t.image).put("hero", t.hero).put("prep", JSONArray(t.prep))
        .put("steps", JSONArray(t.steps.map { s ->
            JSONObject().put("title", s.title).put("body", s.body).put("image", s.image ?: JSONObject.NULL).put("imageUrl", s.imageUrl ?: JSONObject.NULL)
        }))
        .put("imageUrl", t.imageUrl ?: JSONObject.NULL).put("heroUrl", t.heroUrl ?: JSONObject.NULL)

    fun listJson(l: SavedList): JSONObject = JSONObject()
        .put("id", l.id).put("emoji", l.emoji ?: JSONObject.NULL).put("name", l.name).put("description", l.description)
        .put("tipIds", JSONArray(l.tipIds)).put("createdAt", l.createdAt)

    fun settingsJson(s: AppSettings): JSONObject = JSONObject()
        .put("notificationsEnabled", s.notificationsEnabled)
        .put("language", s.language.name.lowercase())
        .put("sortOrder", s.sortOrder.name.lowercase())
}
