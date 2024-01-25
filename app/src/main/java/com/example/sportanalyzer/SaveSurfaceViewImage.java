package com.example.sportanalyzer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveSurfaceViewImage {

    public static void saveSurfaceViewAsImage(Context context, SurfaceView surfaceView) {
        // Create a Bitmap to hold the content of the SurfaceView
        Bitmap bitmap = Bitmap.createBitmap(surfaceView.getWidth(), surfaceView.getHeight(), Bitmap.Config.ARGB_8888);

        // Create a Canvas with the Bitmap
        Canvas canvas = new Canvas(bitmap);

        // Draw the content of the SurfaceView onto the Canvas
        surfaceView.draw(canvas);

        // Save the Bitmap as a JPEG file
        saveBitmapAsJPEG(context, bitmap);
    }

    public static void saveBitmapAsJPEG(Context context, Bitmap bitmap) {
        // Get the external storage directory
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

        // Create a unique filename for the JPEG image
        String fileName = "surface_view_image"+(int)(Math.random()*100000)+".jpg";

        // Create a File object for the image
        File imageFile = new File(directory, fileName);

        try {
            // Create a FileOutputStream for the image file
            FileOutputStream fos = new FileOutputStream(imageFile);

            // Compress the Bitmap as JPEG with quality 100 (maximum)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            // Close the FileOutputStream
            fos.close();

            // Notify the user that the image has been saved
            Toast.makeText(context, "Image saved successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error saving image", Toast.LENGTH_SHORT).show();
        }
    }
}
