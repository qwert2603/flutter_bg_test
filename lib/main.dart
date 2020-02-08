import 'dart:math';
import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_bg_test/bg_main.dart';
import 'package:flutter_bg_test/utils.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(primarySwatch: Colors.blue),
      home: MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _counter = -1;

  final fg_channel = const MethodChannel("fg_channel");

  @override
  void initState() {
    super.initState();

    final handle = PluginUtilities.getCallbackHandle(bgMain).toRawHandle();
    fg_channel.setMethodCallHandler((call) async {
      print("fg_channel begin ${call.method} ${call.arguments}");
      if (call.method == "counter_updated") {
        await onCounterUpdated();
      }
      if (call.method == "notification_clicked") {
        await onNotificationClicked(fg_channel);
      }
      print("fg_channel end ${call.method} ${call.arguments}");
    });
    fg_channel.invokeMethod(
      "save_handle",
      {"handle": handle},
    );
    onCounterUpdated();
  }

  Future<void> onCounterUpdated() async {
    final counter = await fg_channel.invokeMethod("get_counter");
    setState(() => _counter = counter);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(widget.title)),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              '_counter=$_counter',
              style: Theme.of(context).textTheme.display2,
            ),
            Divider(),
            MaterialButton(
              child: Text("inc_counter"),
              onPressed: () {
                fg_channel.invokeMethod("inc_counter");
              },
              color: Colors.orange,
            ),
            Divider(),
            MaterialButton(
              child: Text("show_notification"),
              onPressed: () {
                fg_channel.invokeMethod(
                  "show_notification",
                  {"id": Random().nextInt(10000)},
                );
              },
              color: Colors.red,
            ),
          ],
        ),
      ),
    );
  }
}
