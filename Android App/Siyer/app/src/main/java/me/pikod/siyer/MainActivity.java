package me.pikod.siyer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    PiManager pim;
    Button connectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(PiManager.getInstance() == null)
            pim = new PiManager(this);
        else {

            pim = PiManager.getInstance();
            pim.setActivity(this);

        }

        connectBtn = findViewById(R.id.connectBtn);
        connectBtn.setOnClickListener(v -> connect());
        connect();

    }

    private void connect(){

        pim.getLogger().Info("Trying to connect...");
        connectBtn.setEnabled(false);
        byte e = pim.tryConnect();
        if(e == 0)
            openController();
        else
            ( (TextView) findViewById(R.id.errorText) ).setText(pim.getErrorByID(e));
        connectBtn.setEnabled(true);

    }

    private void openController(){
        Intent intent = new Intent(this, Controller.class);
        startActivity(intent);
        finish();
    }
}
