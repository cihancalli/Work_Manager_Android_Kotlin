package com.zerdasoftware.workermanager.Worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.zerdasoftware.workermanager.MainActivity
import com.zerdasoftware.workermanager.R

class MyWorker(val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val total = 10 + 20
        println("Background processing result: $total")
        createNotification(total)
        return Result.success()
    }

    private fun createNotification(mySavedNumber: Int) {
        val builder : NotificationCompat.Builder

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(context, MainActivity::class.java)
        val goToIntent = PendingIntent.getActivity(context,1,intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {

            val channelID = "channelID"
            val channelName = "channelName"
            val channelDescription = "channelDescription"
            val channelPriority = NotificationManager.IMPORTANCE_HIGH

            var channel: NotificationChannel? = notificationManager.getNotificationChannel(channelID)

            if (channel == null) {
                channel = NotificationChannel(channelID,channelName,channelPriority)
                channel.description = channelDescription
                notificationManager.createNotificationChannel(channel)
            }

            builder = NotificationCompat.Builder(context,channelID)
            builder.setContentTitle("Title")
                .setContentText("Worker Count $mySavedNumber")
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentIntent(goToIntent)
                .setAutoCancel(true)
        }

        else {
            builder = NotificationCompat.Builder(context)
            builder.setContentTitle("Title")
                .setContentText("Worker Count $mySavedNumber")
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentIntent(goToIntent)
                .setAutoCancel(true)
                .priority = Notification.PRIORITY_HIGH
        }

        notificationManager.notify(1,builder.build())
    }
}