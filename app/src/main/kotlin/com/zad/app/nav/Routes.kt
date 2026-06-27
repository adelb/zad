package com.zad.app.nav

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val TODAY = "today"
    const val CAPTURE = "capture"
    const val HISTORY = "history"
    const val RESULT = "result"
    const val PROFILE = "profile"
    const val WEIGHT = "weight"
    const val WATER = "water"
    const val WORKOUT = "workout"
    const val ROUTINE_NEW = "routine/new"
    const val ROUTINE_DETAIL = "routine/{id}"
    const val ROUTINE_EDIT = "routine/{id}/edit"
    fun routineEdit(id: Long): String = "routine/$id/edit"
    const val SESSION = "session/{sessionId}/routine/{routineId}"
    const val SCALE = "scale/{path}"

    fun scale(path: String): String = "scale/${java.net.URLEncoder.encode(path, "UTF-8")}"
    fun routine(id: Long): String = "routine/$id"
    fun session(sessionId: Long, routineId: Long): String = "session/$sessionId/routine/$routineId"
}
