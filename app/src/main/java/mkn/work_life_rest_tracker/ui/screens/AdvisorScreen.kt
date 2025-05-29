package mkn.work_life_rest_tracker.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import mkn.work_life_rest_tracker.data.TrackerData
import androidx.compose.ui.platform.LocalContext
import android.app.Application
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.Alignment
import mkn.work_life_rest_tracker.ui.theme.BackRecommendation

@Composable
fun AdvisorScreen(navController: NavController) {
    val context = LocalContext.current
    val repo = remember { TrackerData(context.applicationContext as Application) }
    val stats by repo.stats.collectAsState(initial = Triple(0, 0, 0))
    val scrollState = rememberScrollState()

    val recommendation = remember(stats) {
        generateRecommendation(stats)
    }

    val expandedStates = remember { mutableStateMapOf<String, Boolean>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        RecommendationSection(recommendation = recommendation)

        Spacer(modifier = Modifier.height(24.dp))

        AdviceSection(
            expandedStates = expandedStates,
            onToggle = { key ->
                expandedStates[key] = expandedStates[key] != true
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        BackButton(onClick = { navController.popBackStack() })
    }
}

@Composable
private fun RecommendationSection(recommendation: String) {
    Text(
        text = "Рекомендация:",
        style = MaterialTheme.typography.titleLarge
    )
    Spacer(modifier = Modifier.height(8.dp))
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = BackRecommendation,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = recommendation,
            modifier = Modifier.padding(12.dp),
            color = Color.Black
        )
    }
}

data class AdviceItem(
    val key: String,
    val title: String,
    val text: String
)

@Composable
private fun AdviceSection(
    expandedStates: MutableMap<String, Boolean>,
    onToggle: (String) -> Unit
) {
    val adviceItems = remember {
        listOf(
            AdviceItem("Sleep", "Совет по сну", "Качественный сон — основа энергии и концентрации. " +
                    "Старайтесь спать 7–9 часов и соблюдайте режим даже в выходные."),
            AdviceItem("Move", "Совет по физ. активности", "Регулярная физическая активность снижает стресс и повышает продуктивность. " +
                    "Найдите то, что вам нравится, и добавьте это в рутину."),
            AdviceItem("Prioritize", "Совет по приоритетам", "Четко определяйте, какие задачи действительно важны, а что может подождать."),
            AdviceItem("Schedule", "Совет по отдыху", "Вносите в календарь не только рабочие встречи, но и время для себя: спорт, хобби, прогулки. " +
                    "Относитесь к этому так же серьезно, как к деловым задачам."),
            AdviceItem("Delegate ", "Совет по делегированию", "Не пытайтесь делать всё сами. " +
                    "Передавайте часть задач коллегам, автоматизируйте процессы или просите помощи у домочадцев в бытовых вопросах."),
        )
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        adviceItems.forEach { item ->
            ExpandableAdviceButton(
                expanded = expandedStates[item.key] ?: false,
                onClick = { onToggle(item.key) },
                title = item.title,
                text = item.text
            )
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

@Composable
fun ExpandableAdviceButton(
    expanded: Boolean,
    onClick: () -> Unit,
    title: String,
    text: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF0E68C),
            contentColor = Color.Black)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title)
                Icon(
                    imageVector = if (expanded)
                        Icons.Default.KeyboardArrowUp
                    else
                        Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Свернуть" else "Развернуть"
                )
            }
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = BackRecommendation,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(12.dp),
                    color = Color.Black
                )
            }
        }
    }
}

private fun generateRecommendation(stats: Triple<Int, Int, Int>): String {
    val (work, life, rest) = stats
    val total = work + life + rest

    if (total == 0) return "Нет данных для анализа."

    val workP = work * 100 / total
    val lifeP = life * 100 / total
    val restP = rest * 100 / total

    val ideal = Triple(40, 50, 10)

    if (workP == ideal.first && lifeP == ideal.second && restP == ideal.third) {
        return "У вас идеальный баланс!"
    }

    return buildRecommendationText(workP, lifeP, restP, ideal)
}

private fun buildRecommendationText(
    workP: Int,
    lifeP: Int,
    restP: Int,
    ideal: Triple<Int, Int, Int>
): String = buildString {
    val categories = listOf(
        Triple("Work", workP, ideal.first),
        Triple("Life", lifeP, ideal.second),
        Triple("Rest", restP, ideal.third)
    )

    categories.forEach { (name, current, target) ->
        when {
            current < target -> {
                val hours = (target - current) * 24 / 100
                append("Тратьте больше времени на $name: +$hours ч в день.")
                if (name != "Rest"){
                    append("\n")
                }
            }
            current > target -> {
                val hours = (current - target) * 24 / 100
                append("Тратьте меньше времени на $name: -$hours ч в день.")
                if (name != "Rest"){
                    append("\n")
                }
            }
        }
    }
}