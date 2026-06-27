package com.zad.app.nav

object Routes {
    const val SPLASH = "splash"
    const val TODAY = "today"
    const val CAPTURE = "capture"
    const val HISTORY = "history"
    const val RESULT = "result"
    const val SCALE = "scale/{path}"
    fun scale(path: String): String = "scale/${java.net.URLEncoder.encode(path, "UTF-8")}"
}
