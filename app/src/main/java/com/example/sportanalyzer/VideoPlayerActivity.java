package com.example.sportanalyzer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;


public class VideoPlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private MediaPlayer mediaPlayer;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaExtractor mediaExtractor;
    private MediaCodec mediaCodec;
    private SeekBar seekBar;
    private Handler handler;
    private boolean pause = false;
    private Button pauseButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videopleyer);
        SurfaceView surfaceView = findViewById(R.id.videoview);
        surfaceView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        surfaceView.setLayerType(SurfaceView.LAYER_TYPE_HARDWARE, null);
        surfaceView = findViewById(R.id.videoview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        mediaPlayer = new MediaPlayer();
        seekBar = findViewById(R.id.Seekbr);
        Uri videoUri = getIntent().getData();
        try {
            mediaPlayer.setDataSource(this,videoUri);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
    }

    @Override
    public void surfaceCreated (SurfaceHolder holder){
        mediaPlayer.setDisplay(holder);

        mediaPlayer.start();


    }

    @Override
    public void surfaceChanged (SurfaceHolder holder,int format, int width, int height){
        // Handle surface changes if needed
    }

    @Override
    public void surfaceDestroyed (SurfaceHolder holder){
        // Release MediaPlayer when the surface is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
    @Override
    protected void onDestroy () {
        super.onDestroy();
        // Release MediaPlayer when the activity is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    private void initializeMediaCodec () {
        try {
            mediaExtractor = new MediaExtractor();
            mediaExtractor.setDataSource("Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI");

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

        long fullduration=mediaPlayer.getDuration();
        int cal=(int)(fullduration* (position/100.0));

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

    // Toggle between pause and resume
    private void togglePauseResume() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Toast.makeText(this, "Paused", Toast.LENGTH_SHORT).show();

            // Stop updating the seek bar
            handler.removeCallbacks(updateSeekBar);
        } else {
            mediaPlayer.start();
            Toast.makeText(this, "Resumed", Toast.LENGTH_SHORT).show();

            // Start updating the seek bar again
            handler.postDelayed(updateSeekBar, 1000);
        }
    }

    // Example: Pause on button click
    public void onPauseButtonClick(View view) {
        togglePauseResume();
    }
}