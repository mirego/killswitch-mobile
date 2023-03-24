package com.mirego.killswitch

import com.mirego.killswitch.model.Action

interface KillswitchListener {
    fun onOk()

    fun onAlert()

    fun onKill()

    fun onError()

    fun onActionReceived(action: Action)

    fun onDialogShown()
}
