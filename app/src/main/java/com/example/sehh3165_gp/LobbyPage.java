package com.example.sehh3165_gp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LobbyPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby);

        DatabaseHelper DB = new DatabaseHelper(getApplicationContext());

        TextView username = findViewById(R.id.username);
        username.setText(DB.getInfo(passcontent);

        Button scoreboard = (Button) findViewById(R.id.button_scoreboard);
        scoreboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LobbyPage.this, ScoreboardPage.class);
                //i.putExtra("Username", username);
                startActivity(i);
            }
        });
    }
}