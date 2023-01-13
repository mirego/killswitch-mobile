package com.mirego.killswitch

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter

internal object KillswitchApi {
    private val client = httpClient()

    suspend fun request(key: String, version: String): KillswitchResponse =
        client
            .get("https://mirego-killswitch-qa.herokuapp.com/killswitch") {
                header("Accept-Language", "en")
                parameter("key", key)
                parameter("version", version)
            }
            .body()
}
