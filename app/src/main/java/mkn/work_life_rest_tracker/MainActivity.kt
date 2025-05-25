package mkn.work_life_rest_tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mkn.work_life_rest_tracker.ui.screens.MainScreen
import mkn.work_life_rest_tracker.ui.screens.TrackerScreen
import mkn.work_life_rest_tracker.ui.screens.AdvisorScreen
import mkn.work_life_rest_tracker.ui.theme.WorkLifeRestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WorkLifeRestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "main") {
                        composable("main") {
                            MainScreen(navController)
                        }
                        composable("tracker") {
                            TrackerScreen(navController)
                        }
                        composable("advisor") {
                            AdvisorScreen(navController)
                        }
                    }
                }
            }
        }
    }
}