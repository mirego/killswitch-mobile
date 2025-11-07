package com.mirego.killswitch

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

internal actual fun httpClient(config: HttpClientConfig<*>.() -> Unit) =
    HttpClient(OkHttp) {
        config(this)

        engine {
            config {
                retryOnConnectionFailure(true)
            }
        }

        install(ContentNegotiation) {
            json(json)
        }
    }
