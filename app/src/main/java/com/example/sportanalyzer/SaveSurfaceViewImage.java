package com.example.sportanalyzer;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

// ... (existing imports)

public class SaveSurfaceViewImage {

    public static void saveCanvasAsJpeg(Context context, Bitmap bitmap, String fileName) {
        // Check if external storage is available
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Get the directory for the app's private pictures directory
            File picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

            // Create a file in the pictures directory with the given fileName
            File file = new File(picturesDirectory, fileName + ".jpg");

            // Using ContentValues to insert the image into the MediaStore
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

            try {
                Uri contentUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
                Uri imageUri = context.getContentResolver().insert(contentUri, values);

                if (imageUri != null) {
                    try (OutputStream outputStream = context.getContentResolver().openOutputStream(imageUri)) {
                        // Save the Bitmap as a JPEG file
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        Toast.makeText(context, "Image saved", Toast.LENGTH_SHORT).show();
                        Log.i("SaveSurfaceViewImage", "Image saved");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error saving image", Toast.LENGTH_SHORT).show();
                        Log.e("SaveSurfaceViewImage", "Error saving image: " + e.getMessage());
                    }
                } else {
                    Toast.makeText(context, "Error creating image entry", Toast.LENGTH_SHORT).show();
                    Log.e("SaveSurfaceViewImage", "Error creating image entry");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Error inserting image into MediaStore", Toast.LENGTH_SHORT).show();
                Log.e("SaveSurfaceViewImage", "Error inserting image into MediaStore: " + e.getMessage());
            }
        } else {
            // Handle the case where external storage is not available
            // This may include using internal storage or notifying the user
            Toast.makeText(context, "External storage not available", Toast.LENGTH_SHORT).show();
            Log.e("SaveSurfaceViewImage", "External storage not available");
        }
    }
}
