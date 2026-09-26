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
import android.media.MediaPlayer
import android.media.SoundPool
import android.media.audiofx.LoudnessEnhancer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmokeAlarmTheme {
                // Content
                Scaffold { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Set attributes for audio file
                        val audioAttributes = AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()

                        // Create sound pool
                        val soundPool = SoundPool.Builder()
                            .setMaxStreams(1)
                            .setAudioAttributes(audioAttributes)
                            .build()

                        val context = LocalContext.current
                        var playAlarm by remember { mutableStateOf(false) }

                        // Greetings text
                        Greeting( name = "Ballsack" )

                        // Manage SoundPool state inside DisposableEffect
                        DisposableEffect(playAlarm) {
                            // Load sound file into memory
                            val soundId = soundPool.load(context, R.raw.smokealarm, 1)
                            var streamId = 0

                            if (playAlarm) {
                                // Wait for audio to finish loading into memory before playing
                                soundPool.setOnLoadCompleteListener { pool, _, status ->
                                    if (status == 0) {
                                        // loop = -1 tells SoundPool to loop indefinitely with zero gap
                                        streamId = pool.play(soundId, 1f, 1f, 1, -1, 1f)
                                    }
                                }
                            }

                            // Stop instantly when playAlarm becomes false or user exits app
                            onDispose {
                                if (streamId != 0) {
                                    soundPool.stop(streamId)
                                }
                                soundPool.release()
                            }
                        }

                        // Alarm toggle button
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(onClick = { playAlarm = !playAlarm }) {
                                Text(if (!playAlarm) "Start alarm" else "Stop alarm")
                            }
                        }

                        // Chirp toggle button
                        var isChirping by remember { mutableStateOf(false) }
                        val chirp = MediaPlayer.create(context, R.raw.chirp_long)
                        chirp.isLooping = true
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Button(onClick = {
                                isChirping = !isChirping
                                if (isChirping) chirp.start() else chirp.stop()
                            }) {
                                Text(if (!isChirping) "Start chirp" else "Stop chirp")
                            }
                        }

                        // Show image when alarm is playing
                        if (playAlarm) Show2dsImage()
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
fun Show2dsImage(modifier: Modifier = Modifier) {
    val image = painterResource(R.drawable._2ds)
    Image(
        painter = image,
        contentDescription = "2DS"
    )
}