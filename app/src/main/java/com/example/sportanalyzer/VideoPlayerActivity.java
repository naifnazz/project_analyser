package com.example.sportanalyzer;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.projection.MediaProjection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.IOException;
import java.util.ArrayList;


public class VideoPlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback {


    private MediaPlayer mediaPlayer;
    private SurfaceView videoSurfaceView;
    SurfaceHolder videoHolder, graphicsHolder;
    SurfaceView graphicsSurfaceView;

    SecondsCounter counter = new SecondsCounter();
    LinearLayout topBarLayout, seekbarLayout, toolsLayout;

    private LinearLayout spotlightMain;
    private LinearLayout formationLine;
    private LinearLayout formationArea;
    private LinearLayout formationArrow;
    private LinearLayout formationText;
    private LinearLayout markerMain;
    private LinearLayout formationscan;
    private LinearLayout formationSpace;
    HologramDraw hologramDraw;
    FormationLine formationLineInstance;
    FormationArea formationAreaInstance;
    FormationMarker formationMarker;
    FormationSpace formationSpaceInstance;
    FormationScan formationScan;
    FormationArrow formationArrowInstance;
    FormateText formateTextInstance;
    ImageView undo;

    private SeekBar seekBar;
    private Handler handler;
    ImageView playBtn, drawBtn, drawSaveBtn;


    private boolean pause = false;
    LinearLayout spotlight_btn;
    boolean isplaying = false;
    boolean isdrawingMode = false;
    Uri videoUri;
    private LinearLayout editextlayout;
    private EditText editText;
    private ImageView edittextokbtn;

    int DRAW_TOOL_OPTION = -1;
    int DRAW_HOLOGRAM = 2000;
    int DRAW_FORMATION_LINE = 2001;
    int DRAW_FORMATION_AREA = 2002;
    int DRAW_ARROW = 2007;
    int DRAW_TEXT = 2005;
    int DRAW_MARKER = 2045;
    int DRAW_SCAN = 2087;
    int DRAW_SPACE = 2090;
    int DRAW_TOOL = -1;
    String text = "";
    int DRAW_HOLOGRAM_DEFAULT = 1, DRAW_HOLOGRAM_YELLOW = 29, DRAW_HOLOGRAM_WHITE = 92, DRAW_HOLOGRAM_BLUE = 89;
    int DRAW_MARKER_DEFAULT = 56, DRAW_MARKER_ROUND = 57, DRAW_MARKER_CROSS = 58, DRAW_MARKER_CURCLE = 59;
    int DRAW_SCAN_DEFAULT = 40, DRAW_SCAN_BACK = 41, DRAW_SCAN_FRONT = 42, DRAW_SCAN_DOWN = 44;
    public static final int DRAW_OPTION_DEFAULT_ARROW = 458645;
    public static final int DRAW_OPTION_CURVED = 490648584;
    public static final int DRAW_OPTION_DASH = 8767654;
    public static final int DRAW_OPTION_ZIGZAG = 786758758;
    Bitmap bitmapMain;
    Canvas canvasMain;





    public VideoPlayerActivity() {
    }


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
        markerMain = findViewById(R.id.marker_main);
        formationscan = findViewById(R.id.formation_scan);
        formationSpace = findViewById(R.id.formationspace);

