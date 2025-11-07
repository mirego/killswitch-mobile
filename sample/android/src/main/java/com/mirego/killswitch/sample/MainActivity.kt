package com.mirego.killswitch.sample

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mirego.killswitch.AndroidKillswitch
import com.mirego.killswitch.KillswitchException
import com.mirego.killswitch.KillswitchListener
import com.mirego.killswitch.navigateToKillswitchUrl
import com.mirego.killswitch.sample.ui.theme.KillswitchSampleTheme
import com.mirego.killswitch.viewmodel.KillswitchViewData
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            KillswitchSampleTheme {
                Scaffold(
                    contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(WindowInsets.systemBars)
                ) { contentPadding ->
                    Box(
                        Modifier
                            .padding(contentPadding)
                            .fillMaxSize()
                    ) {
                        var viewData by remember { mutableStateOf<KillswitchViewData?>(null) }
                        val scope = rememberCoroutineScope()

                        Content(
                            modifier = Modifier
                                .systemBarsPadding()
                                .run { if (viewData != null) blur(5.dp) else this }
                        ) { url, key, version, language, customDialog ->
                            scope.launch {
                                engage(url, key, version, language, customDialog, this@MainActivity) {
                                    viewData = it
                                }
                            }
                        }

                        when (val localViewData = viewData) {
                            is KillswitchViewData -> CustomDialog(
                                viewData = localViewData,
                                dismiss = { viewData = null },
                                navigateToUrl = this@MainActivity::navigateToKillswitchUrl
                            )
                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    private suspend fun engage(
        url: String,
        key: String,
        version: String,
        language: String,
        customDialog: Boolean,
        activity: Activity,
        onViewDataReceived: (KillswitchViewData) -> Unit
    ) {
        if (customDialog) {
            try {
                AndroidKillswitch.engage(
                    key = key,
                    version = version,
                    url = url,
                    language = language
                )?.let {
                    onViewDataReceived(it)
                }
            } catch (e: KillswitchException) {
                Log.e(TAG, "Killswitch exception", e)
            }
        } else {
            try {
                AndroidKillswitch.showDialog(
                    viewData = AndroidKillswitch.engage(
                        key = key,
                        version = version,
                        url = url,
                        language = language
                    ),
                    activity = activity,
                    themeResId = R.style.CustomAlertDialog,
                    object : KillswitchListener {
                        override fun onOk() {
                            Log.d(TAG, "onOk")
                        }

                        override fun onAlert() {
                            Log.d(TAG, "onAlert")
                        }

                        override fun onKill() {
                            Log.d(TAG, "onKill")
                        }

                        override fun onDialogShown() {
                            Log.d(TAG, "onDialogShown")
                        }
                    }
                )
            } catch (e: KillswitchException) {
                Log.e(TAG, "Killswitch exception", e)
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

@Composable
private fun Content(modifier: Modifier = Modifier, engage: (String, String, String, String, Boolean) -> Unit) {
    var url by remember { mutableStateOf("") }
    var key by remember { mutableStateOf("") }
    var version by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("") }
    var customDialog by remember { mutableStateOf(false) }

    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(R.mipmap.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier
                .size(128.dp)
                .align(Alignment.CenterHorizontally)
        )
        TextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("Url") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = key,
            onValueChange = { key = it },
            label = { Text("Key") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = version,
            onValueChange = { version = it },
            label = { Text("Version") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = language,
            onValueChange = { language = it },
            label = { Text("Language") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Custom dialog")
            Checkbox(checked = customDialog, onCheckedChange = { customDialog = it })
        }
        Button(onClick = { engage(url, key, version, language, customDialog) }) {
            Text("Engage")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    KillswitchSampleTheme {
        Content { _, _, _, _, _ -> }
    }
}
