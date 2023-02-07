package com.mirego.killswitch

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

@Suppress("DEPRECATION")
val Context.versionName: String
    get() = try {
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            packageManager.getPackageInfo(packageName, 0)
        }
        packageInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        "unknown"
    }