        undo = findViewById(R.id.undo);


        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formationArrowInstance != null) {
                    formationArrowInstance.undo();

                    if (formationArrowInstance == null) {
                        formationArrowInstance = new FormationArrow();
                    }
                    Canvas canvas = graphicsHolder.lockCanvas();
                    if (canvas != null) {
                        Rect srcRect = new Rect(0, 0, bitmapMain.getWidth(), bitmapMain.getHeight());
                        RectF destRect = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
                        canvas.drawBitmap(bitmapMain, srcRect, destRect, null);
                        formationArrowInstance.setARROW_TYPE(DRAW_TOOL_OPTION);

                        formationArrowInstance.drawArrow(canvas, -1, -1);
                        graphicsHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        });


        editextlayout = findViewById(R.id.editextlayout);
        editText = findViewById(R.id.edittext);
        edittextokbtn = findViewById(R.id.edittextokbtn);
        counter.setInterval(100);

        counter.addInterfaceCallBack(new SecondsCounter.SecondsCounterInterface() {
            @Override
            public void updateTimeFormatSecondsCounter(long hour, long minute, long second) {

            }

            @Override
            public void totalSecondsCounter(long totalSeconds) {
                if (mediaPlayer != null) {
                    int current = mediaPlayer.getCurrentPosition();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            seekBar.setProgress(current);
                        }
                    });
                }
            }

            @Override
            public void timeQueueInterfaceCall(long hour, long minute, long second) {

            }
        });

        edittextokbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = editText.getText().toString();
                editextlayout.setVisibility(GONE);
            }
        });


        // Initialize formation line views
        formationLine = findViewById(R.id.formationline);

        formationLine.setOnClickListener(v -> {


            if (!isdrawingMode)
                return;

            if (DRAW_TOOL == DRAW_FORMATION_LINE) {
                DRAW_TOOL = -1;
                formationLine.setBackgroundTintList(null);
            } else {
                if (DRAW_TOOL == DRAW_HOLOGRAM) {
                    spotlightMain.setBackgroundTintList(null);
                } else if (DRAW_TOOL == DRAW_FORMATION_AREA) {
                    formationArea.setBackgroundTintList(null);
                } else if (DRAW_TOOL == DRAW_ARROW) {
                    formationArrow.setBackgroundTintList(null);

                } else if (DRAW_TOOL == DRAW_MARKER) {
                    markerMain.setBackgroundTintList(null);

                } else if (DRAW_TOOL == DRAW_SCAN) {
                    formationscan.setBackgroundTintList(null);

                } else if (DRAW_TOOL == DRAW_SPACE) {
                    formationSpace.setBackgroundTintList(null);

                } else if (DRAW_TOOL == DRAW_TEXT) {
                    formationText.setBackgroundTintList(null);

                }

                DRAW_TOOL = DRAW_FORMATION_LINE;
                ColorStateList colorStateList = ContextCompat.getColorStateList(getApplicationContext(), R.color.selectrion_highlight);
                formationLine.setBackgroundTintList(colorStateList);
            }
            editextlayout.setVisibility(GONE);
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

                    if (DRAW_TOOL == DRAW_HOLOGRAM) {
                        spotlightMain.setBackgroundTintList(null);
                    } else if (DRAW_TOOL == DRAW_FORMATION_LINE) {
                        formationLine.setBackgroundTintList(null);
                    } else if (DRAW_TOOL == DRAW_ARROW) {
                        formationArrow.setBackgroundTintList(null);

                    } else if (DRAW_TOOL == DRAW_TEXT) {
                        formationText.setBackgroundTintList(null);

                    } else if (DRAW_TOOL == DRAW_MARKER) {
                        markerMain.setBackgroundTintList(null);

                    } else if (DRAW_TOOL == DRAW_SCAN) {
                        formationscan.setBackgroundTintList(null);

                    } else if (DRAW_TOOL == DRAW_SPACE) {
                        formationSpace.setBackgroundTintList(null);

                    }

                    DRAW_TOOL = DRAW_FORMATION_AREA;
                    ColorStateList colorStateList = ContextCompat.getColorStateList(getApplicationContext(), R.color.selectrion_highlight);
                    formationArea.setBackgroundTintList(colorStateList);
                }
                editextlayout.setVisibility(GONE);
            }
        });

        //*******************************  // Initialize formation SPACE views
        formationSpace = findViewById(R.id.formationspace);
        formationSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isdrawingMode)
                    return;
                if (DRAW_TOOL == DRAW_SPACE) {
                    DRAW_TOOL = -1;
                    formationSpace.setBackgroundTintList(null);
                } else {

                    if (DRAW_TOOL == DRAW_HOLOGRAM) {
                        spotlightMain.setBackgroundTintList(null);
                    } else if (DRAW_TOOL == DRAW_FORMATION_LINE) {
                        formationLine.setBackgroundTintList(null);
                    } else if (DRAW_TOOL == DRAW_ARROW) {
                        formationArrow.setBackgroundTintList(null);

                    } else if (DRAW_TOOL == DRAW_TEXT) {
                        formationText.setBackgroundTintList(null);

                    } else if (DRAW_TOOL == DRAW_MARKER) {
                        markerMain.setBackgroundTintList(null);
                    } else if (DRAW_TOOL == DRAW_SCAN) {
                        formationscan.setBackgroundTintList(null);
                    } else if (DRAW_TOOL == DRAW_FORMATION_AREA) {
                        formationArea.setBackgroundTintList(null);
                    }

                    DRAW_TOOL = DRAW_SPACE;
                    ColorStateList colorStateList = ContextCompat.getColorStateList(getApplicationContext(), R.color.selectrion_highlight);
                    formationSpace.setBackgroundTintList(colorStateList);
                }
                editextlayout.setVisibility(GONE);
            }
        });
