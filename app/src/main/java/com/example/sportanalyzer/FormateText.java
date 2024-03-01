package com.example.sportanalyzer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;

public class FormateText {
    ArrayList<Text> textArrayList = new ArrayList<>();
    static HologramDraw hologramDraw;
    static  FormationArea formationArea;
    static FormationScan formationScan;
    static FormationSpace formationSpace;
    static FormationMarker formationMarker;
    static FormationLine formationLine;
    static FormationArrow formationArrow;
    public  void setHologramDraw(HologramDraw hologramDraw) {
        this.hologramDraw = hologramDraw;
    }
    public void setFormationArea(FormationArea formationArea) {
        this.formationArea = formationArea;
    }
    public void setFormationArrow(FormationArrow formationArrow) {
        this.formationArrow = formationArrow;
    }
    public  void setFormationScan(FormationScan formationScan) {
        this.formationScan = formationScan;
    }
    public  void setFormationMarker(FormationMarker formationMarker) {
        this.formationMarker = formationMarker;
    }

    public  void setFormationSpace(FormationSpace formationSpace) {
        this.formationSpace = formationSpace;
    }public void setFormationLine(FormationLine formationLine) {
        this.formationLine = formationLine;
    }
    private Paint textPaint;
    private Paint backgroundPaint;

    public FormateText() {
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(25f); // Adjust text size as needed

        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.GRAY);
    }

    public static class Text {
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
        if (hologramDraw!=null){
            HologramDraw.drawHologramStatic(hologramDraw.arrayList,canvas);
        }if(formationLine!=null){
            FormationLine.drawFormationLineStatic(formationLine.points,canvas);
        } if (formationMarker!=null) {
            FormationMarker.drawMarkerStatic(formationMarker.arrayList,canvas);
        }if (formationScan!=null) {
            FormationScan.drawScanStatic(formationScan.arrayList,canvas);
        }if (formationArea!=null){
            FormationArea.drawFormationAreaStatic(formationArea.points,canvas);
        }if (formationSpace!=null){
            FormationSpace.drawFormationSpaceStatic(formationSpace.points,canvas);
        }if(formationArrow!=null){
            FormationArrow.drawArrowStatic(formationArrow.ArrowList,canvas);
        }
        boolean isrep = false;

        for (Text text1 : textArrayList) {
            if (text1.boundary.contains(x, y)) {
                textArrayList.remove(text1);
                isrep = true;
                break;
            }
        }

        for (Text text1 : textArrayList) {
            drawTextInitStatic(text1.text, text1.x, text1.y, canvas);
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
        drawTextInitStatic(text, x, y, canvas);
    }

    public static void drawTextStatic(ArrayList<Text> textArrayList, Canvas canvas) {
        for (Text text : textArrayList) {
            drawTextInitStatic(text.text, text.x, text.y, canvas);
        }
    }

    private static void drawTextInitStatic(String text, int x, int y, Canvas canvas) {
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(25f); // Adjust text size as needed

        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.BLACK); // Set the background color

        float textWidth = textPaint.measureText(text);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float textHeight = fontMetrics.descent - fontMetrics.ascent;

        // Draw background rectangle
        canvas.drawRect(x, y + fontMetrics.ascent, x + textWidth, y + fontMetrics.descent, backgroundPaint);

        // Draw text at specified coordinates (x, y)
        textPaint.setColor(Color.WHITE); // Set the text color
        canvas.drawText(text, x, y, textPaint);
    }
}
