package com.mirego.killswitch.viewmodel

sealed interface KillswitchButtonAction {
    object Close : KillswitchButtonAction

    data class NavigateToUrl(val url: String) : KillswitchButtonAction
}