//***********************************************************************************************
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

                    if (DRAW_TOOL == DRAW_HOLOGRAM) {
                        spotlightMain.setBackgroundTintList(null);
                    } else if (DRAW_TOOL == DRAW_FORMATION_LINE) {
                        formationLine.setBackgroundTintList(null);
                    } else if (DRAW_TOOL == DRAW_FORMATION_AREA) {
                        formationArea.setBackgroundTintList(null);

                    } else if (DRAW_TOOL == DRAW_TEXT) {
                        formationText.setBackgroundTintList(null);

                    } else if (DRAW_TOOL == DRAW_MARKER) {
                        markerMain.setBackgroundTintList(null);

                    } else if (DRAW_TOOL == DRAW_SCAN) {
                        formationscan.setBackgroundTintList(null);

                    } else if (DRAW_TOOL == DRAW_SPACE) {
                        formationSpace.setBackgroundTintList(null);

                    }

                    DRAW_TOOL = DRAW_ARROW;
                    DRAW_TOOL_OPTION = DRAW_OPTION_DEFAULT_ARROW;
                    ColorStateList colorStateList = ContextCompat.getColorStateList(getApplicationContext(), R.color.selectrion_highlight);
                    formationArrow.setBackgroundTintList(colorStateList);
                }
                editextlayout.setVisibility(GONE);
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
                editextlayout.setVisibility(GONE);

            } else {

                if (DRAW_TOOL == DRAW_HOLOGRAM) {
                    spotlightMain.setBackgroundTintList(null);
                } else if (DRAW_TOOL == DRAW_FORMATION_LINE) {
                    formationLine.setBackgroundTintList(null);
                } else if (DRAW_TOOL == DRAW_FORMATION_AREA) {
                    formationArea.setBackgroundTintList(null);

                } else if (DRAW_TOOL == DRAW_ARROW) {
                    formationArrow.setBackgroundTintList(null);

                } else if (DRAW_TOOL == DRAW_MARKER) {
                    markerMain.setBackgroundTintList(null);

                } else if (DRAW_TOOL == DRAW_SCAN) {
                    formationscan.setBackgroundTintList(null);

                } else if (DRAW_TOOL == DRAW_SPACE) {
                    formationSpace.setBackgroundTintList(null);

                }
                DRAW_TOOL = DRAW_TEXT;
                editextlayout.setVisibility(VISIBLE);


                ColorStateList colorStateList = ContextCompat.getColorStateList(getApplicationContext(), R.color.selectrion_highlight);
                formationText.setBackgroundTintList(colorStateList);
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

        spotlightMain.setOnClickListener(v -> {
            if (!isdrawingMode)
                return;

            if (DRAW_TOOL == DRAW_HOLOGRAM) {
                DRAW_TOOL = -1;
                spotlightMain.setBackgroundTintList(null);
            } else {

                if (DRAW_TOOL == DRAW_TEXT) {
                    formationText.setBackgroundTintList(null);
                } else if (DRAW_TOOL == DRAW_FORMATION_LINE) {
                    formationLine.setBackgroundTintList(null);
                } else if (DRAW_TOOL == DRAW_FORMATION_AREA) {
                    formationArea.setBackgroundTintList(null);

                } else if (DRAW_TOOL == DRAW_ARROW) {
                    formationArrow.setBackgroundTintList(null);

                } else if (DRAW_TOOL == DRAW_MARKER) {
                    markerMain.setBackgroundTintList(null);

                } else if (DRAW_TOOL == DRAW_SCAN) {
                    formationscan.setBackgroundTintList(null);

                } else if (DRAW_TOOL == DRAW_SPACE) {
                    formationSpace.setBackgroundTintList(null);

                }
                DRAW_TOOL = DRAW_HOLOGRAM;
                DRAW_TOOL_OPTION = DRAW_HOLOGRAM_DEFAULT;

                ColorStateList colorStateList = ContextCompat.getColorStateList(getApplicationContext(), R.color.selectrion_highlight);
                spotlightMain.setBackgroundTintList(colorStateList);
            }

            editextlayout.setVisibility(GONE);
        });
//888888888888888888888888888888888888888marker
        markerMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Dialog dialog = new Dialog(VideoPlayerActivity.this);
                WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
                int[] location = new int[2];
                markerMain.getLocationInWindow(location);

                layoutParams.gravity = Gravity.TOP | Gravity.START;
                layoutParams.x = location[0] - markerMain.getWidth(); // Set x-coordinate
                layoutParams.y = location[1]; // Set y-coordinate

                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setAttributes(layoutParams);
                dialog.setContentView(R.layout.marker_option);

                ImageView defaultmarker;
                ImageView roudmarker;
                ImageView crossmarker;
                ImageView cuirclemarker;

                defaultmarker = dialog.findViewById(R.id.default_marker);
                roudmarker = dialog.findViewById(R.id.roud_marker);
                crossmarker = dialog.findViewById(R.id.cross_marker);
                cuirclemarker = dialog.findViewById(R.id.cuircle_marker);

                // Set click listeners
                defaultmarker.setOnClickListener(v1 -> {
                    DRAW_TOOL = DRAW_MARKER;
                    DRAW_TOOL_OPTION = DRAW_MARKER_DEFAULT;
                    dialog.dismiss();
                    // Add your logic for default hologram click
                });

                roudmarker.setOnClickListener(v12 -> {
                    DRAW_TOOL = DRAW_MARKER;
                    DRAW_TOOL_OPTION = DRAW_MARKER_ROUND;
                    dialog.dismiss();
                    // Add your logic for yellow hologram click
                });

                crossmarker.setOnClickListener(v13 -> {
                    DRAW_TOOL = DRAW_MARKER;
                    DRAW_TOOL_OPTION = DRAW_MARKER_CROSS;
                    dialog.dismiss();
                    // Add your logic for white hologram click
                });

                cuirclemarker.setOnClickListener(v14 -> {
                    DRAW_TOOL = DRAW_MARKER;
                    DRAW_TOOL_OPTION = DRAW_MARKER_CURCLE;
                    dialog.dismiss();
                    // Add your logic for blue hologram click
                });

                dialog.show();

                return false;
            }
        });
        markerMain.setOnClickListener(v -> {
            if (!isdrawingMode)
                return;

            if (DRAW_TOOL == DRAW_MARKER) {
                DRAW_TOOL = -1;
                markerMain.setBackgroundTintList(null);
            } else {

                if (DRAW_TOOL == DRAW_TEXT) {
                    formationText.setBackgroundTintList(null);
                } else if (DRAW_TOOL == DRAW_FORMATION_LINE) {
                    formationLine.setBackgroundTintList(null);
                } else if (DRAW_TOOL == DRAW_FORMATION_AREA) {
                    formationArea.setBackgroundTintList(null);

                } else if (DRAW_TOOL == DRAW_ARROW) {
                    formationArrow.setBackgroundTintList(null);

                } else if (DRAW_TOOL == DRAW_HOLOGRAM) {
                    spotlightMain.setBackgroundTintList(null);

                } else if (DRAW_TOOL == DRAW_SPACE) {
                    formationSpace.setBackgroundTintList(null);

                } else if (DRAW_TOOL == DRAW_SCAN) {
                    formationscan.setBackgroundTintList(null);

                }
                DRAW_TOOL = DRAW_MARKER;
                DRAW_TOOL_OPTION = DRAW_MARKER_DEFAULT;

                ColorStateList colorStateList = ContextCompat.getColorStateList(getApplicationContext(), R.color.selectrion_highlight);
                markerMain.setBackgroundTintList(colorStateList);
            }

            editextlayout.setVisibility(GONE);
        });
        //SCAN**************************************
        formationscan.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Dialog dialog = new Dialog(VideoPlayerActivity.this);
                WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
                int[] location = new int[2];
                formationscan.getLocationInWindow(location);

                layoutParams.gravity = Gravity.TOP | Gravity.START;
                layoutParams.x = location[0] - formationscan.getWidth(); // Set x-coordinate
                layoutParams.y = location[1]; // Set y-coordinate

                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setAttributes(layoutParams);
                dialog.setContentView(R.layout.scan_option);

                ImageView defaultscan;
                ImageView backscan;
                ImageView frontscan;
                ImageView downscan;

                defaultscan = dialog.findViewById(R.id.defaultscan);
                backscan = dialog.findViewById(R.id.frontscan);
                frontscan = dialog.findViewById(R.id.backscan);
                downscan = dialog.findViewById(R.id.downscan);

                // Set click listeners
                defaultscan.setOnClickListener(v1 -> {
                    DRAW_TOOL = DRAW_SCAN;
                    DRAW_TOOL_OPTION = DRAW_SCAN_DEFAULT;
                    dialog.dismiss();
                    // Add your logic for default hologram click
                });

                backscan.setOnClickListener(v12 -> {
                    DRAW_TOOL = DRAW_SCAN;
                    DRAW_TOOL_OPTION = DRAW_SCAN_BACK;
                    dialog.dismiss();
                    // Add your logic for yellow hologram click
                });

                frontscan.setOnClickListener(v13 -> {
                    DRAW_TOOL = DRAW_SCAN;
                    DRAW_TOOL_OPTION = DRAW_SCAN_FRONT;
                    dialog.dismiss();
                    // Add your logic for white hologram click
                });

                downscan.setOnClickListener(v14 -> {
                    DRAW_TOOL = DRAW_SCAN;
                    DRAW_TOOL_OPTION = DRAW_SCAN_DOWN;
                    dialog.dismiss();
                    // Add your logic for blue hologram click
                });

                dialog.show();

                return false;
            }
        });
        formationscan.setOnClickListener(v -> {
            if (!isdrawingMode)
                return;

            if (DRAW_TOOL == DRAW_SCAN) {
                DRAW_TOOL = -1;
                formationscan.setBackgroundTintList(null);
            } else {

                if (DRAW_TOOL == DRAW_TEXT) {
                    formationText.setBackgroundTintList(null);
                } else if (DRAW_TOOL == DRAW_FORMATION_LINE) {
                    formationLine.setBackgroundTintList(null);
                } else if (DRAW_TOOL == DRAW_FORMATION_AREA) {
                    formationArea.setBackgroundTintList(null);

                } else if (DRAW_TOOL == DRAW_ARROW) {
                    formationArrow.setBackgroundTintList(null);

                } else if (DRAW_TOOL == DRAW_HOLOGRAM) {
                    spotlightMain.setBackgroundTintList(null);

                } else if (DRAW_TOOL == DRAW_SPACE) {
                    formationSpace.setBackgroundTintList(null);

                } else if (DRAW_TOOL == DRAW_MARKER) {
                    markerMain.setBackgroundTintList(null);

                }
                DRAW_TOOL = DRAW_SCAN;
                DRAW_TOOL_OPTION = DRAW_SCAN_DEFAULT;

                ColorStateList colorStateList = ContextCompat.getColorStateList(getApplicationContext(), R.color.selectrion_highlight);
                formationscan.setBackgroundTintList(colorStateList);
            }

            editextlayout.setVisibility(GONE);
        });
