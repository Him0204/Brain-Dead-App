package com.example.sehh3165_gp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Game4 extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    float xAxis, yAxis, buttonX, buttonY;
    int lastAction;
    boolean initialNetworkState;

    ImageButton home, sound, game_hint, game_reset;
    ImageButton networkPC, standalonePC, router;

    String email;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game4_computer);

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

        networkPC = findViewById(R.id.imageButton_computer);
        standalonePC = findViewById(R.id.imageButton_no_wifi_computer);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        initialNetworkState = isInternetAvailable();
        updateNetworkState(initialNetworkState);

        router = findViewById(R.id.imageButton_router);
        router.setOnTouchListener(this);

        setupNetworkCallback();
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
                    if (Overlapped(v, networkPC) || Overlapped(v, standalonePC)) {
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageButton_home) {
            Intent i = new Intent(Game4.this, LobbyPage.class);
            i.putExtra("Email", email);
            startActivity(i);
        }
        else if (v.getId() == R.id.imageButton_sound) {
            Intent musicIntent = new Intent(this, BackgroundMusic.class);
            stopService(musicIntent);
        }
        else if (v.getId() == R.id.imageButton_hint) {
            Toast.makeText(Game4.this, "Think out of the box", Toast.LENGTH_SHORT).show();
        }
        else if (v.getId() == R.id.imageButton_reset) {
            Intent i = new Intent(this, Game4.class);
            i.putExtra("Email", email);
            startActivity(i);
            finish();
        }
    }

    private void setupNetworkCallback() {
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

        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        connectivityManager.registerNetworkCallback(request, networkCallback);
    }

    private void updateNetworkState(boolean isOnline) {
        if (isOnline) {
            networkPC.setVisibility(View.VISIBLE);
            standalonePC.setVisibility(View.INVISIBLE);
        } else {
            networkPC.setVisibility(View.INVISIBLE);
            standalonePC.setVisibility(View.VISIBLE);
        }

        // Check if network state changed to pass the game
        if (isOnline != initialNetworkState) {
            win();
            initialNetworkState = isOnline; // Update initial state to prevent multiple triggers
        }
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
        Toast.makeText(this, "Network change detected: Game passed", Toast.LENGTH_LONG).show();
    }

}
