package com.example.sehh3165_gp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class LobbyPage extends AppCompatActivity {
    Button scoreboard;
    List<Button> buttonArray = new ArrayList<>();
    Button button_lvl1;
    Button button_lvl2;
    Button button_lvl3;
    Button button_lvl4;
    Button button_lvl5;
    Button button_lvl6;
    Button button_lvl7;
    Button button_lvl8;
    Button button_continue;
    int stage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby);

        Bundle extras = getIntent().getExtras();
        String email = extras != null ? extras.getString("email") : null;

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());

        String player_username = dbHelper.getInfo(email, 0);
        stage = Integer.parseInt(dbHelper.getInfo(email, 1));
        TextView username = findViewById(R.id.hello);
        username.setText(String.format("Hello! %s", player_username));

        button_lvl1 = findViewById(R.id.button_lv1);
        button_lvl2 = findViewById(R.id.button_lv2);
        button_lvl3 = findViewById(R.id.button_lv3);
        button_lvl4 = findViewById(R.id.button_lv4);
        button_lvl5 = findViewById(R.id.button_lv5);
        button_lvl6 = findViewById(R.id.button_lv6);
        button_lvl7 = findViewById(R.id.button_lv7);
        button_lvl8 = findViewById(R.id.button_lv8);

        buttonArray.add(button_lvl1);
        buttonArray.add(button_lvl2);
        buttonArray.add(button_lvl3);
        buttonArray.add(button_lvl4);
        buttonArray.add(button_lvl5);
        buttonArray.add(button_lvl6);
        buttonArray.add(button_lvl7);
        buttonArray.add(button_lvl8);

        stage = Integer.parseInt(dbHelper.getInfo(email, 1));

        for (int i = 0; i <= stage; i++){
            Button button = buttonArray.get(i);
            button.setBackgroundResource(R.drawable.lobby_button_rectangle2);
            button.setEnabled(true);
        }

        scoreboard = findViewById(R.id.button_scoreboard);
        scoreboard.setOnClickListener(v -> {
            Intent i = new Intent(LobbyPage.this, ScoreboardPage.class);
            i.putExtra("email", email);
            i.putExtra("username", player_username);
            startActivity(i);
        });

        //for the following, if player reached stage 8, can play whatever level they want, else the button is disabled
        button_continue = findViewById(R.id.button_continue);
        button_continue.setOnClickListener(v -> {
            if (stage == 0) {
                Intent i = new Intent(LobbyPage.this, Game1.class);
                i.putExtra("email", email);
                i.putExtra("stage", stage);
                startActivity(i);
            } else if (stage == 1) {
                Intent i = new Intent(LobbyPage.this, Game2.class);
                i.putExtra("email", email);
                i.putExtra("stage", stage);
                startActivity(i);
            } else if (stage == 2) {
                Intent i = new Intent(LobbyPage.this, Game3.class);
                i.putExtra("email", email);
                i.putExtra("stage", stage);
                startActivity(i);
            } else if (stage == 3) {
                Intent i = new Intent(LobbyPage.this, Game4.class);
                i.putExtra("email", email);
                i.putExtra("stage", stage);
                startActivity(i);
            } else if (stage == 4) {
                Intent i = new Intent(LobbyPage.this, Game5.class);
                i.putExtra("email", email);
                i.putExtra("stage", stage);
                startActivity(i);
            } else if (stage == 5) {
                Intent i = new Intent(LobbyPage.this, Game6.class);
                i.putExtra("email", email);
                i.putExtra("stage", stage);
                startActivity(i);
            } else if (stage == 6) {
                Intent i = new Intent(LobbyPage.this, Game7.class);
                i.putExtra("email", email);
                i.putExtra("stage", stage);
                startActivity(i);
            } else if (stage == 7) {
                Intent i = new Intent(LobbyPage.this, Game8.class);
                i.putExtra("email", email);
                i.putExtra("stage", stage);
                startActivity(i);
            } else if (stage == 8) {
                Intent i = new Intent(LobbyPage.this, Game8.class);
                i.putExtra("email", email);
                i.putExtra("stage", stage);
                startActivity(i);
            }
        });

        button_lvl1.setOnClickListener(v -> {
            Intent i = new Intent(LobbyPage.this, Game1.class);
            i.putExtra("email", email);
            i.putExtra("stage", stage);
            startActivity(i);
        });
        button_lvl2.setOnClickListener(v -> {
            Intent i = new Intent(LobbyPage.this, Game2.class);
            i.putExtra("email", email);
            i.putExtra("stage", stage);
            startActivity(i);
        });
        button_lvl3.setOnClickListener(v -> {
            Intent i = new Intent(LobbyPage.this, Game3.class);
            i.putExtra("email", email);
            i.putExtra("stage", stage);
            startActivity(i);
        });
        button_lvl4.setOnClickListener(v -> {
            Intent i = new Intent(LobbyPage.this, Game4.class);
            i.putExtra("email", email);
            i.putExtra("stage", stage);
            startActivity(i);
        });
        button_lvl5.setOnClickListener(v -> {
            Intent i = new Intent(LobbyPage.this, Game5.class);
            i.putExtra("email", email);
            i.putExtra("stage", stage);
            startActivity(i);
        });
        button_lvl6.setOnClickListener(v -> {
            Intent i = new Intent(LobbyPage.this, Game6.class);
            i.putExtra("email", email);
            i.putExtra("stage", stage);
            startActivity(i);
        });
        button_lvl7.setOnClickListener(v -> {
            Intent i = new Intent(LobbyPage.this, Game7.class);
            i.putExtra("email", email);
            i.putExtra("stage", stage);
            startActivity(i);
        });
        button_lvl8.setOnClickListener(v -> {
            Intent i = new Intent(LobbyPage.this, Game8.class);
            i.putExtra("email", email);
            i.putExtra("stage", stage);
            startActivity(i);
        });

    }
}