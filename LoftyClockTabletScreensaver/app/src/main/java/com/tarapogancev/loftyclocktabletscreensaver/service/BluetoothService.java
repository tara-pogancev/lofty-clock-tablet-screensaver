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
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.tarapogancev.loftyclocktabletscreensaver.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BluetoothService extends Service {

    private static BluetoothAdapter mBluetoothAdapter;
    private static BluetoothSocket mSocket;
    private List<BluetoothDevice> mDeviceList = new ArrayList<>();
    static final UUID DOCK_UUID = UUID.fromString("931c7e8a-540f-4686-b798-e8df0a2ad9f7");
    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    public BluetoothService() {
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
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        createNotificationChannel();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText("Lofty Clock")
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

}