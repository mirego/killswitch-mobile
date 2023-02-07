package com.mirego.killswitch

import android.os.Process

object ProcessUtils {
    fun kill() {
        Process.killProcess(Process.myPid())
    }
}
