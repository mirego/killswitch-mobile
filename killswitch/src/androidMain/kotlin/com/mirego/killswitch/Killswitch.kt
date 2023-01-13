package com.mirego.killswitch

object Killswitch {
    suspend fun engage(key: String, version: String) = KillswitchApi.request(key, version)
}