//***************************************************************************************

        formationArrow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Dialog dialog = new Dialog(VideoPlayerActivity.this);
                WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
                int[] location = new int[2];
                formationArrow.getLocationInWindow(location);

                layoutParams.gravity = Gravity.TOP | Gravity.START;
                layoutParams.x = location[0] - formationArrow.getWidth(); // Set x-coordinate
                layoutParams.y = location[1]; // Set y-coordinate

                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setAttributes(layoutParams);
                dialog.setContentView(R.layout.arrow_options);

                ImageView defaultArrow;
                ImageView curvedArrow;
                ImageView dashArrow;
                ImageView zigzagArrow;

                defaultArrow = dialog.findViewById(R.id.default_arrow);
                curvedArrow = dialog.findViewById(R.id.curved_arrow);
                dashArrow = dialog.findViewById(R.id.dash_arrow);
                zigzagArrow = dialog.findViewById(R.id.zigzag_arrow);

// Set click listeners
                defaultArrow.setOnClickListener(v1 -> {
                    DRAW_TOOL = DRAW_ARROW;
                    DRAW_TOOL_OPTION = DRAW_OPTION_DEFAULT_ARROW;
                    Log.i("test", "defaukt arrow");
                    dialog.dismiss();
                    // Add your logic for default arrow click
                });

                curvedArrow.setOnClickListener(v12 -> {
                    DRAW_TOOL = DRAW_ARROW;
                    DRAW_TOOL_OPTION = DRAW_OPTION_CURVED;
                    dialog.dismiss();
                    Log.i("test", "curved arrow");

                    // Add your logic for curved arrow click
                });

                dashArrow.setOnClickListener(v13 -> {
                    DRAW_TOOL = DRAW_ARROW;
                    DRAW_TOOL_OPTION = DRAW_OPTION_DASH;
                    dialog.dismiss();
                    Log.i("test", "dash arrow");

                    // Add your logic for dash arrow click
                });

                zigzagArrow.setOnClickListener(v14 -> {
                    DRAW_TOOL = DRAW_ARROW;
                    DRAW_TOOL_OPTION = DRAW_OPTION_ZIGZAG;
                    dialog.dismiss();
                    Log.i("test", "zigzack arrow");

                    // Add your logic for zigzag arrow click
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
                    if (counter != null) {
                        counter.start();
                        Log.i("test", "counter starting");

                    }


                    playBtn.setImageResource(R.drawable.pause_circle);
                    isplaying = true;
                }

            } else {
                if (mediaPlayer != null) {
                    currentFrameMilli = mediaPlayer.getCurrentPosition();
                    mediaPlayer.pause();
                    if (counter != null) {
                        counter.pause();

                    }

                    playBtn.setImageResource(R.drawable.play_circle);
                    isplaying = false;
                }
            }
        });

        // Other initialization...


        drawBtn.setOnClickListener(v -> {
            if (!isdrawingMode) {
                if (mediaPlayer != null) {
                    currentFrameMilli = mediaPlayer.getCurrentPosition();
                    mediaPlayer.pause();

                    graphicsSurfaceView.setVisibility(View.VISIBLE);
                    seekbarLayout.setVisibility(View.GONE);
                    topBarLayout.setVisibility(GONE);
                    playBtn.setVisibility(View.GONE);
                    drawBtn.setVisibility(View.GONE);
                    toolsLayout.setBackground(new ColorDrawable(Color.TRANSPARENT));
                    drawSaveBtn.setVisibility(View.VISIBLE);
                    DRAW_TOOL=-1;

                    Bitmap bitmap = extractFrameAndStoreSequence(videoUri, currentFrameMilli);
                    addlistner(bitmap);



                }
                isdrawingMode = true;
            }
        });

