package com.example.sehh3165_gp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.List;

public class stage8 extends AppCompatActivity implements View.OnTouchListener {

    private int deltaX;
    private int deltaY;
    String email;
    ViewGroup _root;
    MediaPlayer bgm;
    MediaPlayer bgm_laser;
    Handler handler;
    List<ImageButton> buttonArray = new ArrayList<ImageButton>();
    List<ImageButton> buttonArray1 = new ArrayList<ImageButton>();

    ImageButton sun;
    ImageButton unequipped_gun;
    ImageButton sword;
    ImageButton hammer;
    ImageButton equipped_gun;
    ImageButton person;
    ImageButton monster;
    ImageView laser_beam;

    LottieAnimationView animationView;
    View progress_menu;
    Button button_continue;

    int time_taken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stage8);
        time_taken = 0; //delete this when synced
        Bundle extras = getIntent().getExtras();
        email = extras.getString("email");

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());

        _root = (ViewGroup)findViewById(R.id.relative_layout);

        bgm = MediaPlayer.create(this, R.raw.stage8_background_music);
        bgm.start();
        bgm.setLooping(true);

        sun = (ImageButton) findViewById(R.id.sun);
        unequipped_gun = (ImageButton) findViewById(R.id.unequipped_gun);
        sword = (ImageButton) findViewById(R.id.sword);
        hammer = (ImageButton) findViewById(R.id.hammer);
        equipped_gun = (ImageButton) findViewById(R.id.equipped_gun);
        person = (ImageButton) findViewById(R.id.person);

        buttonArray.add(unequipped_gun);
        buttonArray.add(person);
        buttonArray1.add(equipped_gun);
        buttonArray1.add(sun);

        unequipped_gun.setOnTouchListener(this);
        sun.setOnTouchListener(this);
        sword.setOnTouchListener(this);
        hammer.setOnTouchListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bgm.stop();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                deltaX = X - lParams.leftMargin;
                deltaY = Y - lParams.topMargin;
                break;
            case MotionEvent.ACTION_MOVE:
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                layoutParams.leftMargin = X - deltaX;
                layoutParams.topMargin = Y - deltaY;
                v.setLayoutParams(layoutParams);
                break;

            case MotionEvent.ACTION_UP:
                if (checkOverlap((ImageButton) v)) {
                    equipped_gun.setVisibility(View.VISIBLE);
                    unequipped_gun.setVisibility(View.GONE);

                } else if (checkOverlap1((ImageButton) v)) {
                    laser_beam = (ImageView) findViewById(R.id.laser_beam);
                    monster = (ImageButton) findViewById(R.id.monster);
                    sun.setVisibility(View.GONE);
                    bgm.setLooping(false);
                    bgm.stop();
                    bgm_laser = MediaPlayer.create(this, R.raw.stage8_laser);
                    bgm_laser.start();
                    handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Set ImageView to visible after 2 seconds
                            laser_beam.setVisibility(View.VISIBLE);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Fade out the ImageButton over 1 second
                                    laser_beam.setVisibility(View.INVISIBLE);
                                    monster.animate().alpha(0f).setDuration(1000).start();
                                }
                            }, 2900);
                        }
                    }, 3100);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Create a PopupWindow object
                            PopupWindow popupWindow = new PopupWindow(stage8.this);

                            // Inflate the new layout
                            LayoutInflater inflater = LayoutInflater.from(stage8.this);
                            View newLayout = inflater.inflate(R.layout.game_over, null);

                            // Set the background of the PopupWindow to be semi-transparent
                            popupWindow.setBackgroundDrawable(new ColorDrawable(0xCC000000)); // Adjust the alpha value as needed

                            // Add the new layout to the PopupWindow
                            popupWindow.setContentView(newLayout);

                            // Set PopupWindow properties
                            popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                            popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
                            popupWindow.setFocusable(true);

                            // Show the PopupWindow
                            popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);

                            animationView = newLayout.findViewById(R.id.animation_view);
                            animationView.addAnimatorListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    // Fade out the animation view
                                    animationView.animate()
                                            .alpha(0f)

                                            .withEndAction(new Runnable() {
                                                @Override
                                                public void run() {
                                                    // Hide the animation view after fading out
                                                    animationView.setVisibility(View.GONE);
                                                    progress_menu = newLayout.findViewById(R.id.progress_menu);
                                                    progress_menu.setVisibility(View.VISIBLE);
                                                    dbHelper.updateStatus(email, "8",time_taken);
                                                    button_continue = findViewById(R.id.button_continue);
                                                    button_continue.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent i = new Intent(stage8.this, LobbyPage.class);
                                                            i.putExtra("email", email);
                                                            startActivity(i);
                                                        }
                                                    });
                                                    ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(progress_menu, "alpha", 0f, 1f);

                                                    // Set the duration for the animation
                                                    fadeInAnimator.setDuration(1000); // 1 second

                                                    // Start the animation
                                                    fadeInAnimator.start();
                                                }
                                            })
                                            .start();
                                }
                            });
                        }
                    }, 6800);
                }
                break;
        }

        _root.invalidate();
        return true;
    }

    protected void onDestroy() {
        super.onDestroy();
        // Release MediaPlayer resources
        if (bgm != null) {
            bgm.stop();
            bgm = null;
        }
        if (bgm_laser != null) {
            bgm_laser.stop();
            bgm_laser = null;
        }
        // Remove pending callbacks from the handler
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    private boolean checkOverlap(ImageButton button) {
        boolean intersect = true;
        for (int i = 0; i < buttonArray.size(); i++) { //use an array to store the id of buttons and then array.length?
            View view = buttonArray.get(i); //first button to be overlapped, then the second and third
            if (view != button) { //if compared button is not dragged button
                int[] firstPosition = new int[2];
                int[] secondPosition = new int[2];

                button.getLocationOnScreen(firstPosition);
                view.getLocationOnScreen(secondPosition);

                // Rect constructor parameters: left, top, right, bottom
                Rect rectButton = new Rect(firstPosition[0], firstPosition[1],
                        firstPosition[0] + button.getMeasuredWidth(), firstPosition[1] + button.getMeasuredHeight());
                Rect rectOtherButton = new Rect(secondPosition[0], secondPosition[1],
                        secondPosition[0] + view.getMeasuredWidth(), secondPosition[1] + view.getMeasuredHeight());

                if (!rectButton.intersect(rectOtherButton)) {
                    intersect = false;
                    break;
                }
            }
        }
        return intersect;
    }

    private boolean checkOverlap1(ImageButton button) {
        boolean intersect = true;
        for (int i = 0; i < buttonArray1.size(); i++) { //use an array to store the id of buttons and then array.length?
            View view = buttonArray1.get(i); //first button to be overlapped, then the second and third
            if (view != button) { //if compared button is not dragged button
                int[] firstPosition = new int[2];
                int[] secondPosition = new int[2];

                button.getLocationOnScreen(firstPosition);
                view.getLocationOnScreen(secondPosition);

                // Rect constructor parameters: left, top, right, bottom
                Rect rectButton = new Rect(firstPosition[0], firstPosition[1],
                        firstPosition[0] + button.getMeasuredWidth(), firstPosition[1] + button.getMeasuredHeight());
                Rect rectOtherButton = new Rect(secondPosition[0], secondPosition[1],
                        secondPosition[0] + view.getMeasuredWidth(), secondPosition[1] + view.getMeasuredHeight());

                if (!rectButton.intersect(rectOtherButton)) {
                    intersect = false;
                    break;
                }
            }
        }
        return intersect;
    }
}