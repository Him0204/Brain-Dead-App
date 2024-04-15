package com.example.sehh3165_gp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class LobbyPage extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby);
        Bundle extras = getIntent().getExtras();
        String email = extras != null ? extras.getString("Email") : null;
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        String[] infoRtn = new String[5];

        //List<List<String>> str;
        //String[] allInfoRtn = new String[1000][3];

        String player_username = dbHelper.getInfo(email, 0);
        TextView username = findViewById(R.id.hello);
        username.setText("Hello! " + player_username);

        Button scoreboard = (Button) findViewById(R.id.button_scoreboard);
        scoreboard.setOnClickListener(v -> {
            Intent i = new Intent(LobbyPage.this, ScoreboardPage.class);
            i.putExtra("email", email);
            i.putExtra("username", player_username);
            startActivity(i);
        });

        Button button_continue = (Button) findViewById(R.id.button_continue);
        button_continue.setOnClickListener(v -> {

        });
    }
}