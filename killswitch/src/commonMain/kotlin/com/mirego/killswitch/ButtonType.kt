package com.mirego.killswitch

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ButtonType {
    @SerialName("cancel")
    CANCEL,

    @SerialName("url")
    URL
}
