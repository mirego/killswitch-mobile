package com.mirego.killswitch

import com.mirego.killswitch.model.Action
import com.mirego.killswitch.model.Api
import com.mirego.killswitch.model.ButtonType
import com.mirego.killswitch.model.Response
import com.mirego.killswitch.viewmodel.KillswitchButtonAction
import com.mirego.killswitch.viewmodel.KillswitchButtonType
import com.mirego.killswitch.viewmodel.KillswitchButtonViewData
import com.mirego.killswitch.viewmodel.KillswitchViewData
import kotlin.coroutines.cancellation.CancellationException

internal object Killswitch {
    @Throws(KillswitchException::class, CancellationException::class)
    suspend fun engage(
        key: String,
        version: String,
        url: String,
        language: String,
    ): KillswitchViewData? =
        try {
            Api.request(
                key = key,
                version = version,
                url = url,
                language = language,
            )?.let { response ->
                response.error?.takeIf { it.isNotEmpty() }?.let { error ->
                    throw KillswitchException(error)
                }

                when (response.action) {
                    Action.OK, null -> null
                    Action.ALERT, Action.KILL -> createDialog(response)
                }
            }
        } catch (e: Exception) {
            throw KillswitchException("Failed to execute Killswitch request", e)
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
                        },
                    )
                }
                .orEmpty(),
        )
}
