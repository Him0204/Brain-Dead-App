package com.example.sehh3165_gp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Game7 extends AppCompatActivity {

    private static final int SAMPLE_RATE = 44100; // Can be adjusted
    private AudioRecord audioRecorder;
    private boolean isRecording = false;
    private ImageButton whiteMic;
    private TextView canHear, cannotHear;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game7_speak);

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
            if (maxAmplitude > 10000) {
                runOnUiThread(() -> {
                    cannotHear.setVisibility(TextView.INVISIBLE);
                    canHear.setVisibility(TextView.VISIBLE);
                });
            }
        }
    }
}
