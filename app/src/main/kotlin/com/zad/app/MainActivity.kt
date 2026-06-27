package com.zad.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.zad.app.nav.ZadNavRoot
import com.zad.app.ui.theme.ZadTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Android 12 SplashScreen API — paints our night ground as the very
        // first frame (no white system flash, no launcher icon). The animated
        // icon slot is wired to a transparent drawable, so the system reserves
        // space but draws nothing. Sahrah-style.
        installSplashScreen()

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
