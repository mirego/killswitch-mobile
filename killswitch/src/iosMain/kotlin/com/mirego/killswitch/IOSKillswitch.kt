package com.mirego.killswitch

class IOSKillswitch {
    suspend fun engage(key: String, version: String, language: String, url: String) =
        Killswitch.engage(key, version, language, url)
}
