package com.tunnaduong.meovat.data

import android.content.Context
import java.util.UUID

object AppConfig {
    /** The Mẹo Vặt API on the Raspberry Pi (reachable over Tailscale). */
    const val API_BASE_URL = "http://100.102.160.98:8787"
}

/** Anonymous identity: a UUID generated once per install and sent as `X-Device-Id`. */
object DeviceIdentity {
    fun id(context: Context): String {
        val prefs = context.getSharedPreferences("meovat", Context.MODE_PRIVATE)
        prefs.getString("device_id", null)?.let { return it }
        val fresh = UUID.randomUUID().toString()
        prefs.edit().putString("device_id", fresh).apply()
        return fresh
    }
}
