package me.pikod.siyer;

import android.annotation.SuppressLint;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.material.button.MaterialButton;

public class SoundPlayer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soundplayer);

        LinearLayout layout = findViewById(R.id.linearLayout);
        layout.removeAllViews();

        PiManager pi = PiManager.getInstance();

        String[] sounds = pi.sounds;

        int index = 0;
        for( String sound : sounds ) {

            // Creations...
            CardView card = new CardView(this);
            ConstraintLayout panel = new ConstraintLayout(this);
            ConstraintSet constraintSet = new ConstraintSet();
            TextView soundName = new TextView(this);
            MaterialButton soundPlay = new MaterialButton(this);

            int color;
            if((index % 2) == 0)
                color = R.color.light_gray;
            else
                color = R.color.dark_gray;

            // Properties: Sound Name
            soundName.setId(View.generateViewId());
            soundName.setText(sound);
            ConstraintLayout.LayoutParams params =
                    new ConstraintLayout.LayoutParams(
                            ConstraintLayout.LayoutParams.WRAP_CONTENT,
                            ConstraintLayout.LayoutParams.MATCH_PARENT
                    );
            soundName.setLayoutParams(params);
            soundName.setTextSize(30);
            soundName.setTextColor(getColor(R.color.white));

            // Properties: Sound play
            soundPlay.setTypeface(pi.fontAwesome);
            soundPlay.setId(View.generateViewId());
            ConstraintLayout.LayoutParams params1 =
                    new ConstraintLayout.LayoutParams(
                            128,
                            128
                    );
            soundPlay.setLayoutParams(params1);
            soundPlay.setText(getText(R.string.icon_play_circle));
            soundPlay.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            soundPlay.setTextSize(32);
            soundPlay.setCornerRadius(16);
            soundPlay.setTextColor(getColor(R.color.white));
            soundPlay.setOnClickListener(v -> {
                pi.sendString("play "+sound);
            });

            card.setRadius(0);

            // Panel additions
            panel.setId(View.generateViewId());
            panel.addView(soundName);
            panel.addView(soundPlay);
            panel.setPadding(15, 30, 15, 30);
            panel.setBackgroundColor(getColor(color));

            // Layout additions
            card.addView(panel);
            layout.addView(card);

            // Clone constraint set
            constraintSet.clone(panel);

            constraintSet.connect(soundPlay.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraintSet.connect(soundPlay.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);;
            constraintSet.connect(soundPlay.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);

            constraintSet.connect(soundName.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraintSet.connect(soundName.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            constraintSet.connect(soundName.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);

            // Apply constraint set.
            constraintSet.applyTo(panel);

            index++;

        }

        ((Button) findViewById(R.id.stopSoundButton)).setOnClickListener(v -> {
            pi.sendString("pause");
        });

    }

}
