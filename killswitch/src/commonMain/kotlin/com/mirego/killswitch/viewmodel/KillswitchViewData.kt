package com.mirego.killswitch.viewmodel

data class KillswitchViewData(
    val message: String,
    val isCancelable: Boolean,
    val buttons: List<KillswitchButtonViewData>
)
