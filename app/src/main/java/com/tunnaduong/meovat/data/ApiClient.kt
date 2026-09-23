package com.tunnaduong.meovat.data

import com.tunnaduong.meovat.data.Json.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class ApiException(val status: Int, message: String) : IOException(message)

/** Thin client for the PHP backend. Every user-state call carries the device id. */
class ApiClient(private val baseUrl: String, private val deviceId: String) {
    private val http = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()
    private val jsonType = "application/json; charset=utf-8".toMediaType()

    // Content
    suspend fun categories(): List<TipCategory> = array("GET", "/api/categories").map(Json::category)
    suspend fun tips(): List<Tip> = array("GET", "/api/tips").map(Json::tip)

    // User state
    suspend fun me(): MeResponse = obj("GET", "/api/me").let { o ->
        MeResponse(o.getJSONArray("lists").map(Json::list), Json.settings(o.getJSONObject("settings")), Json.checklist(o.optJSONObject("checklist")))
    }

    suspend fun createList(list: SavedList): SavedList = Json.list(obj("POST", "/api/lists", listPayload(list, includeId = true)))
    suspend fun updateList(list: SavedList): SavedList = Json.list(obj("PATCH", "/api/lists/${list.id}", listPayload(list, includeId = false)))
    suspend fun deleteList(id: String) {
        try { call("DELETE", "/api/lists/$id") } catch (e: ApiException) { if (e.status != 404) throw e }
    }
    suspend fun setMembership(tipId: String, listIds: Set<String>): List<SavedList> =
        array("PUT", "/api/tips/$tipId/lists", JSONObject().put("listIds", JSONArray(listIds.sorted()))).map(Json::list)
    suspend fun removeTip(listId: String, tipId: String): SavedList = Json.list(obj("DELETE", "/api/lists/$listId/tips/$tipId"))
    suspend fun updateSettings(settings: AppSettings): AppSettings = Json.settings(obj("PUT", "/api/settings", Json.settingsJson(settings)))
    suspend fun setChecklist(tipId: String, checked: Set<Int>) {
        obj("PUT", "/api/tips/$tipId/checklist", JSONObject().put("checked", JSONArray(checked.sorted())))
    }

    private fun listPayload(list: SavedList, includeId: Boolean) = JSONObject().apply {
        if (includeId) put("id", list.id)
        put("name", list.name)
        put("description", list.description)
        put("emoji", list.emoji ?: "")
    }

    private suspend fun obj(method: String, path: String, body: JSONObject? = null) = JSONObject(call(method, path, body))
    private suspend fun array(method: String, path: String, body: JSONObject? = null) = JSONArray(call(method, path, body))

    private suspend fun call(method: String, path: String, body: JSONObject? = null): String = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(baseUrl + path)
            .header("Accept", "application/json")
            .header("X-Device-Id", deviceId)
            .method(method, body?.toString()?.toRequestBody(jsonType) ?: if (method == "GET") null else "".toRequestBody(null))
            .build()
        http.newCall(request).execute().use { response ->
            val text = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                val message = runCatching { JSONObject(text).getString("error") }.getOrDefault("HTTP ${response.code}")
                throw ApiException(response.code, message)
            }
            text
        }
    }
}
