package com.plum.smokealarm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.plum.smokealarm.ui.theme.SmokeAlarmTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import android.media.AudioAttributes
import android.media.SoundPool

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmokeAlarmTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Ballsack",
                        modifier = Modifier.padding(innerPadding)
                    )

                    val context = LocalContext.current
                    var playAlarm by remember { mutableStateOf(false) }

                    // Manage SoundPool state inside DisposableEffect
                    DisposableEffect(playAlarm) {
                        // 1. Configure audio attributes for an alarm sound
                        val audioAttributes = AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()

                        // 2. Build the SoundPool instance
                        val soundPool = SoundPool.Builder()
                            .setMaxStreams(1)
                            .setAudioAttributes(audioAttributes)
                            .build()

                        // 3. Load the sound file into memory
                        val soundId = soundPool.load(context, R.raw.smokealarm, 1)
                        var streamId = 0

                        if (playAlarm) {
                            // Wait for the audio to finish loading into memory before playing
                            soundPool.setOnLoadCompleteListener { pool, _, status ->
                                if (status == 0) { // 0 means success
                                    // loop = -1 tells SoundPool to loop indefinitely with zero gap
                                    streamId = pool.play(soundId, 1f, 1f, 1, -1, 1f)
                                }
                            }
                        }

                        // 4. Stop instantly when playAlarm becomes false or user exits app
                        onDispose {
                            if (streamId != 0) {
                                soundPool.stop(streamId)
                            }
                            soundPool.release()
                        }
                    }

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(onClick = { playAlarm = !playAlarm }) {
                            Text(if (!playAlarm) "Start alarm" else "Stop alarm")
                        }
                    }

                    if (playAlarm) {
                        AlarmImage("")
                    }
                }
            }


        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Greetings $name!",
        modifier = modifier
    )
}

@Composable
fun AlarmImage(none: String, modifier: Modifier = Modifier) {
    val image = painterResource(R.drawable._2ds)
    Image(
        painter = image,
        contentDescription = "2DS"
    )
}

@Composable
fun CenteredToggle() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        PlayAlarmSoundToggle()
    }
}

@Composable
fun PlayAlarmSoundToggle() {
    // Maintain the checked state
    var isChecked by remember { mutableStateOf(false) }

    // Render toggle
    Switch(
        checked = isChecked,
        onCheckedChange = { newState ->
            isChecked = newState

            if (isChecked) {
                // Done when turned ON
            } else {
                // Done when turned OFF
            }
        }
    )
}