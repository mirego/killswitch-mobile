<div align="center">
  <img src="https://user-images.githubusercontent.com/11348/151395659-3ebe29b6-b1d6-44fa-bb44-c42146c7e99a.png" width="563" />
  <p><strong>Killswitch</strong> is a clever control panel built by <a href="https://www.mirego.com">Mirego</a> that allows mobile developers to apply<br /> runtime version-specific behaviors to their iOS or Android application.</p>
  <br />
  <a href="https://github.com/mirego/killswitch-mobile/actions/workflows/ci.yaml"><img src="https://github.com/mirego/killswitch-mobile/actions/workflows/ci.yaml/badge.svg"/></a>
  <a href="https://kotlinlang.org/"><img src="https://img.shields.io/badge/kotlin-1.9.10-blue.svg?logo=kotlin"/></a>
  <a href="https://opensource.org/licenses/BSD-3-Clause"><img src="https://img.shields.io/badge/License-BSD_3--Clause-blue.svg"/></a>
</div>

## Setup

The library is published to Mirego's public Maven repository, so make sure you have it included in your settings.gradle.kts `dependencyResolutionManagement` block.

```kotlin
dependencyResolutionManagement {
    repositories {
        // ...
        maven("https://s3.amazonaws.com/mirego-maven/public")
    }
}
```

You can then add the dependency to your common source set dependencies in your build.gradle.kts.

```kotlin
commonMain {
    dependencies {
        // ...
        api("com.mirego.killswitch-mobile:killswitch:x.y.z")
    }
}
```

To make the code available on iOS, don't forget to export the library in the framework.

```kotlin
kotlin {
    androidTarget()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries {
            framework {
                // ...
                export("com.mirego.killswitch-mobile:killswitch:x.y.z")
            }
        }
    }
}
```

On iOS, if the Killswitch is engaged from another thread than the main one, you may need to add this line in your project's gradle.properties to prevent your app from crashing.
```groovy
kotlin.native.binary.objcExportSuspendFunctionLaunchThreadRestriction=none
```
Reference: https://youtrack.jetbrains.com/issue/KT-51297/Native-allow-calling-Kotlin-suspend-functions-on-non-main-thread-from-Swift

## Usage

There is two ways of using the Killswitch: by letting the library display a native dialog or by implementing custom UI.

### Native dialog

On Android in the `onCreate()` function of your main Activity you can engage the Killswitch and let the library handle the response in order to display the native dialog.

```kotlin
lifecycleScope.launch {
    AndroidKillswitch.showDialog(
        AndroidKillswitch.engage(KILLSWITCH_API_KEY, this@MainActivity, KILLSWITCH_URL),
        this@MainActivity,
        android.R.style.Theme_DeviceDefault_Light_Dialog_Alert
    )
}
```

On iOS you can do the same thing in the `application()` function of your AppDelegate
```swift
Task {
    do {
        let viewData = try await IOSKillswitch().engage(key: KILLSWITCH_API_KEY, url: KILLSWITCH_URL)
        DispatchQueue.main.async {
            IOSKillswitch().showDialog(viewData: viewData)
        }
    } catch {
        print("Killswitch error: \(error)")
    }
}
```

### Custom UI

On Android somewhere in the root view of your application, you can do something like this.

```kotlin
var viewData by remember { mutableStateOf<KillswitchViewData?>(null) }

when (val localViewData = viewData) {
    is KillswitchViewData -> CustomDialog(
        viewData = localViewData,
        dismiss = { viewData = null },
        navigateToUrl = this@MainActivity::navigateToKillswitchUrl
    )
    else -> MainView()
}
```

You can find a sample CustomDialog view [here](sample/android/src/main/java/com/mirego/killswitch/sample/CustomDialog.kt)

## License

Killswitch is © 2013-2023 [Mirego](https://www.mirego.com) and may be freely distributed under the [New BSD license](http://opensource.org/licenses/BSD-3-Clause). See the [`LICENSE.md`](https://github.com/mirego/killswitch/blob/main/LICENSE.md) file.

The shield logo is based on [this lovely icon by Kimmi Studio](https://thenounproject.com/icon/shield-1055246/), from The Noun Project. Used under a [Creative Commons BY 3.0](http://creativecommons.org/licenses/by/3.0/) license.

## About Mirego

[Mirego](https://www.mirego.com) is a team of passionate people who believe that work is a place where you can innovate and have fun. We’re a team of [talented people](https://life.mirego.com) who imagine and build beautiful Web and mobile applications. We come together to share ideas and [change the world](http://www.mirego.org).

We also [love open-source software](https://open.mirego.com) and we try to give back to the community as much as we can.
