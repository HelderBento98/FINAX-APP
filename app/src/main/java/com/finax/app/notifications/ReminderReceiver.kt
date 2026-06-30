package com.finax.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.finax.app.R

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val descricao = intent.getStringExtra("descricao") ?: return
        val horario = intent.getStringExtra("horario") ?: ""

        showNotification(context, descricao, horario)
    }

    private fun showNotification(context: Context, descricao: String, horario: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "finax_reminders"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Compromissos",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificações de compromissos agendados"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Compromisso Agendado!")
            .setContentText("$descricao às $horario")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}

object ReminderScheduler {

    fun scheduleReminder(context: Context, lembreteId: String, descricao: String, horario: String, data: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = "com.finax.app.REMINDER_ALARM"
            putExtra("lembreteId", lembreteId)
            putExtra("descricao", descricao)
            putExtra("horario", horario)
        }

        val pendingIntent = android.app.PendingIntent.getBroadcast(
            context,
            lembreteId.hashCode(),
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val parts = data.split("/")
        val timeParts = horario.split(":")
        if (parts.size < 3 || timeParts.size < 2) return

        val cal = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.DAY_OF_MONTH, parts[0].toIntOrNull() ?: return)
            set(java.util.Calendar.MONTH, (parts[1].toIntOrNull() ?: return) - 1)
            set(java.util.Calendar.YEAR, parts[2].toIntOrNull() ?: return)
            set(java.util.Calendar.HOUR_OF_DAY, timeParts[0].toIntOrNull() ?: return)
            set(java.util.Calendar.MINUTE, timeParts[1].toIntOrNull() ?: return)
            set(java.util.Calendar.SECOND, 0)
        }

        if (cal.timeInMillis > System.currentTimeMillis()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                alarmManager.set(android.app.AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    android.app.AlarmManager.RTC_WAKEUP,
                    cal.timeInMillis,
                    pendingIntent
                )
            }
        }
    }

    fun cancelReminder(context: Context, lembreteId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = android.app.PendingIntent.getBroadcast(
            context,
            lembreteId.hashCode(),
            intent,
            android.app.PendingIntent.FLAG_NO_CREATE or android.app.PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let { alarmManager.cancel(it) }
    }
}
