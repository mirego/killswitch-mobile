package com.mirego.killswitch.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class Action {
    @SerialName("ok")
    OK,

    @SerialName("alert")
    ALERT,

    @SerialName("kill")
    KILL
}
