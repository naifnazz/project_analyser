package com.example.sportanalyzer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class FormationLine {
    ArrayList<Point> points = new ArrayList<>();
    Paint linePaint;

    public FormationLine() {

        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(10); // Adjust the stroke width as needed
        linePaint.setStyle(Paint.Style.STROKE);
    }

    public class Point {
        int x, y;
        public RectF boundary;
        public Bitmap bitmap;

        public Point(int x, int y, Bitmap bitmap) {
            this.x = x;
            this.y = y;
            this.bitmap = bitmap;

            if (bitmap != null) {
                boundary = new RectF(x, y, x + bitmap.getWidth() , y + bitmap.getHeight() );
            } else {
                boundary = new RectF();
            }
        }
    }

    public void drawIfNotOverlaying(Canvas canvas, int x, int y, Bitmap bitmap) {
        // Create a new Hologram instance with the given coordinates and bitmap
        Point point = new Point(x, y, bitmap);


        Point deletePoint = null;
        if ((deletePoint = isOverlaying(point)) != null) {
            Log.i("test", "clicked the same");
            points.remove(deletePoint);
        } else {
            drawBitmap(canvas, point);
            // Add the new hologram to the list
            points.add(point);

        }
        if (points.size() > 1) {


            for (int i = 1; i < points.size(); i++) {
                Point prev = points.get(i - 1);
                Point current = points.get(i);
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

    private void drawBitmap(Canvas canvas, Point point) {
        // Draw the bitmap to the canvas at the specified coordinates


        if (point.bitmap != null) {
            float left = point.x - point.bitmap.getWidth() / 2f;
            float top = point.y - point.bitmap.getHeight() / 2f;

            canvas.drawBitmap(point.bitmap, left, top, new Paint());
        }
    }
}

