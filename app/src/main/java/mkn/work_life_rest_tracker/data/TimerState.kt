package mkn.work_life_rest_tracker.data

data class TimerState(
    val totalTimeMillis: Long = 60 * 60 * 1000L,
    val remainingTimeMillis: Long = 60 * 60 * 1000L,
    val isRunning: Boolean = false,
    val isFinished: Boolean = false
)