package com.mirego.killswitch.model

import com.mirego.killswitch.httpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.UserAgent
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter

internal object Api {
    var userAgent: String? = null

    private val client by lazy {
        httpClient {
            userAgent?.let {
                install(UserAgent) {
                    agent = it
                }
            }
        }
    }

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
