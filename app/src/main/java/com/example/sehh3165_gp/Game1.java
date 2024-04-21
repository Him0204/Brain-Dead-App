package com.example.sehh3165_gp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.airbnb.lottie.LottieAnimationView;

public class Game1 extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    private ImageButton imageButton_EmptyCup;
    ImageButton home, sound, game_hint, game_reset;
    private SensorManager sensorManager;
    private Sensor orientationSensor;
    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;
    private float[] rotationMatrix = new float[9];
    private float[] orientationAngles = new float[3];

    SharedPreferences prefs;
    String email;
    int stage;
    int old_time_taken;
    int new_time_taken;
    int time_difference;
    private LottieAnimationView animationView;
    private Handler handler;
    boolean won = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game1_cup);

        Bundle extras = getIntent().getExtras();
        email = extras != null ? extras.getString("email") : null;
        email = "Emma.Wilson@gmail.com";
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        stage = Integer.parseInt(dbHelper.getInfo(email, 1));
        old_time_taken = Integer.parseInt(dbHelper.getInfo(email, 2));
        new_time_taken = (int) System.currentTimeMillis();

        if (!won){
            prefs = PreferenceManager.getDefaultSharedPreferences(this);
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
                sound.setImageDrawable(speaker);
            } else {
                sound.setImageDrawable(muted);
            }

            imageButton_EmptyCup = findViewById(R.id.imageButton_EmptyCup);
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            if (orientationSensor != null) {
                sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Toast.makeText(this, "Rotation vector sensor not available; defaulting to accelerometer + magnetometer", Toast.LENGTH_LONG).show();
                // Fallback if rotation vector sensor is not available
                Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                Sensor magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            SensorManager.getOrientation(rotationMatrix, orientationAngles);
            float azimuthInRadians = orientationAngles[0];
            float azimuthInDegress = (float) Math.toDegrees(azimuthInRadians);

            // Check for 90 degree anticlockwise rotation
            if (azimuthInDegress < -80 && azimuthInDegress > -100 && !won) {
                won = true;
                win();
            }
        } else {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.length);
                lastAccelerometerSet = true;
            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.length);
                lastMagnetometerSet = true;
            }
            if (lastAccelerometerSet && lastMagnetometerSet) {
                SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer);
                SensorManager.getOrientation(rotationMatrix, orientationAngles);
                float azimuthInRadians = orientationAngles[0];
                float azimuthInDegress = (float) Math.toDegrees(azimuthInRadians);

                if (azimuthInDegress < -80 && azimuthInDegress > -100) {
                    win();
                }
            }
        }
    }


    private void win() {
        Drawable water = ContextCompat.getDrawable(getApplicationContext(), R.drawable.g1_full_cup);
        imageButton_EmptyCup.setImageDrawable(water);

        time_difference = (int) System.currentTimeMillis() - new_time_taken;
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());

        if(stage == 0){
            if (time_difference < old_time_taken || old_time_taken == 0){
                dbHelper.updateStatus(email, "1", String.valueOf(time_difference));
            } else {
                dbHelper.updateStatus(email, "1", String.valueOf(old_time_taken));
            }
        }

        PopupWindow popupWindow = new PopupWindow(this);
        View popupView = LayoutInflater.from(this).inflate(R.layout.progress_menu, null);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xCC000000));
        popupWindow.setContentView(popupView);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
        setUpPopupAnimations(popupView);
    }

    private void setUpPopupAnimations(View layout) {
        animationView = layout.findViewById(R.id.animation_view);
        animationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                fadeOutAnimationViewAndShowProgress(layout);
            }
        });
    }

    private void fadeOutAnimationViewAndShowProgress(View layout) {
        animationView.animate().alpha(0f).withEndAction(() -> {
            handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> showProgressLayout(layout), 300);
        }).start();
    }

    private void showProgressLayout(View layout) {
        View progress_menu = layout.findViewById(R.id.progress_menu);
        TextView stage_complete_txt = layout.findViewById(R.id.stage_complete);
        Button button_back_to_lobby = layout.findViewById(R.id.button_back_to_lobby);
        progress_menu.setVisibility(View.VISIBLE);
        stage_complete_txt.setText("Stage 1 COMPLETE!");
        Button button_continue = layout.findViewById(R.id.button_continue);
        button_continue.setOnClickListener(v -> {
            Intent i = new Intent(Game1.this, Game2.class);
            i.putExtra("email", email);
            startActivity(i);
        });
        button_back_to_lobby.setOnClickListener(v -> {
            Intent i = new Intent(Game1.this, LobbyPage.class);
            i.putExtra("email", email);
            startActivity(i);
        });
        ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(progress_menu, "alpha", 0f, 1f);
        fadeInAnimator.setDuration(1000);
        fadeInAnimator.start();
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
        Intent i = new Intent(Game1.this, LobbyPage.class);
        i.putExtra("email", email);
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
        Toast.makeText(this, "Try tilting the phone", Toast.LENGTH_SHORT).show();
    }

    private void resetActivity() {
        Intent i = new Intent(this, Game1.class);
        i.putExtra("email", email);
        startActivity(i);
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
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
            orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            if (orientationSensor != null) {
                sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }  // Consider what to do if the sensor is not available

        }
        if (prefs.getBoolean("music_enabled", true)) {
            startService(new Intent(this, BackgroundMusic.class));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}
