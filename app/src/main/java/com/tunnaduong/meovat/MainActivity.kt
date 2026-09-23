package com.tunnaduong.meovat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tunnaduong.meovat.ui.RootScreen
import com.tunnaduong.meovat.ui.theme.MeoVatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MeoVatTheme {
                RootScreen()
            }
        }
    }
}
