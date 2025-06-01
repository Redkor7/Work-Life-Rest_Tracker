package mkn.work_life_rest_tracker.ui.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import mkn.work_life_rest_tracker.data.TrackerData
import mkn.work_life_rest_tracker.ui.theme.LifeColor
import mkn.work_life_rest_tracker.ui.theme.RestColor
import mkn.work_life_rest_tracker.ui.theme.WorkColor
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import mkn.work_life_rest_tracker.timer_logic.TimerViewModel
import mkn.work_life_rest_tracker.data.TimerState

@Composable
fun TrackerScreen(navController: NavController) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Таймер", "Статистика")

    val context = LocalContext.current
    val repo = remember { TrackerData(context.applicationContext as Application) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) },
                )
            }
        }
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (selectedTab) {
                0 -> TimerTab()
                1 -> DiagramTab(repo)
            }
        }

        BackButton(onClick = { navController.popBackStack() })
    }
}

@Composable
fun TimerTab() {
    val viewModel: TimerViewModel = viewModel()
    val timerState by viewModel.timerState.observeAsState(TimerState())
    var showReportDialog by remember { mutableStateOf(false) }
    var reportDialogShown by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val repo = remember { TrackerData(context.applicationContext as Application) }

    LaunchedEffect(timerState.isFinished) {
        if (timerState.isFinished && !reportDialogShown) {
            showReportDialog = true
            reportDialogShown = true
        }
    }

    LaunchedEffect(timerState.remainingTimeMillis) {
        if (timerState.remainingTimeMillis == timerState.totalTimeMillis) {
            reportDialogShown = false
        }
    }

    MaterialTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = formatTime(timerState.remainingTimeMillis),
                    fontSize = 60.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                LinearProgressIndicator(
                    progress = {
                        val progress = timerState.remainingTimeMillis.toFloat() / timerState.totalTimeMillis.toFloat()
                        if (progress.isNaN()) 0f else progress
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (!timerState.isRunning && !timerState.isFinished && timerState.remainingTimeMillis == timerState.totalTimeMillis) {
                        Button(
                            onClick = { viewModel.startTimer() },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Старт")
                        }
                    }

                    if (timerState.isRunning) {
                        Button(
                            onClick = { viewModel.pauseTimer() },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Пауза")
                        }
                    }

                    if (!timerState.isRunning && timerState.remainingTimeMillis < timerState.totalTimeMillis && !timerState.isFinished) {
                        Button(
                            onClick = { viewModel.startTimer() },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Продолжить")
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.resetTimer()
                            showReportDialog = false
                            reportDialogShown = false
                        },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Сброс")
                    }
                }

                if (timerState.isFinished) {
                    Text(
                        text = "Время вышло!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }

    if (showReportDialog) {
        ReportDialog(
            onDismiss = {
                showReportDialog = false
            },
            onSubmit = { work, life, rest ->
                scope.launch {
                    repo.addReport(work, life, rest)
                }
                viewModel.resetTimer()
                showReportDialog = false
            }
        )
    }
}

fun formatTime(millis: Long): String {
    val minutes = (millis / 1000) / 60
    val seconds = (millis / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}

@Composable
fun ReportDialog(onDismiss: () -> Unit, onSubmit: (Int, Int, Int) -> Unit) {
    var work by remember { mutableIntStateOf(0) }
    var life by remember { mutableIntStateOf(0) }
    var rest by remember { mutableIntStateOf(0) }
    var expandedWork by remember { mutableStateOf(false) }
    var expandedLife by remember { mutableStateOf(false) }
    var expandedRest by remember { mutableStateOf(false) }
    val options = (0..60 step 5).toList()
    val isValid = (work + life + rest) == 60

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .background(WorkColor, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Work", color = Color.Black)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(LifeColor, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Life", color = Color.Black)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(RestColor, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Rest", color = Color.Black)
                    }
                }
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Column {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedButton(
                                onClick = { expandedWork = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Work: $work", fontSize = 12.sp)
                            }
                            DropdownMenu(expanded = expandedWork, onDismissRequest = { expandedWork = false }) {
                                options.forEach {
                                    DropdownMenuItem(text = { Text("$it мин") }, onClick = {
                                        work = it
                                        expandedWork = false
                                    })
                                }
                            }
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedButton(
                                onClick = { expandedLife = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Life: $life", fontSize = 12.sp)
                            }
                            DropdownMenu(expanded = expandedLife, onDismissRequest = { expandedLife = false }) {
                                options.forEach {
                                    DropdownMenuItem(text = { Text("$it мин") }, onClick = {
                                        life = it
                                        expandedLife = false
                                    })
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedButton(
                                onClick = { expandedRest = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Rest: $rest", fontSize = 12.sp)
                            }
                            DropdownMenu(expanded = expandedRest, onDismissRequest = { expandedRest = false }) {
                                options.forEach {
                                    DropdownMenuItem(text = { Text("$it мин") }, onClick = {
                                        rest = it
                                        expandedRest = false
                                    })
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (isValid) {
                            onSubmit(work, life, rest)
                        }
                    },
                    enabled = isValid
                ) {
                    Text("Отправить отчет")
                }
                if (!isValid) {
                    Text("Сумма должна быть ровно 60 минут", color = Color.Red, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

@Composable
fun DiagramTab(repo: TrackerData) {
    val stats by repo.stats.collectAsState(initial = Triple(0, 0, 0))
    val total = stats.first + stats.second + stats.third
    val slices = listOf(
        Triple(stats.first, "Work", WorkColor),
        Triple(stats.second, "Life", LifeColor),
        Triple(stats.third, "Rest", RestColor)
    )
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (total == 0) {
            Text("Нет данных для отображения")
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PieChart(slices, total)
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    slices.forEach { (value, label, color) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(color, shape = RoundedCornerShape(4.dp))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("$label: ${if (total > 0) (value * 100 / total) else 0}%  ")
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PieChart(slices: List<Triple<Int, String, Color>>, total: Int) {
    Canvas(modifier = Modifier.size(180.dp)) {
        var startAngle = -90f
        slices.forEach { (value, _, color) ->
            val sweep = if (total > 0) 360f * value / total else 0f
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = true,
                size = Size(size.width, size.height)
            )
            startAngle += sweep
        }
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.End)
    ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
    }
}