package com.example.sehh3165_gp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class BackgroundMusic extends Service {

    private MediaPlayer player;
    private final IBinder binder = (IBinder) new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.game_bgm);
        player.setLooping(true);
        player.setVolume(1.0f, 1.0f);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!player.isPlaying()) {
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
