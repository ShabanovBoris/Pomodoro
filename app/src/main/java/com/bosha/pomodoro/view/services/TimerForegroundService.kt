package com.bosha.pomodoro.view.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import com.bosha.pomodoro.MainActivity
import com.bosha.pomodoro.R
import com.bosha.pomodoro.data.entity.time
import com.bosha.pomodoro.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@RequiresApi(Build.VERSION_CODES.M)
@ExperimentalTime
class TimerForegroundService : Service() {

    private var isStarted = false
    private var job: Job? = null
    private var notificationManager: NotificationManagerCompat? = null


    private val builder by lazy(LazyThreadSafetyMode.NONE) {
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Pomodoro timer")
            .setGroup("Timer group")
            .setGroupSummary(false)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(getStartActivityPendingIntent())
            .setSilent(true)
            .setLargeIcon(
                BitmapFactory.decodeResource(application.resources,
                R.mipmap.ic_launcher_round))
            .setSmallIcon(IconCompat.createWithResource(applicationContext,R.drawable.ic_tomato))
            .addAction(NotificationCompat.Action(null, "Close", getStopPendingIntent()))
    }

    private fun getNotification(content: String, beginTime: Int, progress: Int) =
        builder
            .setContentText(content)
            .setProgress(beginTime, progress, false)
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.best_pomodoro_apps_blog_header_main_1
                        )?.toBitmap()
                    )
                    .setBigContentTitle(content)
            )
            .build()


    override fun onCreate() {
        super.onCreate()
        notificationManager = NotificationManagerCompat.from(this)
        Log.e("TAG", "onCreate: $this")
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.e("TAG", "onDestroy: $this")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        processCommand(intent)
        Log.e("TAG", "onStartCommand: ${intent?.extras?.getString(COMMAND_ID)}")
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun processCommand(intent: Intent?) {
        when (intent?.extras?.getString(COMMAND_ID) ?: INVALID) {
            COMMAND_START -> {
                val beginTime = intent?.extras?.getLong(BEGIN_TIME_MS) ?: return
                val msUntilFinish = intent.extras?.getLong(UNTIL_FINISH_MS) ?: beginTime
                commandStart(beginTime, msUntilFinish)
            }
            COMMAND_STOP -> commandStop()
            INVALID -> return
        }
    }

    private fun commandStart(beginTime: Long, msUntilFinish: Long) {
        if (isStarted) return
        try {
//            ContextCompat.startForegroundService(
//                this, Intent(this, TimerForegroundService::class.java)
//            )
            startForegroundAndShowNotification()
            continueTimer(beginTime, msUntilFinish)
        } finally {
            isStarted = true
        }
    }

    private fun continueTimer(beginTime: Long, msUntilFinish: Long) {
        val startTime = System.currentTimeMillis()
        job = tickerFlow(Duration.Companion.milliseconds(INTERVAL))
            .onEach {

                notificationManager?.notify(
                    NOTIFICATION_ID,
                    getNotification(
                        (msUntilFinish - (System.currentTimeMillis() - startTime)).time(),
                        beginTime.toInt(),
                        msUntilFinish.toInt() - (System.currentTimeMillis() - startTime).toInt()
                    )
                )
                if ((msUntilFinish.toInt() - (System.currentTimeMillis() - startTime)) <= 0L) {
                    commandStop()

                    RingtoneManager.getRingtone(
                        applicationContext,
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    ).play()
                }
            }
            .launchIn(CoroutineScope(Dispatchers.Main))
    }


    private fun commandStop() {
        if (isStarted.not()) return
        Log.e("TAG", "commandStop: called")
        try {
            job?.cancel()
            stopForeground(true)
            stopSelf()
        } finally {
            isStarted = false
        }
    }


    private fun startForegroundAndShowNotification() {
        createChannel()
        val notification = getNotification("content", 0, 0)
        startForeground(NOTIFICATION_ID, notification)
    }


    private fun createChannel() {
        if (notificationManager?.getNotificationChannel(CHANNEL_ID) == null) {
            val notificationChannel = NotificationChannelCompat.Builder(
                CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_HIGH
            )
                .setName("pomodoro")
                .setDescription("This notification will show you the timer status")
                .build()
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }

    private fun getStartActivityPendingIntent(): PendingIntent? {
        val resultIntent = Intent(this, MainActivity::class.java)
//        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT)
    }

    private fun getStopPendingIntent(): PendingIntent? {
        val intent = Intent(applicationContext, TimerForegroundService::class.java).apply {
            putExtra(COMMAND_ID, COMMAND_STOP)
        }
        return PendingIntent.getService(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
    }


    private companion object {
        private const val CHANNEL_ID = "Channel_ID"
        private const val NOTIFICATION_ID = 111 + 222 + 333
        private const val INTERVAL = 1000L
    }
}