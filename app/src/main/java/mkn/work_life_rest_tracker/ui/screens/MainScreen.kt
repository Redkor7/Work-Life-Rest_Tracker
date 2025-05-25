package mkn.work_life_rest_tracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Work-Life-Rest")

        Spacer(modifier = Modifier.height(50.dp))

        Button(
            onClick = { navController.navigate("tracker") },
            modifier = Modifier
                .width(300.dp)
                .height(60.dp)
        ) {
            Text(text = "Трекер")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("advisor") },
            modifier = Modifier
                .width(300.dp)
                .height(60.dp)
        ) {
            Text(text = "Советчик")
        }
    }
}