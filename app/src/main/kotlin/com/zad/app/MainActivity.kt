package com.zad.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.zad.app.nav.ZadNavRoot
import com.zad.app.ui.theme.ZadTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZadTheme { ZadNavRoot() }
        }
    }
}
