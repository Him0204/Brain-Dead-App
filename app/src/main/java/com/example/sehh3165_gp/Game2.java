package com.example.sehh3165_gp;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Game2 extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    float xAxis, yAxis, buttonX, buttonY;
    int lastAction;

    ImageButton home, sound, game_hint, game_reset, next;
    ImageButton cryingBaby, toy, toy2, toy3;
    AudioManager audioManager;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game2_baby);

        Bundle extras = getIntent().getExtras();
        email = extras != null ? extras.getString("Email") : null;

        home = findViewById(R.id.imageButton_home);
        sound = findViewById(R.id.imageButton_sound);
        game_hint = findViewById(R.id.imageButton_hint);
        game_reset = findViewById(R.id.imageButton_reset);
        next = findViewById(R.id.imageButton_next);
        home.setOnClickListener(this);
        sound.setOnClickListener(this);
        game_hint.setOnClickListener(this);
        game_reset.setOnClickListener(this);
        next.setOnClickListener(this);

        cryingBaby = findViewById(R.id.imageButton_crying_baby);
        toy = findViewById(R.id.imageButton_toy);
        toy2 = findViewById(R.id.imageButton_toy2);
        toy3 = findViewById(R.id.imageButton_toy3);
        toy.setOnTouchListener(this);
        toy2.setOnTouchListener(this);
        toy3.setOnTouchListener(this);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                buttonX = v.getX();
                buttonY = v.getY();
                xAxis = buttonX - event.getRawX();
                yAxis = buttonY - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
                break;
            case MotionEvent.ACTION_MOVE:
                v.setX(event.getRawX() + xAxis);
                v.setY(event.getRawY() + yAxis);
                lastAction = MotionEvent.ACTION_MOVE;
                break;
            case MotionEvent.ACTION_UP:
                if (lastAction == MotionEvent.ACTION_MOVE) {
                    if (Overlapped(v, cryingBaby)) {
                        Toast.makeText(this, "X", Toast.LENGTH_SHORT).show();
                    }
                    v.performClick();
                    v.setX(buttonX);
                    v.setY(buttonY);
                }
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageButton_home) {
            Intent i = new Intent(Game2.this, LobbyPage.class);
            i.putExtra("Email", email);
            startActivity(i);
        }
        else if (v.getId() == R.id.imageButton_sound) {
            Intent musicIntent = new Intent(this, BackgroundMusic.class);
            stopService(musicIntent);
        }
        else if (v.getId() == R.id.imageButton_hint) {
            Toast.makeText(Game2.this, "Think out of the box", Toast.LENGTH_SHORT).show();
        }
        else if (v.getId() == R.id.imageButton_reset) {
            Intent i = new Intent(this, Game2.class);
            i.putExtra("Email", email);
            startActivity(i);
            finish();
        }
        else if (v.getId() == R.id.imageButton_next) {
            Intent i = new Intent(Game2.this, Game3.class);
            i.putExtra("Email", email);
            startActivity(i);
        }
    }

    private boolean Overlapped(View firstView, View secondView) {
        int[] firstPosition = new int[2];
        int[] secondPosition = new int[2];

        firstView.getLocationOnScreen(firstPosition);
        secondView.getLocationOnScreen(secondPosition);

        int firstViewRight = firstPosition[0] + firstView.getWidth();
        int firstViewBottom = firstPosition[1] + firstView.getHeight();
        int secondViewRight = secondPosition[0] + secondView.getWidth();
        int secondViewBottom = secondPosition[1] + secondView.getHeight();

        return !(firstPosition[0] > secondViewRight || firstViewRight < secondPosition[0] ||
                firstPosition[1] > secondViewBottom || firstViewBottom < secondPosition[1]);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        boolean isMusicMute = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0;
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && isMusicMute) {
            win();
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    private void win() {
        Toast.makeText(this,  "v", Toast.LENGTH_SHORT).show();
        next.setVisibility(View.VISIBLE);
    }

}
