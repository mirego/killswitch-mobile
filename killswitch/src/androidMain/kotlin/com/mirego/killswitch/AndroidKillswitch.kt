package com.mirego.killswitch

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import androidx.annotation.StyleRes
import com.mirego.killswitch.viewmodel.KillswitchButtonAction
import com.mirego.killswitch.viewmodel.KillswitchButtonType
import com.mirego.killswitch.viewmodel.KillswitchViewData
import java.util.Locale

object AndroidKillswitch {
    suspend fun engage(key: String, version: String, language: String, url: String) =
        Killswitch.engage(key, version, language, url)

    suspend fun engage(key: String, url: String, context: Context) =
        Killswitch.engage(key, context.versionName, Locale.getDefault().language, url)

    fun showDialog(viewData: KillswitchViewData?, activity: Activity, @StyleRes themeResId: Int? = null, listener: KillswitchListener? = null) {
        viewData
            ?.createDialog(activity, themeResId, listener)
            ?.show()
            ?: run { listener?.onOk() }
    }
}

private fun KillswitchViewData.createDialog(activity: Activity, @StyleRes themeResId: Int?, listener: KillswitchListener?): Dialog {
    val builder = if (themeResId != null) AlertDialog.Builder(activity, themeResId) else AlertDialog.Builder(activity)
    val dialog = builder
        .setCancelable(false)
        .setMessage(message)
        .setButtons(this, activity, listener)
        .create()

    dialog.setOnCancelListener {
        executeCloseAction(dialog, this, listener)
    }

    return dialog
}

private fun AlertDialog.Builder.setButtons(viewData: KillswitchViewData, activity: Activity, listener: KillswitchListener?): AlertDialog.Builder =
    apply {
        fun createOnClickListener(action: KillswitchButtonAction) = DialogInterface.OnClickListener { dialog, _ ->
            if (action is KillswitchButtonAction.NavigateToUrl) {
                activity.navigateToKillswitchUrl(action.url)
            }
            executeCloseAction(dialog, viewData, listener)
        }

        viewData.buttons.forEach { button ->
            val clickListener = createOnClickListener(button.action)

            when (button.type) {
                KillswitchButtonType.NEGATIVE -> setNegativeButton(button.title, clickListener)
                KillswitchButtonType.POSITIVE -> setPositiveButton(button.title, clickListener)
            }
        }
    }

private fun executeCloseAction(dialog: DialogInterface, viewData: KillswitchViewData, listener: KillswitchListener?) {
    if (viewData.isCancelable) {
        dialog.dismiss()
        listener?.onAlert()
    } else {
        listener?.onKill()
        ProcessUtils.kill()
    }
}
