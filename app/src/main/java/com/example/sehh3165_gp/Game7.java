package com.example.sehh3165_gp;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;

public class Game7 extends AppCompatActivity {

    private MediaRecorder mediaRecorder;
    private ImageButton speakButton;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game7_speak);

        speakButton = findViewById(R.id.image_mic_white);
        statusText = findViewById(R.id.old_ppl_msg);

        speakButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startRecording();
                    statusText.setText("Listening...");
                    return true;
                case MotionEvent.ACTION_UP:
                    stopRecording();
                    return true;
            }
            return false;
        });
    }

    private void startRecording() {
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile("/dev/null");

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                mediaRecorder.getMaxAmplitude(); // Resetting maximum amplitude
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final Thread listeningThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaRecorder != null) {
                    int amplitude = mediaRecorder.getMaxAmplitude();
                    if (amplitude > 10000) { // Check dB level, this threshold might need adjustment
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                statusText.setText("I can hear you now!");
                            }
                        });
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        listeningThread.start();
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            statusText.setText("Say something! I can't hear you!");
        }
    }
}
