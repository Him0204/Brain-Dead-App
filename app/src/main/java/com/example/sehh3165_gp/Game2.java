package com.example.sehh3165_gp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Game2 extends AppCompatActivity implements View.OnClickListener {

    ImageButton home, sound, game_hint, game_reset, next;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game1_emptyCup);

        Bundle extras = getIntent().getExtras();
        email = extras != null ? extras.getString("Email") : null;

        home = findViewById(R.id.imageButton_home);
        sound = findViewById(R.id.imageButton_sound);
        game_hint = findViewById(R.id.imageButton_hint);
        game_reset = findViewById(R.id.imageButton_reset);
        next = findViewById(R.id.imageButton_next);
        home.setOnClickListener(this);
        sound.setOnClickListener(this);
        game_hint.setOnClickListener(this);
        game_reset.setOnClickListener(this);
        next.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageButton_home) {
            Intent i = new Intent(Game2.this, LobbyPage.class);
            i.putExtra("Email", email);
            startActivity(i);
        }
        else if (v.getId() == R.id.imageButton_sound) {
            pass
        }
        else if (v.getId() == R.id.imageButton_hint) {
            Toast.makeText(Game2.this, "", Toast.LENGTH_SHORT).show();
        }
        else if (v.getId() == R.id.imageButton_reset) {
            pass
        }
        else if (v.getId() == R.id.imageButton_next) {
            pass
        }
    }
}
