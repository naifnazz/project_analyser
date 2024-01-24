package com.example.sportanalyzer;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
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
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.view.MotionEvent;

import com.bumptech.glide.Glide;

import java.io.IOException;


public class VideoPlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private MediaPlayer mediaPlayer;
    private SurfaceView videoSurfaceView;
    SurfaceHolder videoHolder, graphicsHolder;
    SurfaceView graphicsSurfaceView;
    LinearLayout topBarLayout, seekbarLayout, toolsLayout;

    private LinearLayout spotlightMain;
    private LinearLayout formationLine;
    private LinearLayout formationArea;
    private LinearLayout formationArrow;
    private LinearLayout formationText;
    HologramDraw hologramDraw;

    private SeekBar seekBar;
    private Handler handler;
    ImageView playBtn, drawBtn, drawSaveBtn;


    private boolean pause = false;
    LinearLayout spotlight_btn;
    boolean isplaying = false;
    boolean isdrawingMode = false;
    Uri videoUri;

    int DRAW_TOOL_OPTION = -1;
    int DRAW_HOLOGRAM = 2000;
    int DRAW_FORMATION_LINE = 2001;
    int DRAW_FORMATION_AREA = 2002;
    int DRAW_ARROW = 2007;
    int DRAW_TEXT = 2005;

    int DRAW_TOOL = -1;
    int DRAW_HOLOGRAM_DEFAULT = 1, DRAW_HOLOGRAM_YELLOW = 29, DRAW_HOLOGRAM_WHITE = 92, DRAW_HOLOGRAM_BLUE = 89;


    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videopleyer);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        videoSurfaceView = findViewById(R.id.videosurfaceview);
        graphicsSurfaceView = findViewById(R.id.graphicssurfaceview);
        playBtn = findViewById(R.id.playbtn);
        toolsLayout = findViewById(R.id.toolslayout);
        drawBtn = findViewById(R.id.drawframebtn);
        topBarLayout = findViewById(R.id.topbar);
        drawSaveBtn = findViewById(R.id.drawsave);
        seekbarLayout = findViewById(R.id.seekbarlayout);
        videoHolder = videoSurfaceView.getHolder();
        graphicsHolder = graphicsSurfaceView.getHolder();
        spotlightMain = findViewById(R.id.spotlight_main);

        // Initialize formation line views
        formationLine = findViewById(R.id.formationline);

        formationLine.setOnClickListener(v -> {

            if (!isdrawingMode)
                return;
            if (DRAW_TOOL == DRAW_FORMATION_LINE) {
                DRAW_TOOL = -1;
                formationLine.setBackgroundTintList(null);
            } else {
                DRAW_TOOL = DRAW_FORMATION_LINE;
                ColorStateList colorStateList = ContextCompat.getColorStateList(getApplicationContext(), R.color.selectrion_highlight);
                formationLine.setBackgroundTintList(colorStateList);
            }
        });


        // Initialize formation area views
        formationArea = findViewById(R.id.formation_area);
        formationArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isdrawingMode)
                    return;
                if (DRAW_TOOL == DRAW_FORMATION_AREA) {
                    DRAW_TOOL = -1;
                    formationArea.setBackgroundTintList(null);
                } else {
                    DRAW_TOOL = DRAW_FORMATION_AREA;
                    ColorStateList colorStateList = ContextCompat.getColorStateList(getApplicationContext(), R.color.selectrion_highlight);
                    formationArea.setBackgroundTintList(colorStateList);
                }
            }
        });

        // Initialize formation arrow views
        formationArrow = findViewById(R.id.formation_arrow);
        formationArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isdrawingMode)
                    return;
                if (DRAW_TOOL == DRAW_ARROW) {
                    DRAW_TOOL = -1;
                    formationArrow.setBackgroundTintList(null);
                } else {
                    DRAW_TOOL = DRAW_ARROW;
                    ColorStateList colorStateList = ContextCompat.getColorStateList(getApplicationContext(), R.color.selectrion_highlight);
                    formationArrow.setBackgroundTintList(colorStateList);
                }
            }
        });

        // Initialize formation text views
        formationText = findViewById(R.id.formation_text);
        formationText.setOnClickListener(v -> {
            if (!isdrawingMode)
                return;
            if (DRAW_TOOL == DRAW_TEXT) {
                DRAW_TOOL = -1;
                formationText.setBackgroundTintList(null);
            } else {
                DRAW_TOOL = DRAW_TEXT;
                ColorStateList colorStateList = ContextCompat.getColorStateList(getApplicationContext(), R.color.selectrion_highlight);
                formationText.setBackgroundTintList(colorStateList);
            }
        });

        spotlightMain.setOnClickListener(v -> {
            if (!isdrawingMode)
                return;

            if (DRAW_TOOL == DRAW_HOLOGRAM) {
                DRAW_TOOL = -1;
                spotlightMain.setBackgroundTintList(null);
            } else {
                DRAW_TOOL = DRAW_HOLOGRAM;
                DRAW_TOOL_OPTION = DRAW_HOLOGRAM_DEFAULT;
                ColorStateList colorStateList = ContextCompat.getColorStateList(this, R.color.selectrion_highlight);
                spotlightMain.setBackgroundTintList(colorStateList);
            }


        });

        spotlightMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Dialog dialog = new Dialog(VideoPlayerActivity.this);
                WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
                int[] location = new int[2];
                spotlightMain.getLocationInWindow(location);

                layoutParams.gravity = Gravity.TOP | Gravity.START;
                layoutParams.x = location[0] - spotlightMain.getWidth(); // Set x-coordinate
                layoutParams.y = location[1]; // Set y-coordinate

                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setAttributes(layoutParams);
                dialog.setContentView(R.layout.hologram_options);

                ImageView defaultHologram;
                ImageView yellowHologram;
                ImageView whiteHologram;
                ImageView blueHologram;

                defaultHologram = dialog.findViewById(R.id.default_hologram);
                yellowHologram = dialog.findViewById(R.id.yellow_hologram);
                whiteHologram = dialog.findViewById(R.id.white_hologram);
                blueHologram = dialog.findViewById(R.id.blue_hologram);

                // Set click listeners
                defaultHologram.setOnClickListener(v1 -> {
                    DRAW_TOOL = DRAW_HOLOGRAM;
                    DRAW_TOOL_OPTION = DRAW_HOLOGRAM_DEFAULT;
                    dialog.dismiss();
                    // Add your logic for default hologram click
                });

                yellowHologram.setOnClickListener(v12 -> {
                    DRAW_TOOL = DRAW_HOLOGRAM;
                    DRAW_TOOL_OPTION = DRAW_HOLOGRAM_YELLOW;
                    dialog.dismiss();
                    // Add your logic for yellow hologram click
                });

                whiteHologram.setOnClickListener(v13 -> {
                    DRAW_TOOL = DRAW_HOLOGRAM;
                    DRAW_TOOL_OPTION = DRAW_HOLOGRAM_WHITE;
                    dialog.dismiss();
                    // Add your logic for white hologram click
                });

                blueHologram.setOnClickListener(v14 -> {
                    DRAW_TOOL = DRAW_HOLOGRAM;
                    DRAW_TOOL_OPTION = DRAW_HOLOGRAM_BLUE;
                    dialog.dismiss();
                    // Add your logic for blue hologram click
                });

                dialog.show();

                return false;
            }
        });

        videoHolder.addCallback(this);
        graphicsHolder.addCallback(this);


        playBtn.setOnClickListener(v -> {
            if (!isplaying) {
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                    playBtn.setImageResource(R.drawable.pause_circle);
                    isplaying = true;
                }

            } else {
                if (mediaPlayer != null) {
                    currentFrameMilli = mediaPlayer.getCurrentPosition();
                    mediaPlayer.pause();
                    playBtn.setImageResource(R.drawable.play_circle);
                    isplaying = false;
                }
            }
        });


        drawBtn.setOnClickListener(v -> {
            if (!isdrawingMode) {
                if (mediaPlayer != null) {
                    Log.i("test", 10 + "  " + 11);
                    currentFrameMilli = mediaPlayer.getCurrentPosition();
                    mediaPlayer.pause();

                    graphicsSurfaceView.setVisibility(VISIBLE);
                    seekbarLayout.setVisibility(GONE);
                    topBarLayout.setVisibility(GONE);
                    playBtn.setVisibility(GONE);
                    drawBtn.setVisibility(GONE);
                    toolsLayout.setBackground(new ColorDrawable(Color.TRANSPARENT));
                    drawSaveBtn.setVisibility(VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Canvas canvas = graphicsHolder.lockCanvas();
                            if (canvas != null) {
                                Bitmap bitmap = extractFrame(videoUri, currentFrameMilli);




                                Rect srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                                RectF destRect = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());

                                canvas.drawBitmap(bitmap, srcRect, destRect, null);

                                graphicsHolder.unlockCanvasAndPost(canvas);
                                graphicsSurfaceView.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        int x = (int) event.getX();
                                        int y = (int) event.getY();
                                        if (event.getAction() == MotionEvent.ACTION_UP) {
                                            if (DRAW_TOOL != -1) {
                                                if (DRAW_TOOL == DRAW_HOLOGRAM) {
                                                    if (hologramDraw == null) {
                                                        hologramDraw = new HologramDraw();
                                                    }
                                                    Canvas canvas = graphicsHolder.lockCanvas();
                                                    canvas.drawBitmap(bitmap, srcRect, destRect, null);
                                                    Bitmap hologramBitmap = null;
                                                    if (DRAW_TOOL_OPTION == DRAW_HOLOGRAM_DEFAULT) {
                                                        Bitmap default_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.spotlight_icon_btn, new BitmapFactory.Options());
                                                        int newWidth = 200;  // Replace with your desired width
                                                        int newHeight = 250; // Replace with your desired height
                                                        hologramBitmap = Bitmap.createScaledBitmap(default_bitmap, newWidth, newHeight, true);

                                                    }
                                                    if (DRAW_TOOL_OPTION == DRAW_HOLOGRAM_YELLOW) {
                                                        Bitmap default_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.spotlight_icon_btn, new BitmapFactory.Options());
                                                        int newWidth = 200;  // Replace with your desired width
                                                        int newHeight = 250; // Replace with your desired height
                                                        hologramBitmap = Bitmap.createScaledBitmap(default_bitmap, newWidth, newHeight, true);

                                                    }
                                                    if (DRAW_TOOL_OPTION == DRAW_HOLOGRAM_WHITE) {
                                                        Bitmap default_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.spotlight_icon_btn, new BitmapFactory.Options());
                                                        int newWidth = 200;  // Replace with your desired width
                                                        int newHeight = 250; // Replace with your desired height
                                                        hologramBitmap = Bitmap.createScaledBitmap(default_bitmap, newWidth, newHeight, true);

                                                    }
                                                    if (DRAW_TOOL_OPTION == DRAW_HOLOGRAM_BLUE) {
                                                        Bitmap default_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.spotlight_icon_btn, new BitmapFactory.Options());
                                                        int newWidth = 200;  // Replace with your desired width
                                                        int newHeight = 250; // Replace with your desired height
                                                        hologramBitmap = Bitmap.createScaledBitmap(default_bitmap, newWidth, newHeight, true);

                                                    }


                                                    hologramDraw.drawIfNotOverlaying(canvas, x, y, hologramBitmap);
                                                    graphicsHolder.unlockCanvasAndPost(canvas);

                                                }
                                            }

                                        }


                                        return true;
                                    }
                                });
                            }


                        }
                    }, 10);

                }
                isdrawingMode = true;
            }
        });


        drawSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graphicsSurfaceView.setVisibility(GONE);

                mediaPlayer.release();
                mediaPlayer = null;
                mediaPlayer = new MediaPlayer();

                try {
                    mediaPlayer.setDataSource(getApplicationContext(), videoUri);
                    mediaPlayer.setDisplay(videoSurfaceView.getHolder());
                    mediaPlayer.prepare();
                    mediaPlayer.seekTo((int) currentFrameMilli);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                hologramDraw = null;
                formationArrow.setBackgroundTintList(null);
                formationArea.setBackgroundTintList(null);
                formationLine.setBackgroundTintList(null);
                formationText.setBackgroundTintList(null);
                spotlightMain.setBackgroundTintList(null);

                seekbarLayout.setVisibility(VISIBLE);
                topBarLayout.setVisibility(VISIBLE);
                playBtn.setVisibility(VISIBLE);
                drawBtn.setVisibility(VISIBLE);
                toolsLayout.setBackground(getDrawable(R.drawable.border));
                drawSaveBtn.setVisibility(GONE);
                isdrawingMode = false;
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


        videoUri = getIntent().getData();
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

    private Bitmap extractFrame(Uri videoUri, long timeInMillis) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            // Set the data source from the Uri
            retriever.setDataSource(this, videoUri);

            // Get the frame at the specified time
            Bitmap frameBitmap = retriever.getFrameAtTime(timeInMillis * 10, MediaMetadataRetriever.OPTION_CLOSEST);
            retriever.release();
            return frameBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    boolean issurfaceInit = false;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {


        if (holder.getSurface() == videoSurfaceView.getHolder().getSurface()) {
            message("video surfaceview");
            if (!issurfaceInit) {
                mediaPlayer.setDisplay(holder);
                issurfaceInit = true;
            }


        } else {
            message("graphics surfaceview");
            graphicsHolder = holder;

        }


    }

    private void message(String mes) {
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
        Log.i("test", "surface view distroyed");
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
