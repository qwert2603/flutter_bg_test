import 'package:flutter/services.dart';

Future<void> onNotificationClicked(MethodChannel methodChannel) async {
  await methodChannel.invokeMethod("inc_counter");
  await methodChannel.invokeMethod("notification_click_consumed");
}
