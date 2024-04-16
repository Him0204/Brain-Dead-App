package com.example.sehh3165_gp;

import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class Game8 extends AppCompatActivity implements View.OnTouchListener {

    private int deltaX, deltaY;
    String email;
    ViewGroup _root;
    MediaPlayer bgm, bgm_laser;
    Handler handler = new Handler(Looper.getMainLooper());
    List<ImageButton> buttonArray = new ArrayList<>();
    List<ImageButton> buttonArray1 = new ArrayList<>();

    ImageButton sun, unequipped_gun, sword, hammer, equipped_gun, person;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        sword = findViewById(R.id.sword);
        hammer = findViewById(R.id.hammer);
        equipped_gun = findViewById(R.id.equipped_gun);
        person = findViewById(R.id.person);

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
        // Sequence logic goes here
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
