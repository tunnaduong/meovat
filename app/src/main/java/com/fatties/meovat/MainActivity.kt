package com.fatties.meovat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fatties.meovat.ui.RootScreen
import com.fatties.meovat.ui.theme.MeoVatTheme

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
