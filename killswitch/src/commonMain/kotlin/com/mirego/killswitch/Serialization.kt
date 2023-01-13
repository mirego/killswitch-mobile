package com.mirego.killswitch

import kotlinx.serialization.json.Json

internal val json = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
    explicitNulls = false
}
