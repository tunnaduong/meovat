package com.tunnaduong.meovat.data

import androidx.annotation.DrawableRes
import com.tunnaduong.meovat.R

/** Maps the photo names used in seed.json to bundled drawables. */
object Photos {
    private val byName = mapOf(
        "photo_toothbrush" to R.drawable.photo_toothbrush,
        "photo_burger" to R.drawable.photo_burger,
        "photo_drink" to R.drawable.photo_drink,
        "photo_bao" to R.drawable.photo_bao,
        "photo_skewers" to R.drawable.photo_skewers,
        "photo_pancakes" to R.drawable.photo_pancakes,
        "photo_livingroom" to R.drawable.photo_livingroom,
        "photo_step_brush" to R.drawable.photo_step_brush,
    )

    @DrawableRes
    fun id(name: String): Int = byName[name] ?: R.drawable.photo_pancakes
}
