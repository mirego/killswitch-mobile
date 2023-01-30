package com.mirego.killswitch.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import com.mirego.compose.utils.extensions.clickableWithoutIndication
import com.mirego.killswitch.ProcessUtils
import com.mirego.killswitch.viewmodel.KillswitchButtonAction
import com.mirego.killswitch.viewmodel.KillswitchButtonType
import com.mirego.killswitch.viewmodel.KillswitchButtonViewData
import com.mirego.killswitch.viewmodel.KillswitchViewData

@Composable
fun CustomDialog(
    viewData: KillswitchViewData,
    dismiss: () -> Unit,
    navigateToUrl: (String) -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickableWithoutIndication {
                if (viewData.isCancelable) {
                    dismiss()
                }
            }
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(viewData.message, color = Color.White)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                viewData.buttons.forEach { button ->
                    KillswitchButton(
                        viewData = button,
                        isCancelable = viewData.isCancelable,
                        dismiss = dismiss,
                        navigateToUrl = navigateToUrl
                    )
                }
            }
        }
    }

    val focusManager = LocalFocusManager.current
    LaunchedEffect(true) {
        focusManager.clearFocus()
    }
}

@Composable
private fun KillswitchButton(
    viewData: KillswitchButtonViewData,
    isCancelable: Boolean,
    dismiss: () -> Unit,
    navigateToUrl: (String) -> Unit
) {
    val closeAction = if (isCancelable) {
        { dismiss() }
    } else {
        { ProcessUtils.kill() }
    }

    val buttonAction: () -> Unit = when (val action = viewData.action) {
        KillswitchButtonAction.Close -> {
            {
                closeAction()
            }
        }
        is KillswitchButtonAction.NavigateToUrl -> {
            {
                navigateToUrl(action.url)
                closeAction()
            }
        }
    }
    Button(
        onClick = buttonAction,
        colors = ButtonDefaults.buttonColors(
            containerColor = when (viewData.type) {
                KillswitchButtonType.POSITIVE -> MaterialTheme.colorScheme.primary
                KillswitchButtonType.NEGATIVE -> MaterialTheme.colorScheme.secondary
            }
        )
    ) {
        Text(viewData.title)
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    CustomDialog(
        viewData = KillswitchViewData(
            message = "You must update to the latest version",
            isCancelable = false,
            buttons = listOf(
                KillswitchButtonViewData("Update", KillswitchButtonAction.NavigateToUrl("myapp.com"), KillswitchButtonType.POSITIVE),
                KillswitchButtonViewData("Cancel", KillswitchButtonAction.Close, KillswitchButtonType.NEGATIVE)
            )
        ),
        dismiss = {},
        navigateToUrl = {}
    )
}
