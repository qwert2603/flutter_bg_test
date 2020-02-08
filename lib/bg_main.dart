import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_bg_test/utils.dart';

@pragma('vm:entry-point')
void bgMain() {
  const MethodChannel bg_channel = MethodChannel("bg_channel");
  WidgetsFlutterBinding.ensureInitialized();

  bg_channel.setMethodCallHandler((MethodCall call) async {
    print("bg_channel begin ${call.method} ${call.arguments}");
    if (call.method == "notification_clicked") {
      await onNotificationClicked(bg_channel);
    }
    print("bg_channel end ${call.method} ${call.arguments}");
  });
}
