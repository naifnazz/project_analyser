package com.example.sportanalyzer;

import static com.example.sportanalyzer.VideoPlayerActivity.DRAW_OPTION_CURVED;
import static com.example.sportanalyzer.VideoPlayerActivity.DRAW_OPTION_DASH;
import static com.example.sportanalyzer.VideoPlayerActivity.DRAW_OPTION_DEFAULT_ARROW;
import static com.example.sportanalyzer.VideoPlayerActivity.DRAW_OPTION_ZIGZAG;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;

public class FormationArrow {
    private Paint linePaint;
    private int ARROW_TYPE;
    private ArrayList<Arrow> arrayList;

    public FormationArrow() {
        arrayList = new ArrayList<>();
        ARROW_TYPE = DRAW_OPTION_DEFAULT_ARROW;

        linePaint = new Paint();
        linePaint.setColor(Color.RED); // Set arrow color to red
        linePaint.setStrokeWidth(15); // Adjust the stroke width as needed
        linePaint.setStyle(Paint.Style.STROKE);
    }

    public class Arrow {
        int arrowType;
        int startX, startY, endX, endY;

        public Arrow(int arrowType) {
            this.arrowType = arrowType;
        }

        public void setStartingPoint(int x, int y) {
            startX = x;
            startY = y;
        }

        public void setEndingPoint(int x, int y) {
            endX = x;
            endY = y;
        }
    }

    public void setARROW_TYPE(int ARROW_TYPE) {
        this.ARROW_TYPE = ARROW_TYPE;
    }

    Arrow arrow;


    public void drawArrow(Canvas canvas, int x, int y) {

        for (Arrow arrow1 : arrayList) {
            int type = arrow1.arrowType;
            if (DRAW_OPTION_DEFAULT_ARROW == type) {
                drawSolidLine(canvas, arrow1.startX, arrow1.startY, arrow1.endX, arrow1.endY);
                // Draw arrowhead
            } else if (DRAW_OPTION_DASH == type) {
                drawDashedLine(canvas, arrow1.startX, arrow1.startY, arrow1.endX, arrow1.endY);
                // Draw arrowhead
            } else if (DRAW_OPTION_CURVED == type) {
                // Draw parabolic curve
                drawParabolicCurve(canvas, arrow1.startX, arrow1.startY, arrow1.endX, arrow1.endY);
                // Draw arrowhead
            } else if (DRAW_OPTION_ZIGZAG == type) {
                drawCurvedZigzagLine(canvas, arrow1.startX, arrow1.startY, arrow1.endX, arrow1.endY, 30);
            }
        }

        if (x == -1 && y == -1) return;

        if (arrow == null) {
            arrow = new Arrow(ARROW_TYPE);
            arrow.setStartingPoint(x, y);
        } else {
            arrow.setEndingPoint(x, y);

            if (arrow.startX != -1 && arrow.startY != -1 && arrow.endX != -1 && arrow.endY != -1) {
                if (ARROW_TYPE == DRAW_OPTION_DEFAULT_ARROW) {
                    drawSolidLine(canvas, arrow.startX, arrow.startY, arrow.endX, arrow.endY);
                    // Draw arrowhead
                } else if (ARROW_TYPE == DRAW_OPTION_CURVED) {
                    // Draw parabolic curve
                    drawParabolicCurve(canvas, arrow.startX, arrow.startY, arrow.endX, arrow.endY);
                    // Draw arrowhead
                } else if (ARROW_TYPE == DRAW_OPTION_DASH) {
                    // Draw dashed line
                    drawDashedLine(canvas, arrow.startX, arrow.startY, arrow.endX, arrow.endY);
                    // Draw arrowhead
                } else if (ARROW_TYPE == DRAW_OPTION_ZIGZAG) {
                    drawCurvedZigzagLine(canvas, arrow.startX, arrow.startY, arrow.endX, arrow.endY, 30);
                }

                // Add arrow to the list
                Arrow newArrow = new Arrow(arrow.arrowType);
                newArrow.setStartingPoint(arrow.startX, arrow.startY);
                newArrow.setEndingPoint(arrow.endX, arrow.endY);
                arrayList.add(newArrow);
                arrow = null;
            }
        }
    }


    private void drawDashedLine(Canvas canvas, int startX, int startY, int endX, int endY) {
        linePaint.setPathEffect(new DashPathEffect(new float[]{20, 10}, 0));
        canvas.drawLine(startX, startY, endX, endY, linePaint);
        // Reset the path effect to remove dashed effect for subsequent drawings
        linePaint.setPathEffect(null);
    }
    private void drawSolidLine(Canvas canvas, int startX, int startY, int endX, int endY) {
        // Remove the DashPathEffect to draw a solid line
        linePaint.setPathEffect(null);
        canvas.drawLine(startX, startY, endX, endY, linePaint);

    }



    public void undo(){
        if(arrayList!=null){
            if(arrayList.size()>0){
                arrayList.remove(arrayList.size()-1);
            }
        }
    }
    private void drawCurvedZigzagLine(Canvas canvas, int startX, int startY, int endX, int endY, int zigzagSize) {
        Path path = new Path();
        path.moveTo(startX, startY);

        float dx = endX - startX;
        float dy = endY - startY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        float segments = distance / zigzagSize; // Adjust the zigzag size as needed
        dx /= distance;
        dy /= distance;

        for (int i = 0; i < segments; i++) {
            float x1 = startX + dx * i * zigzagSize;
            float y1 = startY + dy * i * zigzagSize;
            float x2 = x1 + 10 * dy;
            float y2 = y1 - 10 * dx;
            float x3 = x2 + dx * 10;
            float y3 = y2 + dy * 10;

            path.cubicTo(x1, y1, x2, y2, x3, y3);
        }
        canvas.drawPath(path, linePaint);
    }
    private void drawParabolicCurve(Canvas canvas, int startX, int startY, int endX, int endY) {
        // Drawing a simple parabolic curve as an example
        Path path = new Path();
        path.moveTo(startX, startY);
        float controlX = (startX + endX) / 2;
        float controlY = Math.min(startY, endY) - 200; // Adjust the control point as needed
        path.quadTo(controlX, controlY, endX, endY);
        canvas.drawPath(path, linePaint);

    }

}
