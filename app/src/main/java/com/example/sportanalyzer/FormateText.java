package com.example.sportanalyzer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;

public class FormateText {
    ArrayList<Text> textArrayList = new ArrayList<>();
    private Paint textPaint;
    private Paint backgroundPaint;

    public FormateText() {
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(25f); // Adjust text size as needed

        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.GRAY);
    }

    public class Text {
        String text;
        public RectF boundary;
        int x, y;

        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void setText(String txt) {
            text = txt;


        }
    }

    public void drawText(Canvas canvas, String text, int x, int y) {
        boolean isrep = false;

        for (Text text1 : textArrayList) {
            if (text1.boundary.contains(x, y)) {
                textArrayList.remove(text1);
                isrep = true;
                break;
            }

        }


        for (Text text1 : textArrayList) {

            drawTextInit(text1.text, text1.x, text1.y, canvas);
        }

        if (isrep) return;
        Text text1 = new Text();
        text1.setText(text);
        text1.setPosition(x, y);


        float textWidth = textPaint.measureText(text);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float textHeight = fontMetrics.descent - fontMetrics.ascent;

        float rectLeft = x; // Use the specified x-coordinate
        float rectTop = y + fontMetrics.ascent; // Adjust the y-coordinate based on font metrics
        float rectRight = rectLeft + textWidth;
        float rectBottom = rectTop + textHeight;

        text1.boundary = new RectF(rectLeft, rectTop, rectRight, rectBottom);

        textArrayList.add(text1);


        // Draw background rectangle
        // Set the background color


        drawTextInit(text, x, y, canvas);

    }


    private void drawTextInit(String text, int x, int y, Canvas canvas) {

        float textWidth = textPaint.measureText(text);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float textHeight = fontMetrics.descent - fontMetrics.ascent;

        // Set the background rectangle coordinates to cover the entire text
        canvas.drawCircle(x - 10, y - textHeight / 2, 10, textPaint);
        float rectLeft = x; // Use the specified x-coordinate
        float rectTop = y + fontMetrics.ascent; // Adjust the y-coordinate based on font metrics
        float rectRight = rectLeft + textWidth;
        float rectBottom = rectTop + textHeight;

        // Draw background rectangle
        backgroundPaint.setColor(Color.BLACK); // Set the background color
        canvas.drawRect(rectLeft, rectTop, rectRight, rectBottom, backgroundPaint);


        // Draw text at specified coordinates (x, y)
        textPaint.setColor(Color.WHITE); // Set the text color
        canvas.drawText(text, x, y, textPaint);

    }

}
