package com.mirego.killswitch

import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.UIWindowScene
import platform.UIKit.UIWindowSceneDelegateProtocol

class IOSKillswitch {
    private val topMostViewController: UIViewController?
        get() {
            var topMost = rootViewController
            val presentedViewController = topMost?.presentedViewController

            while (presentedViewController != null) {
                topMost = presentedViewController
            }

            return topMost
        }

    private val rootViewController: UIViewController?
        get() {
            val scene = UIApplication.sharedApplication.connectedScenes.firstOrNull() as? UIWindowScene
            val sceneDelegate = scene?.delegate as? UIWindowSceneDelegateProtocol
            val sceneRootViewController = sceneDelegate?.window?.rootViewController
            return sceneRootViewController ?: UIApplication.sharedApplication.keyWindow?.rootViewController
        }

    fun test() {
        val alertController = UIAlertController.alertControllerWithTitle("Title", "Message", UIAlertControllerStyleAlert)
        alertController.addAction(UIAlertAction.actionWithTitle("Action", UIAlertActionStyleCancel) {

        })
        topMostViewController?.presentViewController(alertController, true) {
            // Delegate did show
        }
    }

    suspend fun engage() =
        Killswitch.engage(
            "ce087ed5f2196edc982452c22dc5ef6b89496bec9aad8da5a94582b41c54a8ba",
            "0.0.1",
            "en",
            "https://mirego-killswitch-qa.herokuapp.com/killswitch"
        )
}
