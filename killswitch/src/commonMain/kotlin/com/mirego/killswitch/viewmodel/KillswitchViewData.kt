package com.mirego.killswitch.viewmodel

sealed interface KillswitchViewData {
    object None : KillswitchViewData

    data class Dialog(
        val message: String,
        val isCancelable: Boolean,
        val buttons: List<KillswitchButtonViewData>
    ) : KillswitchViewData
}
