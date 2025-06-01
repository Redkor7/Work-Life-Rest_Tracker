package mkn.work_life_rest_tracker.timer_logic

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import mkn.work_life_rest_tracker.data.TimerState

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private var timerService: TimerService? = null
    private var bound = false

    private val _timerState = MutableLiveData(TimerState())
    val timerState: LiveData<TimerState> = _timerState

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TimerService.TimerBinder
            timerService = binder.getService()
            bound = true
            timerService?.timerState?.observeForever { state ->
                _timerState.postValue(state)
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            bound = false
        }
    }

    init {
        bindToService()
    }

    private fun bindToService() {
        Intent(getApplication(), TimerService::class.java).also { intent ->
            getApplication<Application>().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    fun startTimer() {
        val intent = Intent(getApplication(), TimerService::class.java).apply {
            action = TimerService.ACTION_START_TIMER
        }
        getApplication<Application>().startService(intent)
    }

    fun pauseTimer() {
        val intent = Intent(getApplication(), TimerService::class.java).apply {
            action = TimerService.ACTION_PAUSE_TIMER
        }
        getApplication<Application>().startService(intent)
    }

    fun resetTimer() {
        timerService?.resetTimer()
    }

    override fun onCleared() {
        super.onCleared()
        if (bound) {
            getApplication<Application>().unbindService(connection)
            bound = false
        }
    }
}