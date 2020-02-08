package com.qwert2603.flutter_bg_test

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class NotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()

        val channel = App.fgChannel ?: App.getOrCreateBgChannel(context)

        if (channel == null) {
            context.startActivity(Intent(context, MainActivity::class.java))
            return
        }

        val id = intent.getIntExtra("id", 0)
        App.log("NotificationBroadcastReceiver onReceive id=$id")

        NotificationManagerCompat.from(context).cancel(id)

        App.onNotificationClickConsumed = {
            App.log("pendingResult.finish()")
            pendingResult.finish()
        }

        channel.invokeMethod("notification_clicked", id)
    }
}