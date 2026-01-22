package com.mirego.killswitch

import com.mirego.killswitch.viewmodel.KillswitchButtonAction
import com.mirego.killswitch.viewmodel.KillswitchButtonType
import com.mirego.killswitch.viewmodel.KillswitchButtonViewData
import com.mirego.killswitch.viewmodel.KillswitchViewData
import kotlin.coroutines.cancellation.CancellationException
import platform.Foundation.NSBundle
import platform.Foundation.NSLocale
import platform.Foundation.NSLocaleLanguageCode
import platform.Foundation.NSURL
import platform.Foundation.componentsFromLocaleIdentifier
import platform.Foundation.preferredLanguages
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
    companion object {
        fun initialize(configuration: Configuration) {
            Killswitch.initialize(configuration)
        }
    }

    @Throws(KillswitchException::class, CancellationException::class)
    suspend fun engage(
        key: String,
        version: String,
        url: String,
        language: String,
    ): KillswitchViewData? =
        Killswitch.engage(
            key = key,
            version = version,
            url = url,
            language = language,
        )

    @Throws(KillswitchException::class, CancellationException::class)
    suspend fun engage(
        key: String,
        url: String,
    ): KillswitchViewData? =
        Killswitch.engage(
            key = key,
            version = version,
            url = url,
            language = language,
        )

    val language: String
        get() {
            val localeIdentifier: String = NSLocale.preferredLanguages[0].toString()
            val components = NSLocale.componentsFromLocaleIdentifier(localeIdentifier)
            return components[NSLocaleLanguageCode].toString()
        }

    val version: String
        get() = NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString")?.toString().orEmpty()

    fun showDialog(viewData: KillswitchViewData?) = showDialog(viewData, null)

    fun showDialog(
        viewData: KillswitchViewData?,
        listener: KillswitchListener?,
    ) {
        IOSKillswitchViewController().showDialog(viewData, listener)

        if (viewData == null) {
            listener?.onOk()
        } else {
            listener?.onDialogShown()
        }
    }

    private class IOSKillswitchViewController : UIViewController(null, null), SKStoreProductViewControllerDelegateProtocol, UIAdaptivePresentationControllerDelegateProtocol {
        private val storePrefix = "store:"

        private var viewData: KillswitchViewData? = null
        private var listener: KillswitchListener? = null

        private fun shouldHideAlertAfterButtonAction() = viewData?.isCancelable == true

        private fun hideAlertWithCompletion(completion: (() -> Unit)? = null) {
            val topMostViewController = topMostViewController
            if (topMostViewController is IOSKillswitchViewController || topMostViewController is SKStoreProductViewController) {
                topMostViewController.presentingViewController?.dismissViewControllerAnimated(true) {
                    val newTopMostViewController = topMostViewController

                    if (newTopMostViewController is IOSKillswitchViewController) {
                        hideAlertWithCompletion(completion)
                    } else {
                        completion?.invoke()
                    }
                }
            } else {
                completion?.invoke()
            }
        }

        override fun presentViewController(
            viewControllerToPresent: UIViewController,
            animated: Boolean,
            completion: (() -> Unit)?,
        ) {
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
            determineAlertDisplayState()
        }

        fun showDialog(
            viewData: KillswitchViewData?,
            listener: KillswitchListener?,
        ) {
            this.viewData = viewData ?: return
            this.listener = listener

            hideAlertWithCompletion {
                val alertController = UIAlertController.alertControllerWithTitle("", viewData.message, UIAlertControllerStyleAlert)

                viewData.buttons.forEach { button ->
                    alertController.addAction(
                        UIAlertAction.actionWithTitle(
                            button.title,
                            when (button.type) {
                                KillswitchButtonType.POSITIVE -> UIAlertActionStyleDefault
                                KillswitchButtonType.NEGATIVE -> UIAlertActionStyleCancel
                            },
                        ) {
                            performActionForButton(button)
                        },
                    )
                }

                topMostViewController?.presentViewController(alertController, true, null)
            }
        }

        private fun performActionForButton(button: KillswitchButtonViewData) {
            if (shouldHideAlertAfterButtonAction()) {
                listener?.onAlert()
            } else {
                listener?.onKill()
            }

            when (val action = button.action) {
                KillswitchButtonAction.Close -> determineAlertDisplayState()
                is KillswitchButtonAction.NavigateToUrl -> if (action.url.startsWith(storePrefix)) {
                    val storeViewController = SKStoreProductViewController()

                    storeViewController.delegate = this
                    storeViewController.presentationController?.delegate = this

                    topMostViewController?.presentViewController(storeViewController, animated = true) {
                        val storeNumber = action.url.substring(storePrefix.length).toLongOrNull() ?: run {
                            determineAlertDisplayState()
                            return@presentViewController
                        }
                        storeViewController.loadProductWithParameters(mapOf(SKStoreProductParameterITunesItemIdentifier to storeNumber)) { result, _ ->
                            if (!result) {
                                determineAlertDisplayState()
                            }
                        }
                    }
                } else {
                    NSURL.URLWithString(action.url)?.let {
                        UIApplication.sharedApplication.openURL(it)
                    }
                    determineAlertDisplayState()
                }
            }
        }

        private fun determineAlertDisplayState() {
            if (shouldHideAlertAfterButtonAction()) {
                hideAlertWithCompletion()
            } else {
                showDialog(viewData, listener)
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
                return sceneDelegate?.window?.rootViewController ?: UIApplication.sharedApplication.keyWindow?.rootViewController
            }
    }
}
