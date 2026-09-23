package com.fatties.meovat.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import com.fatties.meovat.data.Photos
import com.fatties.meovat.ui.theme.AppColor

/** Photo from the API with the bundled drawable of the same name as placeholder / offline fallback. */
@Composable
fun TipPhoto(url: String?, fallback: String?, modifier: Modifier = Modifier) {
    val fallbackId = fallback?.let(Photos::idOrNull)
    val fallbackPainter = fallbackId?.let { painterResource(it) }
    Box(modifier.background(AppColor.CanvasSecondary)) {
        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = fallbackPainter,
                error = fallbackPainter,
                modifier = Modifier.matchParentSize(),
            )
        } else if (fallbackPainter != null) {
            Image(painter = fallbackPainter, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize())
        }
    }
}
