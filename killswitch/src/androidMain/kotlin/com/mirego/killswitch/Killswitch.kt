package com.mirego.killswitch

import android.app.Activity
import android.app.AlertDialog
import java.lang.ref.WeakReference

class Killswitch(activity: Activity) {
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

        val dialog = AlertDialog.Builder(activity)
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
        fun doAction(dialog: AlertDialog) {
            if (response.action == Action.KILL) {
                kill()
            } else {
                dialog.dismiss()
            }
        }

        response.buttons?.forEach { button ->
            when (button.type) {
                ButtonType.CANCEL -> setNegativeButton(button.label) { dialog, _ ->
                    if (response.action == Action.KILL) {
                        kill()
                    } else {
                        dialog.dismiss()
                    }
                }
                ButtonType.URL -> setPositiveButton(button.label) { dialog, _ ->
                    activity.openAppInPlaystore(button.url)
                    if (response.action == Action.KILL) {
                        kill()
                    } else {
                        dialog.dismiss()
                    }
                }
            }
        }
    }
