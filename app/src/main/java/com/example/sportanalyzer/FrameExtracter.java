package com.example.sportanalyzer;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.File;

public class FrameExtracter {
    public static Bitmap extractFrame(Context context, Uri videoUri, long timeInMillis) {

        // Get the file path from the Uri
        String videoPath = getVideoPathFromUri(context, videoUri);
        Log.i("test",videoPath);

        if (videoPath != null) {
            File file=new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                    "test.mp4"
            );
             videoPath = file.getAbsolutePath();
             Log.i("test","file exists "+file.exists());
            // Build the FFmpeg command
            String outputFilePath = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                    "frame.jpg"
            ).getAbsolutePath();

            String command = "-ss " + (timeInMillis / 1000.0) + " -i " + videoPath + " -vframes 1 -q:v 2 " + outputFilePath;

            // Run the FFmpeg command
            int rc = FFmpeg.execute(command);

            if (rc == RETURN_CODE_SUCCESS) {
                // Load the extracted frame into the ImageView
                return BitmapFactory.decodeFile(outputFilePath);
            } else {
                // Handle FFmpeg execution failure
                System.out.println("FFmpeg execution failed with rc=" + rc);
            }
        }

        return null;
    }

    private static String getVideoPathFromUri(Context context, Uri videoUri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = context.getContentResolver().query(videoUri, projection, null, null, null);

        if (cursor != null) {
            try {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } finally {
                cursor.close();
            }
        }

        return null;
    }
}
