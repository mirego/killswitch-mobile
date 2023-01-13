package com.mirego.killswitch

import kotlinx.serialization.Serializable

@Serializable
data class KillswitchResponse(
    val action: String?,
    val message: String?,
    val buttons: List<Button>?,
    val error: String?
) {
    @Serializable
    data class Button(
        val type: String,
        val label: String,
        val url: String?
    )
}
