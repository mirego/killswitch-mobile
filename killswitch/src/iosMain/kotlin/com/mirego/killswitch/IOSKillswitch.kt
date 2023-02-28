package com.mirego.killswitch

import com.mirego.killswitch.viewmodel.KillswitchButtonAction
import com.mirego.killswitch.viewmodel.KillswitchButtonType
import com.mirego.killswitch.viewmodel.KillswitchButtonViewData
import com.mirego.killswitch.viewmodel.KillswitchViewData
import platform.Foundation.NSBundle
import platform.Foundation.NSURL.Companion.URLWithString
import platform.StoreKit.SKStoreProductParameterITunesItemIdentifier
import platform.StoreKit.SKStoreProductViewController
import platform.StoreKit.SKStoreProductViewControllerDelegateProtocol
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.UIWindowScene
import platform.UIKit.UIWindowSceneDelegateProtocol

class IOSKillswitch @OverrideInit constructor(
    nibName: String? = null,
    bundle: NSBundle? = null
) : UIViewController(nibName, bundle), SKStoreProductViewControllerDelegateProtocol {

    private val storePrefix = "store:"

    private var viewData: KillswitchViewData? = null
    var delegate: IOSKillswitchDelegate? = null

    private fun shouldHideAlertAfterButtonAction() =
        viewData?.isCancelable == true

    private fun hideAlertWithCompletion(completion: (() -> Unit)?) {
        val topMostViewController = topMostViewController
        if (topMostViewController is IOSKillswitch || topMostViewController is SKStoreProductViewController) {
            topMostViewController.presentingViewController?.dismissViewControllerAnimated(true) {
                val newTopMostViewController = topMostViewController
                if (newTopMostViewController is IOSKillswitch) {
                    hideAlertWithCompletion(completion)
                } else {
                    if (shouldHideAlertAfterButtonAction()) {
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

    suspend fun engage(key: String, version: String, language: String, url: String) =
        Killswitch.engage(key, version, language, url)

    override fun productViewControllerDidFinish(viewController: SKStoreProductViewController) {
        viewController.presentingViewController?.dismissViewControllerAnimated(true) {
            determineAlertDisplayState()
        } ?: run {
            determineAlertDisplayState()
        }
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
        (button.action as? KillswitchButtonAction.NavigateToUrl)?.let {
            if (it.url.startsWith(storePrefix)) {
                val storeViewController = SKStoreProductViewController()
                storeViewController.delegate = this
                topMostViewController?.presentViewController(storeViewController, animated = true) {
                    val storeNumber = it.url.substring(storePrefix.length).toIntOrNull() ?: return@presentViewController
                    storeViewController.loadProductWithParameters(mapOf(SKStoreProductParameterITunesItemIdentifier to storeNumber)) { result, _ ->
                        if (!result) {
                            determineAlertDisplayState()
                        }
                    }
                }
            } else {
                URLWithString(it.url)?.let {
                    UIApplication.sharedApplication.openURL(it)
                }
            }
        }
    }

    private fun determineAlertDisplayState() {
        if (shouldHideAlertAfterButtonAction()) {
            hideAlertWithCompletion {}
        } else {
            showDialog(viewData)
        }
    }

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
}
