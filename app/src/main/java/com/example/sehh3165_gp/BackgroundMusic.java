package com.example.sehh3165_gp;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class BackgroundMusic extends Service {
    private MediaPlayer player;
    private final IBinder binder = new LocalBinder();
    private SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.game_bgm);
        player.setLooping(true);
        player.setVolume(1.0f, 1.0f);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("music_enabled", true) && !player.isPlaying()) {
            player.start();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!player.isPlaying() && prefs.getBoolean("music_enabled", true)) {
            player.start();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    public class LocalBinder extends Binder {
        BackgroundMusic getService() {
            return BackgroundMusic.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public boolean isMusicPlaying() {
        return player != null && player.isPlaying();
    }
}
