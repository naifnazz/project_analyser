package com.example.sportanalyzer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;

public class MergeFrames {
    public static void mergeFrames(ArrayList<Bitmap> sequenceArray,Canvas canvas) {
        ArrayList<Bitmap> sequencemergedBitmap = new ArrayList<>();

        for (Bitmap bitmap : sequenceArray) {
            // Create a new bitmap with the same dimensions as the original bitmap
            Bitmap mergedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

            // Create a new canvas with the merged bitmap
            Canvas mergedCanvas = new Canvas(mergedBitmap);

            // Draw the content from the original bitmap onto the merged canvas
            mergedCanvas.drawBitmap(bitmap, 0, 0, null);

            // Draw the content from the provided canvas onto the merged canvas
            canvas.drawBitmap(mergedBitmap, 0, 0, null);

            // Add the merged bitmap to the result array
            sequencemergedBitmap.add(mergedBitmap);

            Log.d("MergeFrames", "Merged bitmap added to sequence");
        }


    }

}
