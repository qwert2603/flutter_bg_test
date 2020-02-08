package com.qwert2603.flutter_bg_test

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity : FlutterActivity() {
    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine)

        val fgChannel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "fg_channel")
        App.fgChannel = fgChannel
        fgChannel.setMethodCallHandler { call, result ->
            App.log("fg_channel ${call.method} ${call.arguments}")
            when (call.method) {
                "save_handle" -> {
                    val handle = call.argument<Long>("handle")!!
                    App.handle = handle
                    Toast.makeText(this, "handle=$handle ", Toast.LENGTH_SHORT).show()
                    result.success(null)
                }
                "get_counter" -> result.success(App.counter ?: 0)
                "inc_counter" -> {
                    App.counter = (App.counter ?: 0) + 1
                    result.success(null)
                    fgChannel.invokeMethod("counter_updated", null)
                }
                "show_notification" -> {
                    showNotification(call.argument<Int>("id")!!)
                    result.success(null)
                }
                "notification_click_consumed" -> {
                    App.onNotificationClickConsumed?.invoke()
                    result.success(null)
                }
                else -> result.notImplemented()
            }
        }
    }

    override fun cleanUpFlutterEngine(flutterEngine: FlutterEngine) {
        App.fgChannel = null
    }

    private fun showNotification(id: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @Suppress("DEPRECATION") val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel("ch", "ch", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val pendingIntent = PendingIntent.getBroadcast(
                this,
                id,
                Intent(this, NotificationBroadcastReceiver::class.java)
                        .putExtra("id", id),
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(this, "ch")
                .setSmallIcon(R.drawable.launch_background)
                .setContentTitle("id=$id")
                .setAutoCancel(true)
                .setShowWhen(true)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.launch_background, "the_action", pendingIntent)
                .build()
        NotificationManagerCompat.from(this).notify(id, notification)
    }
}