//tick save******************************************************
        drawSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editextlayout.setVisibility(GONE);
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
                formationMarker = null;
                formationLineInstance = null;
                formationAreaInstance = null;
                formationArrowInstance = null;
                formationSpaceInstance = null;

                DRAW_TOOL = -1;
                formationArrow.setBackgroundTintList(null);
                formationArea.setBackgroundTintList(null);
                formationLine.setBackgroundTintList(null);
                formationText.setBackgroundTintList(null);
                spotlightMain.setBackgroundTintList(null);
                markerMain.setBackgroundTintList(null);
                formationSpace.setBackgroundTintList(null);
                formationscan.setBackgroundTintList(null);


                seekbarLayout.setVisibility(VISIBLE);
                topBarLayout.setVisibility(VISIBLE);
                playBtn.setVisibility(VISIBLE);
                drawBtn.setVisibility(VISIBLE);
                drawSaveBtn.setVisibility(GONE);
                isdrawingMode = false;
            }
        });

        mediaPlayer = new MediaPlayer();
        seekBar = findViewById(R.id.Seekbr);


        videoUri = getIntent().getData();
        try {
            mediaPlayer.setDataSource(this, videoUri);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }


        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
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


    //frame ectract****************************************************************************
    long currentFrameMilli;

    private Bitmap extractFrameAndStoreSequence(Uri videoUri, long timeInMillis) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        ArrayList<Bitmap> sequenceArray = new ArrayList<>();
        long frameTime = 0;

        try {
            // Set the data source from the Uri
            retriever.setDataSource(this, videoUri);

            // Get the frame at the specified time
            frameTime = timeInMillis * 1000;
            Bitmap firstFrame = retriever.getFrameAtTime(frameTime, MediaMetadataRetriever.OPTION_PREVIOUS_SYNC);
            sequenceArray.add(firstFrame);

            // Store frames in the sequence array for the next 6 seconds
            for (int i = 1; i <= 6; i++) {
                frameTime += 1000000; // Move to the next second (1,000,000 microseconds = 1 second)
                Bitmap frameBitmap = retriever.getFrameAtTime(frameTime, MediaMetadataRetriever.OPTION_PREVIOUS_SYNC);
                sequenceArray.add(frameBitmap);
            }

            retriever.release();

            // You can now use the 'sequenceArray' for further processing
            // and 'frameTime' for the timestamp of the first frame.

            // Example: Print timestamps of frames in the sequence
            for (int i = 0; i < sequenceArray.size(); i++) {
                long timestamp = frameTime + (i * 1000000);
                Log.d("extractFrameAndStoreSequence", "Frame " + i + " timestamp: " + timestamp);
            }

            return firstFrame; // You can return the first frame if needed
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //**********************************************************************************
    boolean issurfaceInit = false;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {


        if (holder.getSurface() == videoSurfaceView.getHolder().getSurface()) {

            if (!issurfaceInit) {
                mediaPlayer.setDisplay(holder);
                issurfaceInit = true;
            }


        } else {

            graphicsHolder = holder;

        }
        ///////////////////////////////////////////////////////////graphic media


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


        runOnUiThread(() -> mediaPlayer.seekTo(position));

    }


    @SuppressLint("SuspiciousIndentation")
    private void addlistner(Bitmap bitmap) {
        bitmapMain = bitmap;

//draaw canvas *************************************************************


        new Handler().postDelayed(new Runnable() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void run() {


                Canvas canvas = graphicsHolder.lockCanvas();
                if (canvas != null) {


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
                                            Bitmap default_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.yellowholo, new BitmapFactory.Options());
                                            int newWidth = 200;  // Replace with your desired width
                                            int newHeight = 300; // Replace with your desired height
                                            hologramBitmap = Bitmap.createScaledBitmap(default_bitmap, newWidth, newHeight, true);

                                        }
                                        if (DRAW_TOOL_OPTION == DRAW_HOLOGRAM_WHITE) {
                                            Bitmap default_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.whiteholo, new BitmapFactory.Options());
                                            int newWidth = 200;  // Replace with your desired width
                                            int newHeight = 250; // Replace with your desired height
                                            hologramBitmap = Bitmap.createScaledBitmap(default_bitmap, newWidth, newHeight, true);

                                        }
                                        if (DRAW_TOOL_OPTION == DRAW_HOLOGRAM_BLUE) {
                                            Bitmap default_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.blueholo, new BitmapFactory.Options());
                                            int newWidth = 150;  // Replace with your desired width
                                            int newHeight = 250; // Replace with your desired height
                                            hologramBitmap = Bitmap.createScaledBitmap(default_bitmap, newWidth, newHeight, true);

                                        }


                                        hologramDraw.drawIfNotOverlaying(canvas, x, y, hologramBitmap);
                                        graphicsHolder.unlockCanvasAndPost(canvas);
                                        canvasMain = canvas;


                                    } else if (DRAW_TOOL == DRAW_MARKER) {
                                        if (formationMarker == null) {
                                            formationMarker = new FormationMarker();
                                            Bitmap markerBitmap = null;
                                        }
                                        Canvas canvas = graphicsHolder.lockCanvas();
                                        canvas.drawBitmap(bitmap, srcRect, destRect, null);
                                        Bitmap markerBitmap = null;
                                        if (DRAW_TOOL_OPTION == DRAW_MARKER_DEFAULT) {
                                            Bitmap default_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.formation_line_marker, new BitmapFactory.Options());
                                            int newWidth = 200;  // Replace with your desired width
                                            int newHeight = 250; // Replace with your desired height
                                            markerBitmap = Bitmap.createScaledBitmap(default_bitmap, newWidth, newHeight, true);

                                        }
                                        if (DRAW_TOOL_OPTION == DRAW_MARKER_ROUND) {
                                            Bitmap default_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.roundmarker, new BitmapFactory.Options());
                                            int newWidth = 200;  // Replace with your desired width
                                            int newHeight = 300; // Replace with your desired height
                                            markerBitmap = Bitmap.createScaledBitmap(default_bitmap, newWidth, newHeight, true);

                                        }
                                        if (DRAW_TOOL_OPTION == DRAW_MARKER_CROSS) {
                                            Bitmap default_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.crossmarker, new BitmapFactory.Options());
                                            int newWidth = 200;  // Replace with your desired width
                                            int newHeight = 250; // Replace with your desired height
                                            markerBitmap = Bitmap.createScaledBitmap(default_bitmap, newWidth, newHeight, true);

                                        }
                                        if (DRAW_TOOL_OPTION == DRAW_MARKER_CURCLE) {
                                            Bitmap default_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.curclemarker, new BitmapFactory.Options());
                                            int newWidth = 150;  // Replace with your desired width
                                            int newHeight = 250; // Replace with your desired height
                                            markerBitmap = Bitmap.createScaledBitmap(default_bitmap, newWidth, newHeight, true);

                                        }


                                        formationMarker.drawIfNotOverlaying(canvas, x, y, markerBitmap);
                                        graphicsHolder.unlockCanvasAndPost(canvas);
                                        canvasMain = canvas;


