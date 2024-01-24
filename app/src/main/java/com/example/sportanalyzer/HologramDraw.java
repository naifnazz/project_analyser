package com.example.sportanalyzer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;

public class HologramDraw {
    private ArrayList<Hologram> arrayList = new ArrayList<>();

    public class Hologram {
        int x, y;
        public RectF boundary;
        public Bitmap bitmap;

        public Hologram(int x, int y,  Bitmap bitmap) {
            this.x = x;
            this.y = y;
            this.bitmap = bitmap;
            if (bitmap != null) {
                boundary = new RectF(x, y, x + bitmap.getWidth()-100, y + bitmap.getHeight()-200);
            } else {
                boundary = new RectF();
            }
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }
    }

    public void drawIfNotOverlaying(Canvas canvas, int x, int y, Bitmap bitmap) {
        // Create a new Hologram instance with the given coordinates and bitmap
        Hologram newHologram = new Hologram(x,y,bitmap);
        newHologram.x = x;
        newHologram.y = y;
        newHologram.setBitmap(bitmap);

        Hologram deleteHologram=null;
        if ((deleteHologram=isOverlaying(newHologram))!=null) {
            Log.i("test","clicked the same");
            arrayList.remove(deleteHologram);
        }else{
            drawBitmap(canvas, newHologram);
            // Add the new hologram to the list
            arrayList.add(newHologram);

        }
        for(Hologram hologram:arrayList){
            drawBitmap(canvas,hologram);
        }


        // Check if the new hologram overlaps with any existing hologram's boundary

        // If overlapping, do nothing or handle accordingly
    }

    private Hologram isOverlaying(Hologram newHologram) {
        // Check if the new hologram overlaps with any existing hologram's boundary
        for (Hologram existingHologram : arrayList) {
            if (RectF.intersects(existingHologram.boundary, newHologram.boundary)) {
                return existingHologram; // Overlapping
            }
        }
        return null; // Not overlapping
    }

    private void drawBitmap(Canvas canvas, Hologram hologram) {
        // Draw the bitmap to the canvas at the specified coordinates
        if (hologram.bitmap != null) {
            float left = hologram.x - hologram.bitmap.getWidth() / 2f;
            float top = hologram.y - hologram.bitmap.getHeight();
            canvas.drawBitmap(hologram.bitmap, left, top, new Paint());
        }
    }
}
