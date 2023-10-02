package com.mirego.killswitch

@Suppress("UNUSED_PARAMETER")
class KillswitchException(override val message: String, cause: Throwable? = null) : Exception(message)