//scan start*******************************************************************************************************


                                    } else if (DRAW_TOOL == DRAW_SCAN) {
                                        if (formationScan == null) {
                                            formationScan = new FormationScan();
                                        }
                                        Canvas canvas = graphicsHolder.lockCanvas();
                                        canvas.drawBitmap(bitmap, srcRect, destRect, null);
                                        Bitmap scanBitmap = null;
                                        if (DRAW_TOOL_OPTION == DRAW_SCAN_DEFAULT) {
                                            Bitmap default_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.topscan, new BitmapFactory.Options());
                                            int newWidth = 200;  // Replace with your desired width
                                            int newHeight = 250; // Replace with your desired height
                                            scanBitmap = Bitmap.createScaledBitmap(default_bitmap, newWidth, newHeight, true);

                                        }
                                        if (DRAW_TOOL_OPTION == DRAW_SCAN_BACK) {
                                            Bitmap default_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.backscan, new BitmapFactory.Options());
                                            int newWidth = 200;  // Replace with your desired width
                                            int newHeight = 300; // Replace with your desired height
                                            scanBitmap = Bitmap.createScaledBitmap(default_bitmap, newWidth, newHeight, true);

                                        }
                                        if (DRAW_TOOL_OPTION == DRAW_SCAN_FRONT) {
                                            Bitmap default_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.frontscan, new BitmapFactory.Options());
                                            int newWidth = 200;  // Replace with your desired width
                                            int newHeight = 250; // Replace with your desired height
                                            scanBitmap = Bitmap.createScaledBitmap(default_bitmap, newWidth, newHeight, true);

                                        }
                                        if (DRAW_TOOL_OPTION == DRAW_SCAN_DOWN) {
                                            Bitmap default_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.downscan, new BitmapFactory.Options());
                                            int newWidth = 150;  // Replace with your desired width
                                            int newHeight = 250; // Replace with your desired height
                                            scanBitmap = Bitmap.createScaledBitmap(default_bitmap, newWidth, newHeight, true);

                                        }


                                        formationScan.drawIfNotOverlaying(canvas, x, y, scanBitmap);
                                        graphicsHolder.unlockCanvasAndPost(canvas);
                                        canvasMain = canvas;


