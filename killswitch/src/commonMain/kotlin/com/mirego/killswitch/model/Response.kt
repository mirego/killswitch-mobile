package com.mirego.killswitch.model

import kotlinx.serialization.Serializable

@Serializable
internal data class Response(
    val action: Action?,
    val message: String?,
    val buttons: List<Button>?,
    val error: String?
) {
    @Serializable
    data class Button(
        val type: ButtonType,
        val label: String,
        val url: String?
    )
}
