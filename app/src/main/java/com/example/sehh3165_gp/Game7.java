package com.example.sehh3165_gp;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
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
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import com.airbnb.lottie.LottieAnimationView;

public class Game7 extends AppCompatActivity implements View.OnClickListener {

    private static final int SAMPLE_RATE = 44100; // Can be adjusted
    private AudioRecord audioRecorder;
    private boolean isRecording = false;
    private ImageButton whiteMic;
    private TextView canHear, cannotHear;
    ImageButton home, sound, game_hint, game_reset;

    SharedPreferences prefs;
    String email;
    int stage;
    int old_time_taken;
    int new_time_taken;
    int time_difference;
    private LottieAnimationView animationView;
    private Handler handler;
    boolean won = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game7_speak);

        Bundle extras = getIntent().getExtras();
        email = extras != null ? extras.getString("email") : null;
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        stage = Integer.parseInt(dbHelper.getInfo(email, 1));
        old_time_taken = Integer.parseInt(dbHelper.getInfo(email, 2));
        new_time_taken = (int) System.currentTimeMillis();

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

        whiteMic = findViewById(R.id.image_mic_white);
        canHear = findViewById(R.id.old_ppl_msg_hear);
        cannotHear = findViewById(R.id.old_ppl_msg);

        Drawable whiteMicDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.g7_mic_white);
        Drawable redMicDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.g7_mic_red);

        whiteMic.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    whiteMic.setImageDrawable(redMicDrawable);
                    startRecording();
                    return true;
                case MotionEvent.ACTION_UP:
                    whiteMic.setImageDrawable(whiteMicDrawable);
                    stopRecording();
                    return true;
            }
            return false;
        });

        setupViews();
        setupListeners();
    }

    private void setupViews() {
        whiteMic = findViewById(R.id.image_mic_white);
        canHear = findViewById(R.id.old_ppl_msg_hear);
        cannotHear = findViewById(R.id.old_ppl_msg);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupListeners() {
        Drawable whiteMicDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.g7_mic_white);
        Drawable redMicDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.g7_mic_red);

        whiteMic.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    whiteMic.setImageDrawable(redMicDrawable);
                    startRecording();
                    return true;
                case MotionEvent.ACTION_UP:
                    whiteMic.setImageDrawable(whiteMicDrawable);
                    stopRecording();
                    checkWinCondition();
                    return true;
            }
            return false;
        });
    }

    private void startRecording() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            if (audioRecorder == null || !isRecording) {
                int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
                audioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, minBufferSize);

                if (audioRecorder.getState() == AudioRecord.STATE_INITIALIZED) {
                    audioRecorder.startRecording();
                    isRecording = true;
                    Thread recordingThread = new Thread(this::analyzeAudio);
                    recordingThread.start();
                }  // Handle initialization error

            }
        }  // Permission is not granted, handle accordingly

    }

    private void stopRecording() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            if (audioRecorder != null && isRecording) {
                isRecording = false;
                audioRecorder.stop();
                audioRecorder.release();
                audioRecorder = null;
                runOnUiThread(() -> {
                    canHear.setVisibility(TextView.INVISIBLE);
                    cannotHear.setVisibility(TextView.VISIBLE);
                });
            }
        }  // Permission is not granted, handle accordingly

    }


    private void analyzeAudio() {
        // Modify this method to be sensitive to normal speaking levels
        short[] buffer = new short[AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)];
        while (isRecording) {
            int readSize = audioRecorder.read(buffer, 0, buffer.length);
            int maxAmplitude = 0;
            for (int i = 0; i < readSize; i++) {
                maxAmplitude = Math.max(maxAmplitude, Math.abs(buffer[i]));
            }
            int winThreshold = 3000;
            if (maxAmplitude > winThreshold) { // Lower the threshold for normal speaking
                runOnUiThread(() -> {
                    canHear.setVisibility(View.VISIBLE);
                    cannotHear.setVisibility(View.INVISIBLE);
                    win();
                });
                break; // Stop analyzing after winning
            }
        }
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
        Intent i = new Intent(Game7.this, LobbyPage.class);
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
        Toast.makeText(this, "Grandpa cannot hear you… speak LOUDER!", Toast.LENGTH_SHORT).show();
    }

    private void resetActivity() {
        Intent i = new Intent(this, Game7.class);
        i.putExtra("email", email);
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
        if (prefs.getBoolean("music_enabled", true)) {
            startService(new Intent(this, BackgroundMusic.class));
        }
    }

    private void checkWinCondition() {
        // This method will be called after stopping recording to check if the player has won
        canHear.setVisibility(View.VISIBLE);
        cannotHear.setVisibility(View.INVISIBLE);
        win();
    }

    private void win() {
        time_difference = (int) System.currentTimeMillis() - new_time_taken;
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());

        if(stage == 6){
            if (time_difference < old_time_taken || old_time_taken == 0){
                dbHelper.updateStatus(email, "7", String.valueOf(time_difference));
            } else {
                dbHelper.updateStatus(email, "7", String.valueOf(old_time_taken));
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
        progress_menu.setVisibility(View.VISIBLE);
        stage_complete_txt.setText("Stage 4 COMPLETE!");
        Button button_continue = layout.findViewById(R.id.button_continue);
        Button button_back_to_lobby = layout.findViewById(R.id.button_back_to_lobby);
        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Game7.this, Game8.class);
                i.putExtra("email", email);
                startActivity(i);
            }
        });
        button_back_to_lobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Game7.this, LobbyPage.class);
                i.putExtra("email", email);
                startActivity(i);
            }
        });
        ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(progress_menu, "alpha", 0f, 1f);
        fadeInAnimator.setDuration(1000);
        fadeInAnimator.start();
    }
}
