package com.tarapogancev.loftyclocktabletscreensaver.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.tarapogancev.loftyclocktabletscreensaver.MainActivity;
import com.tarapogancev.loftyclocktabletscreensaver.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ForegroundService extends Service {

    private static BluetoothAdapter mBluetoothAdapter;
    private static BluetoothSocket mSocket;
    private List<BluetoothDevice> mDeviceList = new ArrayList<>();
    static final UUID DOCK_UUID = UUID.fromString("931c7e8a-540f-4686-b798-e8df0a2ad9f7");
    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    private AppState lastState = AppState.NOT_CHARGING;
    ChargeDetection chargeDetector;
    ScreenDetection screenDetector;
    private static boolean isActive = false;

    public ForegroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isActive = true;
        chargeDetector = new ChargeDetection();
        screenDetector = new ScreenDetection();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        createNotificationChannel();

        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        this.registerReceiver(chargeDetector, filter);
        startListeningForCharging();

        IntentFilter filter2 = new IntentFilter();
        filter2.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter2.addAction(Intent.ACTION_SCREEN_OFF);
        this.registerReceiver(screenDetector, filter2);
        startListeningForScreenOff();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Lofty Clock Screensaver")
                .setContentText("Tap to return to the application.")
                .setSmallIcon(R.drawable.ic_launcher_round)
                .setContentIntent(pendingIntent)
                .setSilent(true)
                .build();
        startForeground(1, notification);

        return START_NOT_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_MIN
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void startListeningForCharging() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, ifilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        if (isCharging) {
            if (lastState == AppState.NOT_CHARGING) {
                try {
                    connectToAudioDevice();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            lastState = AppState.CHARGING;
        } else {
            lastState = AppState.NOT_CHARGING;
            disconnectFromAudioDevice();
        }
    }

    private void startListeningForScreenOff() {
        Log.e("123", "Screen is off");
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        Intent screenState = this.registerReceiver(null, ifilter);
        Log.e("123", "Screen is off");
    }

    @SuppressLint("MissingPermission")
    public static void connectToAudioDevice() {
        if (mBluetoothAdapter != null) {
            Set<BluetoothDevice> connectedDevices = mBluetoothAdapter.getBondedDevices();
            BluetoothDevice device = null;
            for (BluetoothDevice devices : connectedDevices) {
                if (devices.getName().equals("Lenovo Smart Dock 7A621C")) {
                    device = devices;

                    try {
                        mSocket = device.createRfcommSocketToServiceRecord(DOCK_UUID);
                        mSocket.connect();
                        Log.e("INFO", "Connected");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    public static void disconnectFromAudioDevice() {
        try {
            mSocket.close();
            Log.e("INFO", "Disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startMainActivity() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        this.startActivity(mainIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(chargeDetector);
        unregisterReceiver(screenDetector);
        isActive = false;
    }

    public static boolean isActive() {
        return isActive;
    }
}

class ChargeDetection extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        Log.e("LOG", "Charge mode changed");
    }

}

class ScreenDetection extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        Intent screenStatus = context.registerReceiver(null, ifilter);
    }
}