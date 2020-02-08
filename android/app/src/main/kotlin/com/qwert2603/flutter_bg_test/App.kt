package com.qwert2603.flutter_bg_test

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import io.flutter.app.FlutterApplication
import io.flutter.embedding.engine.loader.FlutterLoader
import io.flutter.plugin.common.MethodChannel
import io.flutter.view.FlutterCallbackInformation
import io.flutter.view.FlutterMain
import io.flutter.view.FlutterNativeView
import io.flutter.view.FlutterRunArguments
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class App : FlutterApplication() {

    companion object {
        lateinit var prefs: SharedPreferences

        private var bgChannel: MethodChannel? = null

        fun getOrCreateBgChannel(context: Context): MethodChannel? {
            val handle = this.handle
            log("handle=$handle")
            if (bgChannel == null && handle != null) {
                val callbackInfo = FlutterCallbackInformation.lookupCallbackInformation(handle)

                val backgroundFlutterView = FlutterNativeView(context.applicationContext, true)

                val args = FlutterRunArguments()
                args.bundlePath = FlutterLoader.getInstance().findAppBundlePath()
                args.entrypoint = callbackInfo.callbackName
                args.libraryPath = callbackInfo.callbackLibraryPath

                backgroundFlutterView.runFromBundle(args)

                val bgChannel = MethodChannel(backgroundFlutterView, "bg_channel")
                App.bgChannel = bgChannel

                bgChannel.setMethodCallHandler { call, result ->
                    log("bg_channel ${call.method} ${call.arguments}")
                    when (call.method) {
                        "inc_counter" -> {
                            counter = (counter ?: 0) + 1
                            result.success(null)
                        }
                        "notification_click_consumed" -> {
                            onNotificationClickConsumed?.invoke()
                            result.success(null)
                        }
                        else -> result.notImplemented()
                    }
                }
            }
            return bgChannel
        }

        var fgChannel: MethodChannel? = null

        var onNotificationClickConsumed: (() -> Unit)? = null

        fun log(s: String) = Log.d("AASSDD", s)

        var handle by prefsLong({ prefs }, "handle")
        var counter by prefsLong({ prefs }, "counter")

        private fun prefsLong(prefs: () -> SharedPreferences, key: String) = object : ReadWriteProperty<Any, Long?> {
            override fun getValue(thisRef: Any, property: KProperty<*>): Long? =
                    if (prefs().contains(key)) prefs().getLong(key, 0) else null

            override fun setValue(thisRef: Any, property: KProperty<*>, value: Long?) {
                prefs().edit()
                        .let {
                            if (value != null) {
                                it.putLong(key, value)
                            } else {
                                it.remove(key)
                            }
                        }
                        .apply()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        FlutterMain.ensureInitializationComplete(this, arrayOf())
    }
}