package com.example.sportanalyzer;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.view.MotionEvent;

import java.io.IOException;


public class VideoPlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private MediaPlayer mediaPlayer;
    private SurfaceView videoSurfaceView;
    SurfaceHolder videoHolder,graphicsHolder;
    SurfaceView graphicsSurfaceView;
    LinearLayout topBarLayout,seekbarLayout,toolsLayout;

    private SeekBar seekBar;
    private Handler handler;
    ImageView playBtn,drawBtn,drawSaveBtn;



    private boolean pause = false;
    LinearLayout spotlight_btn;
  boolean isplaying=false;
  boolean isdrawingMode=false;
    @SuppressLint({"MissingInflatedId","ClickableViewAccessibility"})

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videopleyer);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
         videoSurfaceView = findViewById(R.id.videosurfaceview);
         graphicsSurfaceView=findViewById(R.id.graphicssurfaceview);
         playBtn=findViewById(R.id.playbtn);
         toolsLayout=findViewById(R.id.toolslayout);
         drawBtn=findViewById(R.id.drawframebtn);
         topBarLayout=findViewById(R.id.topbar);
         drawSaveBtn=findViewById(R.id.drawsave);
         seekbarLayout=findViewById(R.id.seekbarlayout);
        videoHolder= videoSurfaceView.getHolder();
        graphicsHolder= graphicsSurfaceView.getHolder();
        videoHolder.addCallback(this);
        graphicsHolder.addCallback(this);

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isplaying){
                    if(mediaPlayer!=null){
                        mediaPlayer.start();
                        playBtn.setImageResource(R.drawable.pause_circle);
                        isplaying=true;
                    }

                }else{
                    if(mediaPlayer!=null){
                        mediaPlayer.pause();
                        playBtn.setImageResource(R.drawable.play_circle);
                        isplaying=false;
                    }
                }
            }
        });


        drawBtn.setOnClickListener(v -> {
            if(!isdrawingMode){
                if(mediaPlayer!=null){

                    currentFrameMilli  =mediaPlayer.getCurrentPosition();
                    mediaPlayer.pause();

                    graphicsSurfaceView.setVisibility(VISIBLE);
                    seekbarLayout.setVisibility(GONE);
                    topBarLayout.setVisibility(GONE);
                    playBtn.setVisibility(GONE);
                    drawBtn.setVisibility(GONE);
                    toolsLayout.setBackground(new ColorDrawable(Color.TRANSPARENT));
                    drawSaveBtn.setVisibility(VISIBLE);

                }

            }
        });

        drawSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graphicsSurfaceView.setVisibility(GONE);
              //  videoSurfaceView.setVisibility(VISIBLE);
                mediaPlayer.release();
                mediaPlayer=null;
                mediaPlayer=new MediaPlayer();
                Uri videoUri = getIntent().getData();
                try {
                    mediaPlayer.setDataSource(getApplicationContext(), videoUri);
                    mediaPlayer.setDisplay(videoSurfaceView.getHolder());
                    mediaPlayer.prepare();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                seekbarLayout.setVisibility(VISIBLE);
                topBarLayout.setVisibility(VISIBLE);
                playBtn.setVisibility(VISIBLE);
                drawBtn.setVisibility(VISIBLE);
                toolsLayout.setBackground(getDrawable(R.drawable.border));
                drawSaveBtn.setVisibility(GONE);
            }
        });

        mediaPlayer = new MediaPlayer();
        seekBar = findViewById(R.id.Seekbr);





        videoSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return true;
            }
        });




        Uri videoUri = getIntent().getData();
        try {
            mediaPlayer.setDataSource(this, videoUri);
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
    long currentFrameMilli;

    private Bitmap extractFrame(String videoPath, long timeInMillis) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            retriever.setDataSource(videoPath);

            // Get the frame at the specified time
            Bitmap frameBitmap = retriever.getFrameAtTime(timeInMillis * 1000, MediaMetadataRetriever.OPTION_CLOSEST);
            retriever.release();
            return frameBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    boolean issurfaceInit=false;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {




        if(holder.getSurface()==videoSurfaceView.getHolder().getSurface()){
           message("video surfaceview");
           if(!issurfaceInit){
               mediaPlayer.setDisplay(holder);
               issurfaceInit=true;
           }


        }else{
            message("graphics surfaceview");

        }


    }

    private  void message(String mes){
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(VideoPlayerActivity.this, mes, Toast.LENGTH_SHORT).show());
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





    private void seekTo(int position) {

        long fullduration = mediaPlayer.getDuration();
        int cal = (int) (fullduration * (position / 100.0));

        runOnUiThread(() -> mediaPlayer.seekTo(cal));

    }








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
