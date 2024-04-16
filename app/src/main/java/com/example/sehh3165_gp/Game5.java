package com.example.sehh3165_gp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import com.airbnb.lottie.LottieAnimationView;
import java.util.ArrayList;
import java.util.List;

public class Game5 extends Activity implements View.OnTouchListener {

    private int deltaX, deltaY;
    String email;
    ViewGroup _root;
    ImageButton glue, roof, garage, body;
    ImageView house_result, glue_result;
    List<ImageButton> buttonArray = new ArrayList<>();
    View progress_menu;
    Handler handler;
    LottieAnimationView animationView;
    Button button_continue, button_back_to_lobby;

    int time_taken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game5_house);
        time_taken = 0; //delete this when synced!
        Bundle extras = getIntent().getExtras();
        email = extras != null ? extras.getString("email") : null;

        _root = findViewById(R.id.relative_layout);

        glue = findViewById(R.id.imageButton_glue);
        roof = findViewById(R.id.imageButton_roof);
        garage = findViewById(R.id.imageButton_garage);
        body = findViewById(R.id.imageButton_body);

        buttonArray.add(glue);
        buttonArray.add(roof);
        buttonArray.add(garage);
        buttonArray.add(body);

        glue.setOnTouchListener(this);
        roof.setOnTouchListener(this);
        garage.setOnTouchListener(this);
        body.setOnTouchListener(this);
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
                    house_result = findViewById(R.id.imageView_house_result);
                    glue_result = findViewById(R.id.imageButton_glue_result);
                    house_result.setVisibility(View.VISIBLE);
                    glue_result.setVisibility(View.VISIBLE);
                    glue.setVisibility(View.GONE);
                    roof.setVisibility(View.GONE);
                    garage.setVisibility(View.GONE);
                    body.setVisibility(View.GONE);

                    // Create a PopupWindow object
                    PopupWindow popupWindow = new PopupWindow(Game5.this);

                    // Inflate the new layout
                    LayoutInflater inflater = LayoutInflater.from(Game5.this);
                    View newLayout = inflater.inflate(R.layout.progress_menu, null);

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

                                    .withEndAction(() -> {
                                        // Hide the animation view after fading out
                                        animationView.setVisibility(View.GONE);

                                        handler = new Handler();
                                        handler.postDelayed(() -> {
                                            progress_menu = newLayout.findViewById(R.id.progress_menu);
                                            progress_menu.setVisibility(View.VISIBLE);
                                            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                                            dbHelper.updateStatus(email, "5", String.valueOf(time_taken));
                                            button_continue.setOnClickListener(v12 -> {
                                                Intent i = new Intent(Game5.this, Game6.class);
                                                i.putExtra("email", email);
                                                startActivity(i);
                                            });
                                            button_back_to_lobby.setOnClickListener(v1 -> {
                                                Intent i = new Intent(Game5.this, LobbyPage.class);
                                                i.putExtra("email", email);
                                                startActivity(i);
                                            });
                                            ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(progress_menu, "alpha", 0f, 1f);

                                            // Set the duration for the animation
                                            fadeInAnimator.setDuration(1000); // 1 second

                                            // Start the animation
                                            fadeInAnimator.start();
                                        }, 300);
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
}