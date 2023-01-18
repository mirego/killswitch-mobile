package com.mirego.killswitch

import kotlinx.serialization.Serializable

@Serializable
data class Response(
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
