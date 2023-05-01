package com.mirego.killswitch

import com.mirego.killswitch.viewmodel.KillswitchButtonAction
import com.mirego.killswitch.viewmodel.KillswitchButtonType
import com.mirego.killswitch.viewmodel.KillswitchButtonViewData
import com.mirego.killswitch.viewmodel.KillswitchViewData
import platform.Foundation.NSURL
import platform.StoreKit.SKStoreProductParameterITunesItemIdentifier
import platform.StoreKit.SKStoreProductViewController
import platform.StoreKit.SKStoreProductViewControllerDelegateProtocol
import platform.UIKit.UIAdaptivePresentationControllerDelegateProtocol
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert
import platform.UIKit.UIApplication
import platform.UIKit.UIPresentationController
import platform.UIKit.UIViewController
import platform.UIKit.UIWindowScene
import platform.UIKit.UIWindowSceneDelegateProtocol
import platform.UIKit.presentationController

class IOSKillswitch {
    suspend fun engage(key: String, version: String, language: String, url: String) =
        Killswitch.engage(key, version, language, url)

    fun showDialog(viewData: KillswitchViewData?) {
        IOSKillswitchViewController().showDialog(viewData)
    }

    private class IOSKillswitchViewController : UIViewController(null, null), SKStoreProductViewControllerDelegateProtocol, UIAdaptivePresentationControllerDelegateProtocol {

        private val storePrefix = "store:"

        private var viewData: KillswitchViewData? = null
        var delegate: IOSKillswitchDelegate? = null

        private fun shouldHideAlertAfterButtonAction() =
            viewData?.isCancelable == true

        private fun hideAlertWithCompletion(completion: (() -> Unit)?) {
            val topMostViewController = topMostViewController
            println("topMostViewController: $topMostViewController")
            if (topMostViewController is IOSKillswitchViewController || topMostViewController is SKStoreProductViewController) {
                println("dismissViewControllerAnimated")
                topMostViewController.presentingViewController?.dismissViewControllerAnimated(true) {
                    val newTopMostViewController = topMostViewController
                    if (newTopMostViewController is IOSKillswitchViewController) {
                        println("hideAlertWithCompletion")
                        hideAlertWithCompletion(completion)
                    } else {
                        if (shouldHideAlertAfterButtonAction()) {
                            println("alertDidHide")
                            delegate?.alertDidHide()
                        }

                        completion?.invoke()
                    }
                }
            } else {
                completion?.invoke()
            }
        }

        override fun presentViewController(viewControllerToPresent: UIViewController, animated: Boolean, completion: (() -> Unit)?) {
            throw IllegalStateException("Trying to present a view controller on top of the Killswitch: $viewControllerToPresent")
        }

        override fun productViewControllerDidFinish(viewController: SKStoreProductViewController) {
            viewController.presentingViewController?.dismissViewControllerAnimated(true) {
                determineAlertDisplayState()
            } ?: run {
                determineAlertDisplayState()
            }
        }

        override fun presentationControllerDidDismiss(presentationController: UIPresentationController) {
            println("presentationControllerDidDismiss")
            determineAlertDisplayState()
        }

        override fun presentationControllerDidAttemptToDismiss(presentationController: UIPresentationController) {
            println("presentationControllerDidAttemptToDismiss")
        }

        fun showDialog(viewData: KillswitchViewData?) {
            this.viewData = viewData ?: return

            hideAlertWithCompletion {
                val alertController = UIAlertController.alertControllerWithTitle("", viewData.message, UIAlertControllerStyleAlert)

                viewData.buttons.sortedBy { it.type.displayOrder }.forEach { button ->
                    alertController.addAction(
                        UIAlertAction.actionWithTitle(
                            button.title,
                            when (button.type) {
                                KillswitchButtonType.POSITIVE -> UIAlertActionStyleDefault
                                KillswitchButtonType.NEGATIVE -> UIAlertActionStyleCancel
                            }
                        ) {
                            performActionForButton(button)
                        }
                    )
                }

                topMostViewController?.presentViewController(alertController, true) {
                    delegate?.alertDidShow()
                }
            }
        }

        private fun performActionForButton(button: KillswitchButtonViewData) {
            when (val action = button.action) {
                KillswitchButtonAction.Close -> determineAlertDisplayState()
                is KillswitchButtonAction.NavigateToUrl -> if (action.url.startsWith(storePrefix)) {
                    val storeViewController = SKStoreProductViewController()
                    storeViewController.delegate = this
                    storeViewController.presentationController?.delegate = this
                    topMostViewController?.presentViewController(storeViewController, animated = true) {
                        val storeNumber = action.url.substring(storePrefix.length).toLongOrNull() ?: run {
                            println("Wrong store number")
                            determineAlertDisplayState()
                            return@presentViewController
                        }
                        println("Store number: $storeNumber")
                        storeViewController.loadProductWithParameters(mapOf(SKStoreProductParameterITunesItemIdentifier to storeNumber)) { result, error ->
                            if (!result) {
                                println("loadProductWithParameters failed: $error")
                                determineAlertDisplayState()
                            }
                        }
                    }
                } else {
                    NSURL.URLWithString(action.url)?.let {
                        UIApplication.sharedApplication.openURL(it)
                    }
                }
            }
        }

        private fun determineAlertDisplayState() {
            println("determineAlertDisplayState")
            if (shouldHideAlertAfterButtonAction()) {
                println("hideAlertWithCompletion")
                hideAlertWithCompletion {}
            } else {
                println("viewData: $viewData")
                showDialog(viewData)
            }
        }

        private val topMostViewController: UIViewController?
            get() {
                var topMost = rootViewController
                while (topMost?.presentedViewController != null) {
                    topMost = topMost.presentedViewController
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
    }
}