//////scan end********************************************************************************


                                    } else if (DRAW_TOOL == DRAW_FORMATION_LINE) {

                                        if (formationLineInstance == null) {
                                            formationLineInstance = new FormationLine();
                                        }
                                        Canvas canvas = graphicsHolder.lockCanvas();
                                        if (canvas != null) {

                                            canvas.drawBitmap(bitmap, srcRect, destRect, null);
                                            Bitmap default_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.formation_line_marker, new BitmapFactory.Options());
                                            int newWidth = 150;  // Replace with your desired width
                                            int newHeight = 150; // Replace with your desired height


                                            Bitmap pointbitmap = Bitmap.createScaledBitmap(default_bitmap, newWidth, newHeight, true);


                                            formationLineInstance.drawIfNotOverlaying(canvas, x, y, pointbitmap);
                                            graphicsHolder.unlockCanvasAndPost(canvas);
                                        }

                                    } else if (DRAW_TOOL == DRAW_FORMATION_AREA) {
                                        if (formationAreaInstance == null) {
                                            formationAreaInstance = new FormationArea();
                                        }
                                        Canvas canvas = graphicsHolder.lockCanvas();
                                        if (canvas != null) {

                                            canvas.drawBitmap(bitmap, srcRect, destRect, null);
                                            Bitmap default_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.formation_line_marker, new BitmapFactory.Options());
                                            int newWidth = 150;  // Replace with your desired width
                                            int newHeight = 150; // Replace with your desired height


                                            Bitmap pointbitmap = Bitmap.createScaledBitmap(default_bitmap, newWidth, newHeight, true);


                                            formationAreaInstance.drawIfNotOverlaying(canvas, x, y, pointbitmap);
                                            graphicsHolder.unlockCanvasAndPost(canvas);
                                        }

                                    } else if (DRAW_TOOL == DRAW_SPACE) {
                                        if (formationSpaceInstance == null) {
                                            formationSpaceInstance = new FormationSpace();
                                        }
                                        Canvas canvas = graphicsHolder.lockCanvas();
                                        if (canvas != null) {

                                            canvas.drawBitmap(bitmap, srcRect, destRect, null);
                                            Bitmap default_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.formation_line_marker, new BitmapFactory.Options());
                                            int newWidth = 70;  // Replace with your desired width
                                            int newHeight = 70; // Replace with your desired height


                                            Bitmap pointbitmap = Bitmap.createScaledBitmap(default_bitmap, newWidth, newHeight, true);


                                            formationSpaceInstance.drawIfNotOverlaying(canvas, x, y, pointbitmap);
                                            graphicsHolder.unlockCanvasAndPost(canvas);
                                        }

                                    } else if (DRAW_TOOL == DRAW_ARROW) {
                                        if (formationArrowInstance == null) {
                                            formationArrowInstance = new FormationArrow();
                                        }
                                        Canvas canvas = graphicsHolder.lockCanvas();
                                        if (canvas != null) {

                                            canvas.drawBitmap(bitmap, srcRect, destRect, null);
                                            formationArrowInstance.setARROW_TYPE(DRAW_TOOL_OPTION);

                                            formationArrowInstance.drawArrow(canvas, x, y);
                                            graphicsHolder.unlockCanvasAndPost(canvas);
                                        }

                                    } else if (DRAW_TOOL == DRAW_TEXT) {
                                        if (formateTextInstance == null) {
                                            formateTextInstance = new FormateText();
                                        }
                                        Canvas canvas = graphicsHolder.lockCanvas();

                                        if (canvas != null) {

                                            canvas.drawBitmap(bitmap, srcRect, destRect, null);


                                            text = editText.getText().toString().trim();


                                            String main = "  " + text + "  ";

                                            formateTextInstance.drawText(canvas, main, x, y);
                                            graphicsHolder.unlockCanvasAndPost(canvas);


                                        }


                                    }
                                }

                            }


                            return true;
                        }
                    });
                }

            }


        }, 10);
//****************************************************************************************


    }
}
