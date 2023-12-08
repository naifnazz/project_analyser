package com.example.sportanalyzer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlayerFormationOverlay extends View {

    private List<Player> players;
    private Player selectedPlayer;
    private Paint linePaint;
    private Paint circlePaint;
    private Handler handler;
    private long durationMillis = 5000; // Default duration in milliseconds

    public PlayerFormationOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        players = new ArrayList<>();

        linePaint = new Paint();
        linePaint.setColor(Color.RED);
        linePaint.setStrokeWidth(5);
        linePaint.setStyle(Paint.Style.STROKE);

        circlePaint = new Paint();
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.FILL);

        handler = new Handler(Looper.getMainLooper());
    }

    public void addPlayer(float x, float y) {
        Player player = new Player(x, y);
        players.add(player);
        invalidate();

        // Schedule a task to remove the player after the specified duration
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                players.remove(player);
                selectedPlayer = null; // Deselect the player
                invalidate();
            }
        }, durationMillis);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw lines
        for (Player player : players) {
            for (Player otherPlayer : players) {
                if (player != otherPlayer) {
                    drawLine(canvas, player, otherPlayer);
                }
            }
        }

        // Draw circles
        for (Player player : players) {
            drawCircle(canvas, player);
        }

        // Draw a rotating circle for the selected player
        if (selectedPlayer != null) {
            drawRotatingCircle(canvas, selectedPlayer);
        }
    }

    private void drawLine(Canvas canvas, Player player1, Player player2) {
        canvas.drawLine(player1.getX(), player1.getY(), player2.getX(), player2.getY(), linePaint);
    }

    private void drawCircle(Canvas canvas, Player player) {
        canvas.drawCircle(player.getX(), player.getY(), 30, circlePaint);
    }

    private void drawRotatingCircle(Canvas canvas, Player player) {
        float centerX = player.getX();
        float centerY = player.getY();
        float radius = 50; // Adjust the radius as needed

        // Calculate rotation angle based on elapsed time
        long elapsedTime = SystemClock.uptimeMillis() - player.getStartTime();
        float rotation = (float) (elapsedTime % 1000) / 1000 * 360;

        canvas.save();
        canvas.rotate(rotation, centerX, centerY);
        canvas.drawCircle(centerX, centerY, radius, circlePaint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                selectPlayer(x, y);
                break;
        }

        return true;
    }

    private void selectPlayer(float x, float y) {
        for (Player player : players) {
            if (isTouchInsideCircle(x, y, player.getX(), player.getY(), 30)) {
                selectedPlayer = player;
                invalidate();
                break;
            }
        }
    }

    private boolean isTouchInsideCircle(float touchX, float touchY, float circleX, float circleY, float radius) {
        double distance = Math.sqrt(Math.pow(touchX - circleX, 2) + Math.pow(touchY - circleY, 2));
        return distance <= radius;
    }

    private static class Player {
        private float x;
        private float y;
        private long startTime;

        public Player(float x, float y) {
            this.x = x;
            this.y = y;
            this.startTime = SystemClock.uptimeMillis();
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public long getStartTime() {
            return startTime;
        }
    }
}