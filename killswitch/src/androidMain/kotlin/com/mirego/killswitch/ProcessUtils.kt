package com.mirego.killswitch

import android.os.Process

fun kill() {
    Process.killProcess(Process.myPid())
}
