package com.mirego.killswitch

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import androidx.annotation.StyleRes
import com.mirego.killswitch.viewmodel.KillswitchButtonAction
import com.mirego.killswitch.viewmodel.KillswitchButtonType
import com.mirego.killswitch.viewmodel.KillswitchViewData

object AndroidKillswitch {
    suspend fun engage(key: String, version: String, language: String) =
        Killswitch.engage(key, version, language)

    fun handleResponse(viewData: KillswitchViewData, activity: Activity, @StyleRes themeResId: Int? = null) {
        (viewData as? KillswitchViewData.Dialog)
            ?.createDialog(activity, themeResId)
            ?.show()
    }
}

private fun KillswitchViewData.Dialog.createDialog(activity: Activity, @StyleRes themeResId: Int?): Dialog {
    val builder = if (themeResId != null) AlertDialog.Builder(activity, themeResId) else AlertDialog.Builder(activity)
    val dialog = builder
        .setCancelable(false)
        .setMessage(message)
        .setButtons(this, activity)
        .create()

    dialog.setOnCancelListener {
        executeCloseAction(dialog, this)
    }

    return dialog
}

private fun AlertDialog.Builder.setButtons(dialogViewData: KillswitchViewData.Dialog, activity: Activity): AlertDialog.Builder =
    apply {
        fun createOnClickListener(action: KillswitchButtonAction) = DialogInterface.OnClickListener { dialog, _ ->
            if (action is KillswitchButtonAction.NavigateToUrl) {
                activity.navigateToKillswitchUrl(action.url)
            }
            executeCloseAction(dialog, dialogViewData)
        }

        dialogViewData.buttons.forEach { button ->
            val clickListener = createOnClickListener(button.action)

            when (button.type) {
                KillswitchButtonType.NEGATIVE -> setNegativeButton(button.title, clickListener)
                KillswitchButtonType.POSITIVE -> setPositiveButton(button.title, clickListener)
            }
        }
    }

private fun executeCloseAction(dialog: DialogInterface, dialogViewData: KillswitchViewData.Dialog) {
    if (dialogViewData.isCancelable) {
        dialog.dismiss()
    } else {
        ProcessUtils.kill()
    }
}
