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

public class Game3 extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    float xAxis, yAxis, buttonX, buttonY;
    int lastAction;

    ImageButton home, sound, game_hint, game_reset;
    ImageButton wakeBoy, sleepBoy, Obj, Obj2, Obj3;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game3_sleep);

        Bundle extras = getIntent().getExtras();
        email = extras != null ? extras.getString("Email") : null;

        home = findViewById(R.id.imageButton_home);
        sound = findViewById(R.id.imageButton_sound);
        game_hint = findViewById(R.id.imageButton_hint);
        game_reset = findViewById(R.id.imageButton_reset);
        home.setOnClickListener(this);
        sound.setOnClickListener(this);
        game_hint.setOnClickListener(this);
        game_reset.setOnClickListener(this);

        wakeBoy = findViewById(R.id.imageButton_insomnia);
        sleepBoy = findViewById(R.id.imageButton_sleeping);
        Obj = findViewById(R.id.imageButton_aromatherapy);
        Obj2 = findViewById(R.id.imageButton_mp3);
        Obj3 = findViewById(R.id.imageButton_milk);
        Obj.setOnTouchListener(this);
        Obj2.setOnTouchListener(this);
        Obj3.setOnTouchListener(this);

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
                    if (Overlapped(v, wakeBoy)) {
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
            Intent i = new Intent(Game3.this, LobbyPage.class);
            i.putExtra("Email", email);
            startActivity(i);
        }
        else if (v.getId() == R.id.imageButton_sound) {
            Intent musicIntent = new Intent(this, BackgroundMusic.class);
            stopService(musicIntent);
        }
        else if (v.getId() == R.id.imageButton_hint) {
            Toast.makeText(Game3.this, "Think out of the box", Toast.LENGTH_SHORT).show();
        }
        else if (v.getId() == R.id.imageButton_reset) {
            Intent i = new Intent(this, Game3.class);
            i.putExtra("Email", email);
            startActivity(i);
            finish();
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
        wakeBoy.setVisibility(View.INVISIBLE);
        sleepBoy.setVisibility(View.VISIBLE);
    }

}
