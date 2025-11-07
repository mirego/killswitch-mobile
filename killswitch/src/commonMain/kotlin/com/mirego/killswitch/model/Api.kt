package com.mirego.killswitch.model

import com.mirego.killswitch.httpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter

internal object Api {
    private val client = httpClient()

    suspend fun request(
        key: String,
        version: String,
        url: String,
        language: String,
    ): Response? =
        client
            .get(url) {
                header("Accept-Language", language)
                parameter("key", key)
                parameter("version", version)
            }
            .body()
}
