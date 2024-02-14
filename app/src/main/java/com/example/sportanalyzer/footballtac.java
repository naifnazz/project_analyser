package com.example.sportanalyzer;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class footballtac extends AppCompatActivity {

    private ImageView[] redPlayers = new ImageView[11];
    private ImageView[] bluePlayers = new ImageView[11];

    private float[] xDeltaRed = new float[11];
    private float[] yDeltaRed = new float[11];
    private float[] xDeltaBlue = new float[11];
    private float[] yDeltaBlue = new float[11];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_footballtac);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Initialize red players
        for (int i = 0; i < 11; i++) {
            redPlayers[i] = findViewById(getResources().getIdentifier("redPlayer" + (i + 1), "id", getPackageName()));
            setTouchListener(redPlayers[i], true, i);
        }

        // Initialize blue players
        for (int i = 0; i < 11; i++) {
            bluePlayers[i] = findViewById(getResources().getIdentifier("bluePlayer" + (i + 1), "id", getPackageName()));
            setTouchListener(bluePlayers[i], false, i);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchListener(final ImageView playerIcon, final boolean isRed, final int index) {
        playerIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final float x = event.getRawX();
                final float y = event.getRawY();

                float[] xDeltaArray = isRed ? xDeltaRed : xDeltaBlue;
                float[] yDeltaArray = isRed ? yDeltaRed : yDeltaBlue;

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        xDeltaArray[index] = x - playerIcon.getX();
                        yDeltaArray[index] = y - playerIcon.getY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        playerIcon.setX(x - xDeltaArray[index]);
                        playerIcon.setY(y - yDeltaArray[index]);
                        break;
                }

                return true;
            }
        });
    }
}