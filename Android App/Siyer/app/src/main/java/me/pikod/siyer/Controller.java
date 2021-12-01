package me.pikod.siyer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class Controller extends AppCompatActivity {

    PiManager btManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        btManager = PiManager.getInstance();

        Button forwardBtn = findViewById(R.id.forwardBtn);
        forwardBtn.setOnClickListener(v -> {

            btManager.sendByte((byte) 1); // means go forward :>

        });

        Button backwardBtn = findViewById(R.id.backwardBtn);
        backwardBtn.setOnClickListener(v -> {

            btManager.sendByte((byte) 2); // means go backward :>

        });

        Button stopBtn = findViewById(R.id.stopBtn);
        stopBtn.setOnClickListener(v -> {

            btManager.sendByte((byte) 3); // means stop :>

        });

        Button soundsBtn = findViewById(R.id.soundsBtn);
        soundsBtn.setOnClickListener(v -> {

            btManager.sendByte((byte) 4); // means I want to see what can I play :>

        });

    }

}