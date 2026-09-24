import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:intl/intl.dart';

class ClockPage extends StatefulWidget {
  const ClockPage({super.key});

  @override
  State<ClockPage> createState() => _ClockPageState();
}

class _ClockPageState extends State<ClockPage> {
  String clockText = '';
  Timer? _timer;

  void startTimer() {
    _timer = Timer.periodic(
      const Duration(seconds: 1),
      (timer) => setClockText(),
    );
  }

  void stopTimer() {
    _timer?.cancel();
    _timer = null;
  }

  void setClockText() {
    setState(() {
      clockText = DateFormat.Hm().format(DateTime.now());
    });
  }

  @override
  void initState() {
    super.initState();

    setClockText();
    startTimer();
    SystemChrome.setEnabledSystemUIMode(SystemUiMode.immersiveSticky);
  }

  @override
  void dispose() {
    SystemChrome.setEnabledSystemUIMode(SystemUiMode.edgeToEdge);
    stopTimer();

    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      extendBodyBehindAppBar: true,
      body: Center(
        child: Text(
          clockText,
          textAlign: TextAlign.center,
          style: TextStyle(
            color: Colors.white,
            fontSize: 475,
            fontFamily: "Agilera",
          ),
        ),
      ),
    );
  }
}
