package com.example.healthtrack.core.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.healthtrack.R
import com.example.healthtrack.domain.model.AlertLevel
import com.example.healthtrack.ui.dashboard.DashboardActivity

object DiagnosticNotificationHelper {

    private const val CHANNEL_ID = "diagnostic_alerts_channel"
    private const val NOTIFICATION_ID = 2001

    @SuppressLint("MissingPermission")
    fun showDiagnosticAlert(
        context: Context,
        title: String,
        message: String,
        alertLevel: AlertLevel
    ) {
        if (alertLevel == AlertLevel.NONE) return

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.diagnostic_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.diagnostic_channel_desc)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, DashboardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val priority = if (alertLevel == AlertLevel.CRITICAL) {
            NotificationCompat.PRIORITY_MAX
        } else {
            NotificationCompat.PRIORITY_HIGH
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_monitor_heart)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(priority)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}