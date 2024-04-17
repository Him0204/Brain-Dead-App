package com.example.sehh3165_gp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.List;

public class Game8 extends AppCompatActivity implements View.OnTouchListener {

    private int deltaX, deltaY;
    private String email;
    private ViewGroup _root;
    private MediaPlayer bgm, bgm_laser;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final List<ImageButton> buttonArray = new ArrayList<>();
    private final List<ImageButton> buttonArray1 = new ArrayList<>();

    private ImageButton sun;
    private ImageButton unequipped_gun;
    private ImageButton equipped_gun;
    private ImageButton monster;
    private ImageView laser_beam;

    private LottieAnimationView animationView;
    private View progress_menu;
    private Button button_continue;

    private final int time_taken = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game8_fight);

        Bundle extras = getIntent().getExtras();
        email = extras != null ? extras.getString("email") : null;

        _root = findViewById(R.id.relative_layout);

        bgm = MediaPlayer.create(this, R.raw.stage8_background_music);
        bgm.start();
        bgm.setLooping(true);

        sun = findViewById(R.id.sun);
        unequipped_gun = findViewById(R.id.unequipped_gun);
        ImageButton sword = findViewById(R.id.sword);
        ImageButton hammer = findViewById(R.id.hammer);
        equipped_gun = findViewById(R.id.equipped_gun);
        ImageButton person = findViewById(R.id.person);

        buttonArray.add(unequipped_gun);
        buttonArray.add(person);
        buttonArray1.add(equipped_gun);
        buttonArray1.add(sun);

        sun.setOnTouchListener(this);
        unequipped_gun.setOnTouchListener(this);
        sword.setOnTouchListener(this);
        hammer.setOnTouchListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bgm.isPlaying()) {
            bgm.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bgm.start();
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
                if (checkOverlap((ImageButton) v, buttonArray)) {
                    equipped_gun.setVisibility(View.VISIBLE);
                    unequipped_gun.setVisibility(View.GONE);
                } else if (checkOverlap((ImageButton) v, buttonArray1)) {
                    executeLaserSequence();
                }
                break;
        }

        _root.invalidate();
        return true;
    }

    private boolean checkOverlap(ImageButton button, List<ImageButton> buttons) {
        for (ImageButton otherButton : buttons) {
            if (button != otherButton) {
                int[] buttonPos = new int[2];
                int[] otherPos = new int[2];

                button.getLocationOnScreen(buttonPos);
                otherButton.getLocationOnScreen(otherPos);

                Rect rectButton = new Rect(buttonPos[0], buttonPos[1],
                        buttonPos[0] + button.getWidth(), buttonPos[1] + button.getHeight());
                Rect rectOther = new Rect(otherPos[0], otherPos[1],
                        otherPos[0] + otherButton.getWidth(), otherPos[1] + otherButton.getHeight());

                if (rectButton.intersect(rectOther)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void executeLaserSequence() {
        sun.setVisibility(View.GONE);
        bgm.setLooping(false);
        bgm.stop();
        bgm_laser = MediaPlayer.create(this, R.raw.stage8_laser);
        bgm_laser.start();

        handler.postDelayed(() -> {
            laser_beam = findViewById(R.id.laser_beam);
            monster = findViewById(R.id.monster);
            laser_beam.setVisibility(View.VISIBLE);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                laser_beam.setVisibility(View.INVISIBLE);
                monster.animate().alpha(0f).setDuration(1000).start();
            }, 2900);
        }, 3100);

        handler.postDelayed(() -> {
            PopupWindow popupWindow = new PopupWindow(Game8.this);
            LayoutInflater inflater = LayoutInflater.from(Game8.this);
            View newLayout = inflater.inflate(R.layout.gameover, null);
            popupWindow.setBackgroundDrawable(new ColorDrawable(0xCC000000)); // Semi-transparent
            popupWindow.setContentView(newLayout);
            popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindow.setFocusable(true);
            popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
            animationView = newLayout.findViewById(R.id.animation_view);
            animationView.addAnimatorListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    animationView.animate().alpha(0f).withEndAction(() -> {
                        animationView.setVisibility(View.GONE);
                        progress_menu = newLayout.findViewById(R.id.progress_menu);
                        progress_menu.setVisibility(View.VISIBLE);
                        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                        dbHelper.updateStatus(email, "8", String.valueOf(time_taken));
                        button_continue = newLayout.findViewById(R.id.button_continue);
                        button_continue.setOnClickListener(v1 -> {
                            Intent i = new Intent(Game8.this, LobbyPage.class);
                            i.putExtra("email", email);
                            startActivity(i);
                        });
                        ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(progress_menu, "alpha", 0f, 1f);
                        fadeInAnimator.setDuration(1000); // 1 second
                        fadeInAnimator.start();
                    }).start();
                }
            });
        }, 6800);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bgm != null) {
            bgm.release();
            bgm = null;
        }
        if (bgm_laser != null) {
            bgm_laser.release();
            bgm_laser = null;
        }
        handler.removeCallbacksAndMessages(null);
    }
}
