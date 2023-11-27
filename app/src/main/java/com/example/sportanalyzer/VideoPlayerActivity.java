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
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.nio.ByteBuffer;


public class VideoPlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private MediaPlayer mediaPlayer;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaExtractor mediaExtractor;
    private MediaCodec mediaCodec;
    private SeekBar seekBar;
    private Handler handler;
    private boolean pause = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videopleyer);

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
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    seekTo(progress);
                }
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

        private void extractAndDisplayFrames () {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();

                    while (true) {
                        if (!pause) {
                            int inputBufferIndex = mediaCodec.dequeueInputBuffer(10000);
                            if (inputBufferIndex >= 0) {
                                ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                                int sampleSize = mediaExtractor.readSampleData(inputBuffer, 0);

                                if (sampleSize < 0) {
                                    mediaCodec.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                                    break;
                                } else {
                                    mediaCodec.queueInputBuffer(inputBufferIndex, 0, sampleSize, mediaExtractor.getSampleTime(), 0);
                                    mediaExtractor.advance();
                                }
                            }
                            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 10000);

                            if (outputBufferIndex >= 0) {
                                mediaCodec.releaseOutputBuffer(outputBufferIndex, true);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int currentPosition = (int) mediaExtractor.getSampleTime() / 1000;
                                        seekBar.setProgress(currentPosition);
                                    }
                                });
                            }

                            if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                                break;
                            }
                        }
                    }
                }
            }).start();
        }
    private void setupTimeline() {
        int duration = (int) mediaExtractor.getSampleTime() / 1000; // Convert to milliseconds
        seekBar.setMax(duration);
        handler.postDelayed(updateSeekBar, 1000);
    }

    private void seekTo(int position) {
        long seekPosition = position * 1000L; // Convert to microseconds
        if (mediaExtractor != null) {
            // Use logging to debug and trace the flow
            Log.d("VideoActivity", "Before seekTo");
            mediaExtractor.seekTo(seekPosition, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
            Log.d("VideoActivity", "After seekTo");
        }
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
        pause = !pause;
        if (pause) {
            Toast.makeText(this, "Paused", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Resumed", Toast.LENGTH_SHORT).show();
        }
    }

    // Example: Pause on button click
    public void onPauseButtonClick(View view) {
        togglePauseResume();
    }
}






