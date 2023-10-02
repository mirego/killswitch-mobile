package com.mirego.killswitch

class KillswitchException(override val message: String, cause: Throwable? = null) : Exception(message)
