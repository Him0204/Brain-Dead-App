package com.example.sehh3165_gp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

public class Game7 extends AppCompatActivity implements View.OnClickListener {

    private static final int SAMPLE_RATE = 44100; // Can be adjusted
    private AudioRecord audioRecorder;
    private boolean isRecording = false;
    private ImageButton whiteMic;
    private TextView canHear, cannotHear;
    ImageButton home, sound, game_hint, game_reset;

    SharedPreferences prefs;
    String email;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game7_speak);

        Bundle extras = getIntent().getExtras();
        email = extras != null ? extras.getString("Email") : null;

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
            sound.setImageDrawable(muted);
        } else {
            sound.setImageDrawable(speaker);
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
        short[] buffer = new short[AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)];
        while (isRecording) {
            int readSize = audioRecorder.read(buffer, 0, buffer.length);
            int maxAmplitude = 0;
            for (int i = 0; i < readSize; i++) {
                maxAmplitude = Math.max(maxAmplitude, Math.abs(buffer[i]));
            }
            if (maxAmplitude > 10) {
                Toast.makeText(this, String.valueOf(maxAmplitude), Toast.LENGTH_SHORT).show();
                runOnUiThread(() -> {
                    cannotHear.setVisibility(TextView.INVISIBLE);
                    canHear.setVisibility(TextView.VISIBLE);
                });
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
        Toast.makeText(this, "Grandpa cannot hear you… speak LOUDER!", Toast.LENGTH_SHORT).show();
    }

    private void resetActivity() {
        Intent i = new Intent(this, Game7.class);
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
        if (prefs.getBoolean("music_enabled", true)) {
            startService(new Intent(this, BackgroundMusic.class));
        }
    }

}
