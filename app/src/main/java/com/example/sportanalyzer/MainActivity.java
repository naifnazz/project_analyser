package com.example.sportanalyzer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private ImageView imageView1, imageView2, imageView3,imageView4;

    private int[] images = {R.drawable.ad1, R.drawable.ad2, R.drawable.ad3, R.drawable.ad4};
    private int currentPage = 0;

    private static final int PICK_VIDEO_REQUEST = 1;
    private static final int PICK_PREVIOUS_VIDEO_REQUEST = 2;
    Button foottac, importbtn, webbutton;

    private DatabaseHelper dbHelper;

    @SuppressLint({"MissingInflatedId", "Range"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);

        ImagePagerAdapter adapter = new ImagePagerAdapter(images);
        viewPager.setAdapter(adapter);

        // Optional: Set a listener to perform actions when swiping occurs

        // Timer to automatically change images every 2 seconds
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (currentPage == images.length - 1) {
                            currentPage =0;
                        } else {
                            currentPage++;
                        }
                        viewPager.setCurrentItem(currentPage);
                    }
                });
            }
        }, 7, 7000);



        // Your existing code for initializing the rest of the app goes here
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        foottac = findViewById(R.id.foottac);
        importbtn = findViewById(R.id.importbtn);
        webbutton = findViewById(R.id.webbutton);

        dbHelper = new DatabaseHelper(this);

        importbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Display options to pick a video: from storage or from previous videos
                showVideoPickerOptions();
            }
        });

        foottac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open footballtac activity
                Intent intent = new Intent(MainActivity.this, footballtac.class);
                startActivity(intent);
            }
        });


        webbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace "https://example.com" with the desired URL
                String url = "https://naifnazz.github.io/spotanalyser.com/";

                // Create an Intent to open a web page
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

                // Start the activity
                startActivity(intent);
            }
        });
    }

    private void showVideoPickerOptions() {
        // Inflate the custom layout
        View view = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);

        // Find buttons in the custom layout
        Button btnPickFromStorage = view.findViewById(R.id.btnPickFromStorage);
        Button btnPickPreviousVideo = view.findViewById(R.id.btnPickPreviousVideo);

        // Set click listeners
        btnPickFromStorage.setOnClickListener(v -> {
            showLoadingIndicator();
            pickVideoFromStorage();
        });

        btnPickPreviousVideo.setOnClickListener(v -> {
            showLoadingIndicator();
            pickPreviousVideo();
        });

        // Create and show AlertDialog with the custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle("Select Option");
        builder.show();
    }
    private void showLoadingIndicator() {
        ImageView loading = findViewById(R.id.loadinimageview);
        CardView circluarProgress = findViewById(R.id.progress_circular);

        if (loading != null) {
            loading.setVisibility(View.VISIBLE);

            Glide.with(this)
                    .asGif()
                    .load(R.drawable.ld1)
                    .into(loading);

            // Additional functionality for hiding the loading indicator after a delay
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loading.setVisibility(View.GONE);
                }
            }, 4000); // Delay in milliseconds (4 seconds in this example)
        }
    }


    private void pickVideoFromStorage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    private void pickPreviousVideo() {
        Intent intent = new Intent(MainActivity.this, PreviousVideosActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            // Pick from mobile storage
            Uri videoUri = data.getData();

            // Store the video URI in the database using the dbHelper instance
            long databaseId = dbHelper.addVideo(videoUri.toString());

            if (databaseId != -1) {
                // Video URI successfully added to the database

            } else {
                // Error occurred while adding video URI to the database

            }

            // Start the video player activity
            startVideoPlayerActivity(videoUri);
        } else if (requestCode == PICK_PREVIOUS_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            // Pick from previous videos
            Uri previousVideoUri = data.getData();
            startVideoPlayerActivity(previousVideoUri);
        }
    }


    private void startVideoPlayerActivity(Uri videoUri) {
        // Pass the video URI to the new activity
        Intent intent = new Intent(MainActivity.this, VideoPlayerActivity.class);
        intent.setData(videoUri);
        startActivity(intent);
    }
}
