package mkn.work_life_rest_tracker.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import mkn.work_life_rest_tracker.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun mainScreen_shouldDisplayAllButtons() {
        composeTestRule.onNodeWithText("Трекер").assertExists()
        composeTestRule.onNodeWithText("Советчик").assertExists()
    }

    @Test
    fun mainScreen_shouldNavigateToTrackerScreen() {
        composeTestRule.onNodeWithText("Трекер").performClick()
        composeTestRule.onNodeWithText("Старт").assertExists()
        composeTestRule.onNodeWithText("Сброс").assertExists()
    }

    @Test
    fun mainScreen_shouldNavigateToAdvisorScreen() {
        composeTestRule.onNodeWithText("Советчик").performClick()
        composeTestRule.onNodeWithText("Рекомендация:").assertExists()
    }
} 