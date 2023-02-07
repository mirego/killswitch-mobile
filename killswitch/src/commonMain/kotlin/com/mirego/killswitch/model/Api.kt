package com.mirego.killswitch.model

import co.touchlab.kermit.Logger
import com.mirego.killswitch.httpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter

internal object Api {
    private val client = httpClient()

    suspend fun request(key: String, version: String, language: String, url: String): Response? =
        try {
            client
                .get(url) {
                    header("Accept-Language", language)
                    parameter("key", key)
                    parameter("version", version)
                }
                .body()
        } catch (e: Exception) {
            Logger.e("Failed to execute Killswitch request", e)
            null
        }
}
