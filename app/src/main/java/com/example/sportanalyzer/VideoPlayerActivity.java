package com.example.sportanalyzer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.view.MotionEvent;

import java.io.IOException;


public class VideoPlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private MediaPlayer mediaPlayer;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaExtractor mediaExtractor;
    private MediaCodec mediaCodec;
    private SeekBar seekBar;
    private Handler handler;
    SpotlightHologram spotlightHologram;


    private boolean pause = false;
    LinearLayout spotlight_btn;

    @SuppressLint({"MissingInflatedId","ClickableViewAccessibility"})

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videopleyer);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        surfaceView = findViewById(R.id.view);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        mediaPlayer = new MediaPlayer();
        seekBar = findViewById(R.id.Seekbr);
        surfaceView.setLayerType(SurfaceView.LAYER_TYPE_HARDWARE, null);
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.spotlight_icon_btn);;

        spotlightHologram = new SpotlightHologram(this,bitmap);
        spotlightHologram.setHologramXY(300,300);
        spotlightHologram.setBitmap(bitmap);

        addContentView(spotlightHologram, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        Uri videoUri = getIntent().getData();
        try {
            mediaPlayer.setDataSource(this, videoUri);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        surfaceView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float xpoint=   event.getX();
                float ypoint =   event.getY();
                Toast.makeText(VideoPlayerActivity.this, xpoint+" "+ypoint, Toast.LENGTH_SHORT).show();
                spotlightHologram.setHologramXY(xpoint,ypoint);
                return false;
            }
        });


        seekBar.setMax(100);
        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                seekTo(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed for this example
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not needed for this example
            }
        });

        handler = new Handler(Looper.getMainLooper());

        spotlight_btn = findViewById(R.id.spotlight);

        spotlight_btn.setOnClickListener(view -> {

        });


    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mediaPlayer.setDisplay(holder);

        mediaPlayer.start();


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Handle surface changes if needed
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Release MediaPlayer when the surface is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release MediaPlayer when the activity is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }


    private void initializeMediaCodec() {
        try {
            mediaExtractor = new MediaExtractor();
            mediaExtractor.setDataSource("MediaStore.Video.Media.EXTERNAL_CONTENT_URI");

            MediaFormat format = mediaExtractor.getTrackFormat(0);
            String mime = format.getString(MediaFormat.KEY_MIME);
            mediaCodec = MediaCodec.createDecoderByType(mime);
            Surface surface = null;
            mediaCodec.configure(format, surface, null, 0);
            mediaCodec.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void seekTo(int position) {

        long fullduration = mediaPlayer.getDuration();
        int cal = (int) (fullduration * (position / 100.0));

        runOnUiThread(() -> mediaPlayer.seekTo(cal));

    }

    private Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            int currentPosition = (int) mediaExtractor.getSampleTime() / 1000; // Convert to milliseconds
            seekBar.setProgress(currentPosition);
            handler.postDelayed(this, 1000);
        }
    };


    private void initializeMediaExtractor() {
        mediaExtractor = new MediaExtractor();
        Uri videoUri = getIntent().getData();

        try {
            mediaExtractor.setDataSource(this, videoUri, null);
            int numTracks = mediaExtractor.getTrackCount();

            for (int i = 0; i < numTracks; i++) {
                MediaFormat mediaFormat = mediaExtractor.getTrackFormat(i);
                // Process the MediaFormat as needed
                // ...
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void releaseMediaCodec() {
        if (mediaCodec != null) {
            mediaCodec.stop();
            mediaCodec.release();
            mediaCodec = null;
        }
        if (mediaExtractor != null) {
            mediaExtractor.release();
            mediaExtractor = null;
        }

    }

    ;

    void togglePauseResume() {
        try {


            if (!isFinishing() && mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    // Stop updating the seek bar
                    //   handler.removeCallbacks(updateSeekBar);
                } else {
                    mediaPlayer.start();
                    // Start updating the seek bar again
                    //  handler.postDelayed(updateSeekBar, 1000);
                }
            } else {
                // Handle the case where mediaPlayer is null
                Toast.makeText(this, "Media player not initialized", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
        }
    }

    // Example: Pause on button click
    public void onPauseButtonClick(View view) {
        togglePauseResume();
    }
}
