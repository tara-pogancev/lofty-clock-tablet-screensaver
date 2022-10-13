package com.tarapogancev.loftyclocktabletscreensaver.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.tarapogancev.loftyclocktabletscreensaver.MainActivity;

public class ChargingService extends Service {
    private AppState lastState = AppState.NOT_CHARGING;

    public ChargingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        //Add broadcast receiver for plugging in and out here
        ChargeDetection chargeDetector = new ChargeDetection(); //This is the name of the class at the bottom of this code.

        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        this.registerReceiver(chargeDetector, filter);

        startForeground();

        return super.onStartCommand(intent, flags, startId);
    }

    private void startForeground() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, ifilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        if (isCharging) {
            if (lastState == AppState.NOT_CHARGING) {
                try {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Toast.makeText(this, "Charging", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            lastState = AppState.CHARGING;
        } else {
            lastState = AppState.NOT_CHARGING;
            Toast.makeText(this, "Not Charging", Toast.LENGTH_SHORT).show();
        }
    }
}

    class ChargeDetection extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

    }
}
