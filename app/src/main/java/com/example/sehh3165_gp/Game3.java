package com.example.sehh3165_gp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

public class Game3 extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    float xAxis, yAxis, buttonX, buttonY;
    int lastAction;

    ImageButton home, sound, game_hint, game_reset;
    ImageButton wakeBoy, sleepBoy, Obj, Obj2, Obj3;

    SharedPreferences prefs;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game3_sleep);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

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

        Drawable speaker = ContextCompat.getDrawable(getApplicationContext(), R.drawable.setting_speaker);
        Drawable muted = ContextCompat.getDrawable(getApplicationContext(), R.drawable.setting_mute);

        boolean isPlaying = prefs.getBoolean("music_enabled", true);
        if (isPlaying) {
            sound.setImageDrawable(muted);
        } else {
            sound.setImageDrawable(speaker);
        }

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
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imageButton_home) {
            navigateHome();
        } else if (id == R.id.imageButton_sound) {
            toggleMusic();
        } else if (id == R.id.imageButton_hint) {
            showHint();
        } else if (id == R.id.imageButton_reset) {
            resetActivity();
        }
    }

    private void navigateHome() {
        Intent i = new Intent(Game3.this, LobbyPage.class);
        i.putExtra("Email", email);
        startActivity(i);
    }

    private void toggleMusic() {
        Drawable speaker = ContextCompat.getDrawable(getApplicationContext(), R.drawable.setting_speaker);
        Drawable muted = ContextCompat.getDrawable(getApplicationContext(), R.drawable.setting_mute);

        boolean isPlaying = prefs.getBoolean("music_enabled", true);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("music_enabled", !isPlaying);
        editor.apply();

        if (isPlaying) {
            stopService(new Intent(this, BackgroundMusic.class));
            sound.setImageDrawable(muted);
        } else {
            startService(new Intent(this, BackgroundMusic.class));
            sound.setImageDrawable(speaker);
        }
    }

    private void showHint() {
        Toast.makeText(this, "Try adjusting the brightness of the phone", Toast.LENGTH_SHORT).show();
    }

    private void resetActivity() {
        Intent i = new Intent(this, Game3.class);
        i.putExtra("Email", email);
        startActivity(i);
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (prefs.getBoolean("music_enabled", true)) {
            stopService(new Intent(this, BackgroundMusic.class));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Ensure sensorManager is initialized
        if (sensorManager == null) {
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        }
        if (sensorManager != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            if (lightSensor != null) {
                sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }  // Consider what to do if the sensor is not available

        }
        if (prefs.getBoolean("music_enabled", true)) {
            startService(new Intent(this, BackgroundMusic.class));
        }
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
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lux = event.values[0];
            if (lux == 0) {
                win();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //
    }

    private void win() {
        Toast.makeText(this,  "v", Toast.LENGTH_SHORT).show();
        wakeBoy.setVisibility(View.INVISIBLE);
        sleepBoy.setVisibility(View.VISIBLE);
    }

}
