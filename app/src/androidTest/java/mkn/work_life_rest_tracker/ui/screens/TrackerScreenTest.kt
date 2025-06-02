package mkn.work_life_rest_tracker.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import mkn.work_life_rest_tracker.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TrackerScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun navigateToTrackerScreen() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Трекер", useUnmergedTree = true).performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Старт", useUnmergedTree = true).fetchSemanticsNodes().size == 1
        }
    }

    @Test
    fun trackerScreen_shouldDisplayTimerControls() {
        composeTestRule.onNodeWithText("Старт", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithText("Сброс", useUnmergedTree = true).assertExists()
    }

    @Test
    fun trackerScreen_shouldDisplayTimerDisplay() {
        composeTestRule.onNode(
            hasText("60:00"),
            useUnmergedTree = true
        ).assertExists()
    }

    @Test
    fun trackerScreen_shouldHaveWorkingStartButton() {
        composeTestRule.onNodeWithText("Старт", useUnmergedTree = true).performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Пауза", useUnmergedTree = true).fetchSemanticsNodes().size == 1
        }
    }
} 