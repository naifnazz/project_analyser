package com.example.sportanalyzer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class FormationLine {

    static  HologramDraw hologramDraw;
     static  FormationArea formationArea;
     static FormationScan formationScan;
     static FormationSpace formationSpace;
     static FormationMarker formationMarker;
     static FormateText formateText;
     static FormationArrow formationArrow;


    public void setHologramDraw(HologramDraw hologramDraw) {
        this.hologramDraw = hologramDraw;
    }

    public void setFormationArea(FormationArea formationArea) {
        this.formationArea = formationArea;
    }

    public  void setFormationScan(FormationScan formationScan) {
        this.formationScan = formationScan;
    }
    public  void setFormationMarker(FormationMarker formationMarker) {
        this.formationMarker = formationMarker;
    }

    public  void setFormationSpace(FormationSpace formationSpace) {
        this.formationSpace = formationSpace;
    }


    public  void setFormateText(FormateText formateText) {
        this.formateText = formateText;
    }

    public  void setFormationArrow(FormationArrow formationArrow) {
        this.formationArrow = formationArrow;
    }


    public   ArrayList<Point> points = new ArrayList<>();
   static Paint linePaint;

    public FormationLine() {

        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(8); // Adjust the stroke width as needed
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true); // Enable anti-aliasing for smoother lines
        linePaint.setStrokeCap(Paint.Cap.SQUARE); // Set the line cap style (ROUND, SQUARE, or BUTT)
        linePaint.setStrokeJoin(Paint.Join.MITER); // Set the line join style (ROUND, BEVEL, or MITER)
        linePaint.setShadowLayer(2f, 0f, 0f, Color.BLACK); // Apply a shadow to the line (adjust parameters as needed)
        linePaint.setDither(true); // Enable or disable dithering
        linePaint.setStrokeMiter(3f); // Set the miter limit for sharp angles
        linePaint.setFilterBitmap(true); // Enable or disable bitmap filtering
        linePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.OVERLAY));




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
                boundary = new RectF(x, y, x + bitmap.getWidth()-50 , y + bitmap.getHeight()-50 );
            } else {
                boundary = new RectF();
            }
        }
    }

    public  void drawIfNotOverlaying(Canvas canvas, int x, int y, Bitmap bitmap) {
        // Create a new Hologram instance with the given coordinates and bitmap
        Point point = new Point(x, y, bitmap);

        if(hologramDraw!=null){
            HologramDraw.drawHologramStatic(hologramDraw.arrayList,canvas);
        }  if (formationMarker!=null) {
            FormationMarker.drawMarkerStatic(formationMarker.arrayList,canvas);
        }if (formationScan!=null) {
            FormationScan.drawScanStatic(formationScan.arrayList,canvas);
        }if (formationSpace!=null){
            FormationSpace.drawFormationSpaceStatic(formationSpace.points,canvas);
        }if (formationArea!=null){
            FormationArea.drawFormationAreaStatic(formationArea.points,canvas);
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
        if (points.size() > 1) {
            linePaint.setShader(new LinearGradient(0, 0, 0, canvas.getHeight(), Color.WHITE, Color.TRANSPARENT, Shader.TileMode.MIRROR));


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

    }

    private  Point isOverlaying(Point newPoint) {
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
    public static void drawFormationLineStatic(ArrayList<Point> points,Canvas canvas){
        if(points.size()>0)
            drawBitmap(canvas,points.get(0));


        for (int i = 1; i < points.size(); i++) {
            Point prev = points.get(i - 1);
            Point current = points.get(i);
            float startX = prev.x;
            float startY = prev.y;
            float stopX = current.x;
            float stopY = current.y;

            canvas.drawLine(startX, startY, stopX, stopY, linePaint);
        }

        for(int i=1;i<points.size();i++){
            Point point=points.get(i);
            if (point.bitmap != null) {
                float left = point.x - point.bitmap.getWidth() / 2f;
                float top = point.y - point.bitmap.getHeight() / 2f;
                canvas.drawBitmap(point.bitmap, left, top, new Paint());
            }
        }
    }


}

