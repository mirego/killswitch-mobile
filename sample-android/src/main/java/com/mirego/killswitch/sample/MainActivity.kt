package com.mirego.killswitch.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mirego.killswitch.Killswitch
import com.mirego.killswitch.sample.ui.theme.KillswitchSampleTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KillswitchSampleTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Content()
                }
            }
        }
    }
}

@Composable
private fun Content() {
    val scope = rememberCoroutineScope()

    var key by remember { mutableStateOf("") }
    var version by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("") }

    Column(
        Modifier
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
        Button(onClick = {
            scope.launch {
                response = try {
                    Killswitch.engage(key, version).toString()
                } catch (e: Exception) {
                    e.cause?.message.orEmpty()
                }
            }
        }) {
            Text("Engage")
        }

        Text("Response:\n\n$response")
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    KillswitchSampleTheme {
        Content()
    }
}