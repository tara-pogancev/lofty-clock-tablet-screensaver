import 'package:flutter/material.dart';
import 'package:lofty_clock/features/clock/clock_page.dart';
import 'package:lofty_clock/theming/lofty_clock_theme.dart';

void main() {
  runApp(const MainApp());
}

class MainApp extends StatelessWidget {
  const MainApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: loftyClockTheme,
      home: const ClockPage(),
    );
  }
}
