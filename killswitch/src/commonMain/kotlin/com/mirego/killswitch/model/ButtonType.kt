package com.mirego.killswitch.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class ButtonType {
    @SerialName("cancel")
    CANCEL,

    @SerialName("url")
    URL,
}
