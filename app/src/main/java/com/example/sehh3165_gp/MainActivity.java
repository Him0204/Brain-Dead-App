package com.example.sehh3165_gp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent musicIntent = new Intent(this, BackgroundMusic.class);
        startService(musicIntent);
        startActivity(new Intent(MainActivity.this, LoginPage.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent musicIntent = new Intent(this, BackgroundMusic.class);
        startService(musicIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent musicIntent = new Intent(this, BackgroundMusic.class);
        stopService(musicIntent);
    }
}