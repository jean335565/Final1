package com.example.afinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf("main") }

        Scaffold(
            timeText = { TimeText() } // Versión simplificada
        ) {
            when (currentScreen) {
                "chrono" -> ChronoScreen { currentScreen = "main" }
                "health" -> HealthScreen { currentScreen = "main" }
                else -> MainScreen(
                    onChronoClick = { currentScreen = "chrono" },
                    onHealthClick = { currentScreen = "health" }
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    onChronoClick: () -> Unit,
    onHealthClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Rastreador De Salud",
            style = MaterialTheme.typography.title1.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = onChronoClick,
            modifier = Modifier.size(ButtonDefaults.LargeButtonSize)
        ) {
            val timerIcon: Painter = painterResource(R.drawable.ic_timer)
            Icon(
                painter = timerIcon,
                contentDescription = "Cronómetro"
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onHealthClick,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.secondary
            )
        ) {
            val healthIcon: Painter = painterResource(R.drawable.ic_health)
            Icon(
                painter = healthIcon,
                contentDescription = "Salud"
            )
        }
    }
}

@Composable
fun ChronoScreen(onBack: () -> Unit) {
    var isRunning by remember { mutableStateOf(false) }
    var timeMillis by remember { mutableStateOf(0L) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(10)
            timeMillis += 10
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = String.format(
                "%02d:%02d.%02d",
                TimeUnit.MILLISECONDS.toMinutes(timeMillis) % 60,
                TimeUnit.MILLISECONDS.toSeconds(timeMillis) % 60,
                (timeMillis % 1000) / 10
            ),
            style = MaterialTheme.typography.display2.copy(
                fontSize = 24.sp
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { isRunning = !isRunning },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (isRunning) Color.Red else Color.Green
            )
        ) {
            Text(if (isRunning) "Detener" else "Iniciar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onBack) {
            Text("Volver")
        }
    }
}

@Composable
fun HealthScreen(onBack: () -> Unit) {
    val heartRate by simulateHeartRate()
    val steps by simulateSteps()

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            HealthMetric(
                icon = painterResource(R.drawable.ic_heart),
                value = "$heartRate",
                label = "Ritmo cardíaco",
                unit = "BPM"
            )
        }

        item {
            HealthMetric(
                icon = painterResource(R.drawable.ic_steps),
                value = "$steps",
                label = "Pasos",
                unit = ""
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBack) {
                Text("Volver")
            }
        }
    }
}

@Composable
fun HealthMetric(
    icon: Painter,
    value: String,
    label: String,
    unit: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = {}
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.caption2
                )
                Text(
                    text = "$value $unit",
                    style = MaterialTheme.typography.title2
                )
            }
        }
    }
}

@Composable
fun simulateHeartRate(): State<Int> {
    val heartRate = remember { mutableStateOf(72) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            heartRate.value = (60..120).random()
        }
    }
    return heartRate
}

@Composable
fun simulateSteps(): State<Int> {
    val steps = remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1500)
            steps.value += (1..10).random()
        }
    }
    return steps
}

@Preview(device = "id:wearos_small_round", showSystemUi = true)
@Composable
fun PreviewApp() {
    WearApp()
}