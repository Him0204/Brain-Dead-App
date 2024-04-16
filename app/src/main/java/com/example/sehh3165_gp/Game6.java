package com.example.sehh3165_gp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.List;

public class stage6 extends AppCompatActivity implements View.OnTouchListener {
    private int deltaX;
    private int deltaY;
    String email;
    ViewGroup _root;
    ViewGroup gym_layout;
    View progress_menu;
    ImageView dumbbell;
    ImageView fat_guy;
    ImageView slim_guy;
    ImageView janitor;
    ImageView vacuum;
    ImageView protein;
    ImageView vacuum_for_guy;
    RelativeLayout janitor_dead;
    List<ImageView> buttonArray = new ArrayList<ImageView>();
    List<ImageView> buttonArray1 = new ArrayList<ImageView>();
    Handler handler;
    Boolean first_overlap;

    LottieAnimationView animationView;
    Button button_continue;
    Button button_back_to_lobby;
    int time_taken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stage6);
        first_overlap = false;

        time_taken = 0; //delete this when synced!
        Bundle extras = getIntent().getExtras();
        email = extras.getString("email");

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());

        _root = (ViewGroup)findViewById(R.id.relative_layout);
        gym_layout = (ViewGroup)findViewById(R.id.gym_layout);

        dumbbell = (ImageView) findViewById(R.id.image_dumbbell);
        fat_guy = (ImageView) findViewById(R.id.fat_guy);
        slim_guy = (ImageView) findViewById(R.id.slim_guy);
        protein = (ImageView) findViewById(R.id.protein);
        janitor = (ImageView) findViewById(R.id.janitor);
        vacuum = (ImageView) findViewById(R.id.vacuum);
        vacuum_for_guy = findViewById(R.id.vacuum_for_guy);
        janitor_dead = (RelativeLayout) findViewById(R.id.janitor_dead);

        buttonArray.add(dumbbell);
        buttonArray.add(vacuum);

        dumbbell.setOnTouchListener(this);
        protein.setOnTouchListener(this);
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
                if (checkOverlap((ImageView) v)) {
                    janitor.setVisibility(View.GONE);
                    vacuum.setVisibility(View.GONE);
                    janitor_dead.setVisibility(View.VISIBLE);
                    vacuum_for_guy.setVisibility(View.VISIBLE);

                    buttonArray1.add(fat_guy);
                    buttonArray1.add(vacuum_for_guy);

                    // Set onTouchListener for vacuum
                    vacuum_for_guy.setOnTouchListener(this);
                    first_overlap = true;
                } else if (first_overlap == true && checkOverlap1((ImageView) v)) {
                    slim_guy.setVisibility(View.VISIBLE);
                    vacuum_for_guy.setVisibility(View.GONE);
                    fat_guy.setVisibility(View.GONE);
                    vacuum.setVisibility(View.VISIBLE);

                    // Create a PopupWindow object
                    PopupWindow popupWindow = new PopupWindow(stage6.this);

                    // Inflate the new layout
                    LayoutInflater inflater = LayoutInflater.from(stage6.this);
                    View newLayout = inflater.inflate(R.layout.progress_menu_6, null);

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

                                            handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progress_menu = newLayout.findViewById(R.id.progress_menu);
                                                    progress_menu.setVisibility(View.VISIBLE);
                                                    dbHelper.updateStatus(email, "5",time_taken);
                                                    button_continue.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent i = new Intent(stage5.this, stage6.class);
                                                            i.putExtra("email", email);
                                                            startActivity(i);
                                                        }
                                                    });
                                                    button_back_to_lobby.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent i = new Intent(stage5.this, LobbyPage.class);
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
                                            }, 300);
                                        }
                                    })
                                    .start();
                        }
                    });
                }
                break;
        }

        _root.invalidate();
        return true;
    }

    private boolean checkOverlap(ImageView button) {
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

    private boolean checkOverlap1(ImageView button) {
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