package com.mirego.killswitch

interface KillswitchListener {
    fun onOk()

    fun onAlert()

    fun onKill()

    fun onDialogShown()
}
