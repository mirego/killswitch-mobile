package com.mirego.killswitch

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import androidx.annotation.StyleRes
import java.lang.ref.WeakReference

class Killswitch(activity: Activity, @StyleRes private val themeResId: Int? = null) {
    private val activityReference = WeakReference(activity)

    init {
        activity.application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacksAdapter() {
            override fun onActivityDestroyed(destroyedActivity: Activity) {
                if (destroyedActivity == activity) {
                    activityReference.clear()
                }
            }
        })
    }

    suspend fun engage(key: String, version: String, language: String) {
        val response = Api.request(key, version, language)
        val activity = activityReference.get()

        if (activity == null || activity.isFinishing || response.action == null || response.action == Action.OK) return

        val builder = if (themeResId != null) AlertDialog.Builder(activity, themeResId) else AlertDialog.Builder(activity)
        val dialog = builder
            .setCancelable(false)
            .setMessage(response.message)
            .setButtons(response, activity)
            .create()

        when (response.action) {
            Action.ALERT -> dialog.setOnCancelListener { dialog.dismiss() }
            Action.KILL -> dialog.setOnCancelListener { kill() }
            else -> Unit
        }

        dialog.show()
    }
}

private fun AlertDialog.Builder.setButtons(response: Response, activity: Activity): AlertDialog.Builder =
    apply {
        fun executeCloseAction(dialog: DialogInterface) {
            if (response.action == Action.KILL) {
                kill()
            } else {
                dialog.dismiss()
            }
        }

        response.buttons?.forEach { button ->
            when (button.type) {
                ButtonType.CANCEL -> setNegativeButton(button.label) { dialog, _ ->
                    executeCloseAction(dialog)
                }
                ButtonType.URL -> setPositiveButton(button.label) { dialog, _ ->
                    activity.openAppInPlaystore(button.url)
                    executeCloseAction(dialog)
                }
            }
        }
    }
