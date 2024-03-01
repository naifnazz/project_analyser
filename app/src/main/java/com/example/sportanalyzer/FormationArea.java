package com.example.sportanalyzer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class FormationArea {
    public ArrayList<Point> points = new ArrayList<>();
    static HologramDraw hologramDraw;
    static FormationLine formationLine;
    static FormationSpace formationSpace;
    static FormationMarker formationMarker;
    static FormateText formateText;
    static FormationArrow formationArrow;
    static FormationScan formationScan;
    public ArrayList<Point> arrayList;


    public void setFormationLine(FormationLine formationLine) {
        this.formationLine = formationLine;
    }

    public void setFormationScan(FormationScan formationScan) {
        this.formationScan = formationScan;
    }

    public void setHologramDraw(HologramDraw hologramDraw) {

        this.hologramDraw = hologramDraw;
    }
    public void setFormationMarker(FormationMarker formationMarker) {
        this.formationMarker = formationMarker;
    }


    public void setFormationSpace(FormationSpace formationSpace) {
        this.formationSpace = formationSpace;
    }



    public void setFormateText(FormateText formateText) {

        this.formateText = formateText;
    }

    public void setFormationArrow(FormationArrow formationArrow) {
        this.formationArrow = formationArrow;
    }


    private static Paint linePaint;
    private static Paint fillPaint;

    public FormationArea() {

        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(8); // Adjust the stroke width as needed
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));
        linePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.OVERLAY));

        fillPaint = new Paint();
        fillPaint.setColor(Color.parseColor("#852FC18E"));
        fillPaint.setStyle(Paint.Style.FILL);
        PorterDuff.Mode mode = PorterDuff.Mode.OVERLAY; // Choose a blending mode
        fillPaint.setXfermode(new PorterDuffXfermode(mode));
        PathEffect effect = new DashPathEffect(new float[]{10, 5}, 0); // Example: Dash effect
        fillPaint.setPathEffect(effect);


    }

    public static class Point {
        int x, y;
        public RectF boundary;
        public Bitmap bitmap;

        public Point(int x, int y, Bitmap bitmap) {
            this.x = x;
            this.y = y;
            this.bitmap = bitmap;

            if (bitmap != null) {
                boundary = new RectF(x, y, x + bitmap.getWidth() - 50, y + bitmap.getHeight() - 50);
            } else {
                boundary = new RectF();
            }
        }
    }

    public void drawIfNotOverlaying(Canvas canvas, int x, int y, Bitmap bitmap) {
        // Create a new Hologram instance with the given coordinates and bitmap
        Point point = new Point(x, y, bitmap);
         if (hologramDraw!=null){
            HologramDraw.drawHologramStatic(hologramDraw.arrayList,canvas);
        } if (formationMarker!=null){
            FormationMarker.drawMarkerStatic(formationMarker.arrayList,canvas);
        }if (formationScan!=null) {
            FormationScan.drawScanStatic(formationScan.arrayList,canvas);
        }if (formationSpace!=null){
            FormationSpace.drawFormationSpaceStatic(formationSpace.points,canvas);
        }if(formationLine!=null){
            FormationLine.drawFormationLineStatic(formationLine.points,canvas);
        }if(formateText!=null){
            FormateText.drawTextStatic(formateText.textArrayList,canvas);
        }if(formationArrow!=null){
            FormationArrow.drawArrowStatic(formationArrow.ArrowList,canvas);
        }



        Point deletePoint = null;
        if ((deletePoint = isOverlaying(point)) != null) {
            Log.i("test", "clicked the same");
            points.remove(deletePoint);
        } else {
            drawBitmap(canvas, point);
            // Add the new hologram to the list
            points.add(point);

        }
        if (points.size() == 0) return;
        Path path = new Path();
        path.moveTo(points.get(0).x, points.get(0).y);

        if (points.size() > 1) {
            for (int i = 1; i < points.size(); i++) {
                Point prev = points.get(i - 1);
                Point current = points.get(i);
                float startX = prev.x;
                float startY = prev.y;
                float stopX = current.x;
                float stopY = current.y;

                canvas.drawLine(startX, startY, stopX, stopY, linePaint);
                path.lineTo(points.get(i).x, points.get(i).y);
            }
            path.close();

            // Draw the filled area
            if (points.size() > 1) {
                canvas.drawPath(path, fillPaint);
                Point prev = points.get(points.size() - 1);
                Point current = points.get(0);
                float startX = prev.x;
                float startY = prev.y;
                float stopX = current.x;
                float stopY = current.y;

                canvas.drawLine(startX, startY, stopX, stopY, linePaint);
            }


        }

        for (Point point1 : points) {
            drawBitmap(canvas, point1);
        }


        // Check if the new hologram overlaps with any existing hologram's boundary

        // If overlapping, do nothing or handle accordingly
    }

    private Point isOverlaying(Point newPoint) {
        // Check if the new hologram overlaps with any existing hologram's boundary
        for (Point existingPoint : points) {
            if (RectF.intersects(existingPoint.boundary, newPoint.boundary)) {
                return existingPoint; // Overlapping
            }
        }
        return null; // Not overlapping
    }

    private static void drawBitmap(Canvas canvas, Point point) {
        // Draw the bitmap to the canvas at the specified coordinates


        if (point.bitmap != null) {
            float left = point.x - point.bitmap.getWidth() / 2f;
            float top = point.y - point.bitmap.getHeight() / 2f;

            canvas.drawBitmap(point.bitmap, left, top, new Paint());
        }
    }
    public static void drawFormationAreaStatic(ArrayList<Point> points, Canvas canvas) {
        if (points == null || points.size() == 0) {
            return;
        }

        Path path = new Path();
        path.moveTo(points.get(0).x, points.get(0).y);

        for (int i = 1; i < points.size(); i++) {
            Point prev = points.get(i - 1);
            Point current = points.get(i);
            float startX = prev.x;
            float startY = prev.y;
            float stopX = current.x;
            float stopY = current.y;

            canvas.drawLine(startX, startY, stopX, stopY, linePaint);
            path.lineTo(points.get(i).x, points.get(i).y);
        }
        path.close();

        canvas.drawPath(path, fillPaint);

        if (points.size() > 1) {
            Point prev = points.get(points.size() - 1);
            Point current = points.get(0);
            float startX = prev.x;
            float startY = prev.y;
            float stopX = current.x;
            float stopY = current.y;

            canvas.drawLine(startX, startY, stopX, stopY, linePaint);
        }

        for (Point point : points) {
            drawBitmap(canvas, point);
        }
    }

}
