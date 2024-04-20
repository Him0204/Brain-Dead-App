package com.example.sehh3165_gp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
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
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.airbnb.lottie.LottieAnimationView;

public class Game4 extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    float xAxis, yAxis, buttonX, buttonY;
    int lastAction;
    boolean initialNetworkState;

    ImageButton home, sound, game_hint, game_reset;
    ImageButton networkPC, standalonePC, router;
    TextView title;
    SharedPreferences prefs;
    String email;
    int stage;
    int old_time_taken;
    int new_time_taken;
    int time_difference;
    private LottieAnimationView animationView;
    private Handler handler;
    boolean won = false;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game4_computer);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Bundle extras = getIntent().getExtras();
        email = extras != null ? extras.getString("email") : null;
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        stage = Integer.parseInt(dbHelper.getInfo(email, 1));
        old_time_taken = Integer.parseInt(dbHelper.getInfo(email, 2));
        new_time_taken = (int) System.currentTimeMillis();

        home = findViewById(R.id.imageButton_home);
        sound = findViewById(R.id.imageButton_sound);
        game_hint = findViewById(R.id.imageButton_hint);
        game_reset = findViewById(R.id.imageButton_reset);
        title = findViewById(R.id.textView_computer);
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

        networkPC = findViewById(R.id.imageButton_computer);
        standalonePC = findViewById(R.id.imageButton_no_wifi_computer);
        router = findViewById(R.id.imageButton_router);
        router.setOnTouchListener(this);

        initialNetworkState = isInternetAvailable();
        updateNetworkState(initialNetworkState);
        setupNetworkCallback();
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
        Intent i = new Intent(Game4.this, LobbyPage.class);
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
        Toast.makeText(this, "Try disconnecting the phone from the network", Toast.LENGTH_SHORT).show();
    }

    private void resetActivity() {
        Intent i = new Intent(this, Game4.class);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        connectivityManager.unregisterNetworkCallback(networkCallback);
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
                    /*
                    if (Overlapped(v, networkPC) || Overlapped(v, standalonePC)) {
                        Toast.makeText(this, "X", Toast.LENGTH_SHORT).show();
                    }
                     */
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

    private void setupNetworkCallback() {
        // Initialize connectivityManager
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Prepare the network callback to respond to network changes
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                runOnUiThread(() -> updateNetworkState(true));
            }

            @Override
            public void onLost(Network network) {
                runOnUiThread(() -> updateNetworkState(false));
            }
        };
        // Build the network request that specifies what kind of network capabilities you are interested in
        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        // Register the network callback with the network request
        connectivityManager.registerNetworkCallback(request, networkCallback);
    }


    private void updateNetworkState(boolean isOnline) {
        if (isOnline) {
            title.setText("4. Disconnect the WiFi");
            router.setVisibility(View.VISIBLE);
        }
        else {
            title.setText("4. Connect to the WiFi");
            router.setVisibility(View.INVISIBLE);
        }

        // Check if network state changed to pass the game
        if (isOnline != initialNetworkState && !won) {
            won = true;
            win();
            initialNetworkState = isOnline; // Update initial state to prevent multiple triggers
        }
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            }
        }
        return false;
    }

    private void win() {
        if (initialNetworkState) {
            router.setVisibility(View.INVISIBLE);
        }
        else {
            router.setVisibility(View.VISIBLE);
        }

        time_difference = (int) System.currentTimeMillis() - new_time_taken;
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        if(stage == 3){

            if (time_difference < old_time_taken || old_time_taken == 0){
                dbHelper.updateStatus(email, "4", String.valueOf(time_difference));
            } else {
                dbHelper.updateStatus(email, "4", String.valueOf(old_time_taken));
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
        stage_complete_txt.setText("Stage 4 COMPLETE!");
        Button button_continue = layout.findViewById(R.id.button_continue);
        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Game4.this, Game5.class);
                i.putExtra("email", email);
                startActivity(i);
            }
        });
        button_back_to_lobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Game4.this, LobbyPage.class);
                i.putExtra("email", email);
                startActivity(i);
            }
        });
        ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(progress_menu, "alpha", 0f, 1f);
        fadeInAnimator.setDuration(1000);
        fadeInAnimator.start();
    }
}
