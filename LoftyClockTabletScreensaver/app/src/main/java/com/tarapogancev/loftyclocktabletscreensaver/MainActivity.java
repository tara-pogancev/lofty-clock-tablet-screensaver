package com.tarapogancev.loftyclocktabletscreensaver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.tarapogancev.loftyclocktabletscreensaver.service.BluetoothService;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    TextView time;
    Button buttonBack, buttonDisconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        time = findViewById(R.id.text_time);
        buttonBack = findViewById(R.id.button_back);
        buttonDisconnect = findViewById(R.id.button_disconnect);

        writeTime();
        hideScreenElements();

        Intent bluetoothService = new Intent(getApplicationContext(), BluetoothService.class);
        ContextCompat.startForegroundService(this, bluetoothService);

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                writeTime();
                handler.postDelayed(this, 1000);
            }
        });

        Handler handler2 = new Handler();
        handler2.post(new Runnable() {
            @Override
            public void run() {
                hideScreenElements();
                handler2.postDelayed(this, 5000);
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //moveTaskToBack(true);
                BluetoothService.connectToAudioDevice();
            }
        });

        buttonDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothService.disconnectFromAudioDevice();
            }
        });

    }

    private void writeTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        time.setText(formatter.format(date));
    }

    private void hideScreenElements() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

}



