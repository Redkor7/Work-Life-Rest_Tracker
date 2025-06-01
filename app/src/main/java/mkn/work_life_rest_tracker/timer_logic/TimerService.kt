package mkn.work_life_rest_tracker.timer_logic

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import mkn.work_life_rest_tracker.MainActivity
import mkn.work_life_rest_tracker.data.TimerState

class TimerService : Service() {

    private val binder = TimerBinder()
    private var countDownTimer: CountDownTimer? = null

    val timerState = MutableLiveData(TimerState())

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "timer_channel"
        const val ACTION_START_TIMER = "START_TIMER"
        const val ACTION_PAUSE_TIMER = "PAUSE_TIMER"
        const val ACTION_STOP_TIMER = "STOP_TIMER"
        const val FINISH_CHANNEL_ID = "timer_finish_channel"
    }

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_TIMER -> startTimer()
            ACTION_PAUSE_TIMER -> pauseTimer()
            ACTION_STOP_TIMER -> stopTimer()
        }
        return START_STICKY
    }

    fun startTimer() {
        val currentState = timerState.value ?: TimerState()
        if (currentState.isRunning) return

        startForeground(NOTIFICATION_ID, createNotification())

        countDownTimer = object : CountDownTimer(
            currentState.remainingTimeMillis,
            1000
        ) {
            override fun onTick(millisUntilFinished: Long) {
                val newState = TimerState(
                    remainingTimeMillis = millisUntilFinished,
                    isRunning = true,
                    isFinished = false
                )
                timerState.postValue(newState)
                updateNotification()
            }

            override fun onFinish() {
                timerState.postValue(
                    currentState.copy(
                        remainingTimeMillis = 0L,
                        isRunning = false,
                        isFinished = true
                    )
                )
                showFinishedNotification()
                stopSelf()
            }
        }.start()

        timerState.postValue(currentState.copy(isRunning = true))
    }

    fun pauseTimer() {
        countDownTimer?.cancel()
        val currentState = timerState.value ?: TimerState()
        timerState.postValue(currentState.copy(isRunning = false))
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    fun stopTimer() {
        countDownTimer?.cancel()
        timerState.postValue(TimerState())
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    fun resetTimer() {
        countDownTimer?.cancel()
        timerState.postValue(TimerState())
    }

    private fun createNotificationChannel() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val timerChannel = NotificationChannel(
            CHANNEL_ID,
            "Timer Channel",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Timer notifications"
        }

        val finishChannel = NotificationChannel(
            FINISH_CHANNEL_ID,
            "Timer Finish Channel",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Timer finish notifications"
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
            setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI, null)
        }

        notificationManager.createNotificationChannel(timerChannel)
        notificationManager.createNotificationChannel(finishChannel)
    }

     private fun createNotification(): Notification {
       val currentState = timerState.value ?: TimerState()
       val timeText = formatTime(currentState.remainingTimeMillis)

       val pauseIntent = Intent(this, TimerService::class.java).apply {
           action = ACTION_PAUSE_TIMER
       }
       val pausePendingIntent = PendingIntent.getService(
           this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
       )

       val stopIntent = Intent(this, TimerService::class.java).apply {
           action = ACTION_STOP_TIMER
       }
       val stopPendingIntent = PendingIntent.getService(
           this, 1, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
       )

       val openAppIntent = Intent(this, MainActivity::class.java)
       val openAppPendingIntent = PendingIntent.getActivity(
           this, 0, openAppIntent,
           PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
       )

       return NotificationCompat.Builder(this, CHANNEL_ID)
           .setContentTitle("Таймер")
           .setContentText("Осталось: $timeText")
           .setSmallIcon(android.R.drawable.ic_dialog_info)
           .setContentIntent(openAppPendingIntent)
           .setOngoing(true)
           .addAction(android.R.drawable.ic_media_pause, "Пауза", pausePendingIntent)
           .addAction(android.R.drawable.ic_delete, "Стоп", stopPendingIntent)
           .build()
    }

    private fun updateNotification() {
       val notification = createNotification()
       val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
       notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun showFinishedNotification() {
        stopForeground(STOP_FOREGROUND_REMOVE)

        val openAppIntent = Intent(this, MainActivity::class.java)
        val openAppPendingIntent = PendingIntent.getActivity(
            this, 0, openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, FINISH_CHANNEL_ID)
            .setContentTitle("Таймер завершен!")
            .setContentText("60 минут истекло")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(openAppPendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setFullScreenIntent(openAppPendingIntent, true)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID + 1, notification)
    }

    private fun formatTime(millis: Long): String {
       val minutes = (millis / 1000) / 60
       val seconds = (millis / 1000) % 60
       return String.format("%02d:%02d", minutes, seconds)
    }
}