package com.zad.app.health

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

object HealthIntents {
    private const val HC_PACKAGE = "com.google.android.apps.healthdata"
    private const val PLAY = "https://play.google.com/store/apps/details?id="

    /** Open the Health Connect app's settings/permissions UI. */
    fun openHealthConnectSettings(ctx: Context): Boolean {
        // Newer (Android 14): direct settings intent
        val a = Intent("android.health.connect.action.HEALTH_HOME_SETTINGS")
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (tryStart(ctx, a)) return true
        // Older: Jetpack one
        val b = Intent("androidx.health.ACTION_HEALTH_CONNECT_SETTINGS")
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (tryStart(ctx, b)) return true
        // Last resort: just launch the HC app if installed
        val c = ctx.packageManager.getLaunchIntentForPackage(HC_PACKAGE)
            ?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return c != null && tryStart(ctx, c)
    }

    fun installHealthConnect(ctx: Context) {
        val market = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$HC_PACKAGE"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (tryStart(ctx, market)) return
        val play = Intent(Intent.ACTION_VIEW, Uri.parse("$PLAY$HC_PACKAGE"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        tryStart(ctx, play)
    }

    /** Open Huawei Health page in the user's store (Play if present). */
    fun openHuaweiHealth(ctx: Context) {
        val huawei = "com.huawei.health"
        val launch = ctx.packageManager.getLaunchIntentForPackage(huawei)
            ?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (launch != null && tryStart(ctx, launch)) return
        val play = Intent(Intent.ACTION_VIEW, Uri.parse("$PLAY$huawei"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        tryStart(ctx, play)
    }

    private fun tryStart(ctx: Context, intent: Intent): Boolean = runCatching {
        ctx.startActivity(intent); true
    }.getOrDefault(false)
}
