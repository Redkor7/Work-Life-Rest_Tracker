package mkn.work_life_rest_tracker.timer_logic

import android.app.Application
import android.content.ComponentName
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import mkn.work_life_rest_tracker.data.TimerState
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import kotlin.jvm.java
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class TimerViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockApplication: Application

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockService: TimerService

    @Mock
    private lateinit var mockBinder: TimerService.TimerBinder

    private lateinit var timerViewModel: TimerViewModel
    private lateinit var mockTimerState: MutableLiveData<TimerState>

    @Before
    fun setup() {
        mockTimerState = MutableLiveData(TimerState())
        timerViewModel = TimerViewModel(mockApplication)
    }

    @Test
    fun `test service connection sets up timer state observer`() {
        `when`(mockBinder.getService()).thenReturn(mockService)
        `when`(mockService.timerState).thenReturn(mockTimerState)

        val testState =
            TimerState(
                totalTimeMillis = 60000L,
                remainingTimeMillis = 30000L,
                isRunning = true,
                isFinished = false,
            )
        mockTimerState.value = testState

        val connection = timerViewModel.getConnection()
        connection.onServiceConnected(ComponentName(mockContext, TimerService::class.java), mockBinder)

        assertEquals(testState, timerViewModel.timerState.value)
    }

    @Test
    fun `test resetTimer calls service method`() {
        `when`(mockBinder.getService()).thenReturn(mockService)
        `when`(mockService.timerState).thenReturn(mockTimerState)

        val connection = timerViewModel.getConnection()
        connection.onServiceConnected(ComponentName(mockContext, TimerService::class.java), mockBinder)

        timerViewModel.resetTimer()

        verify(mockService).resetTimer()
    }

    @Test
    fun `test onCleared unbinds service`() {
        `when`(mockBinder.getService()).thenReturn(mockService)
        `when`(mockService.timerState).thenReturn(mockTimerState)

        val connection = timerViewModel.getConnection()
        connection.onServiceConnected(ComponentName(mockContext, TimerService::class.java), mockBinder)

        val boundField = TimerViewModel::class.java.getDeclaredField("bound")
        boundField.isAccessible = true
        boundField.set(timerViewModel, true)

        timerViewModel.onCleared()

        verify(mockApplication).unbindService(connection)
    }
}
