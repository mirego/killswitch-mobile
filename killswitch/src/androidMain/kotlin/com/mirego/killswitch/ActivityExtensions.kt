package com.mirego.killswitch

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log

fun Activity.navigateToKillswitchUrl(url: String?) {
    val uri = Uri.parse(url)

    try {
        val playStoreIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(playStoreIntent)
    } catch (_: ActivityNotFoundException) {
        try {
            val id = uri.getQueryParameter("id")
            if (id != null && "market" == uri.scheme) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=$id"))
                startActivity(browserIntent)
            }
        } catch (_: ActivityNotFoundException) {
        } catch (e: UnsupportedOperationException) {
            Log.e(this::class.simpleName, "Unable to parse Killswitch URL", e)
        }
    }
}
