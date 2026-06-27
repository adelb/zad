package com.zad.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.zad.app.nav.ZadNavRoot
import com.zad.app.ui.theme.ZadTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Edge-to-edge: window fills the display, no system-bar color sectors.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(0x00000000, 0x00000000),
            navigationBarStyle = SystemBarStyle.auto(0x00000000, 0x00000000)
        )
        super.onCreate(savedInstanceState)
        setContent {
            ZadTheme { ZadNavRoot() }
        }
    }
}
