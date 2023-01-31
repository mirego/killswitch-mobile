package com.mirego.killswitch

import co.touchlab.kermit.Logger
import com.mirego.killswitch.model.Action
import com.mirego.killswitch.model.Api
import com.mirego.killswitch.model.ButtonType
import com.mirego.killswitch.model.Response
import com.mirego.killswitch.viewmodel.KillswitchButtonAction
import com.mirego.killswitch.viewmodel.KillswitchButtonType
import com.mirego.killswitch.viewmodel.KillswitchButtonViewData
import com.mirego.killswitch.viewmodel.KillswitchViewData

internal object Killswitch {
    suspend fun engage(key: String, version: String, language: String, url: String): KillswitchViewData? =
        Api.request(key, version, language, url)?.let { response ->
            response.error?.takeIf { it.isNotEmpty() }?.let { error ->
                Logger.e("Received Killswitch error: $error")
            }

            when (response.action) {
                Action.OK, null -> null
                Action.ALERT, Action.KILL -> createDialog(response)
            }
        }

    private fun createDialog(response: Response): KillswitchViewData =
        KillswitchViewData(
            response.message.orEmpty(),
            response.action == Action.ALERT,
            response.buttons
                ?.map {
                    KillswitchButtonViewData(
                        it.label,
                        when {
                            !it.url.isNullOrEmpty() -> KillswitchButtonAction.NavigateToUrl(it.url)
                            else -> KillswitchButtonAction.Close
                        },
                        when (it.type) {
                            ButtonType.CANCEL -> KillswitchButtonType.NEGATIVE
                            ButtonType.URL -> KillswitchButtonType.POSITIVE
                        }
                    )
                }
                .orEmpty()
        )
}
