package com.example.sehh3165_gp;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Game2 extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private int deltaX;
    private int deltaY;

    ImageButton home, sound, game_hint, game_reset, next;
    ImageButton cryingBaby, toy, toy2, toy3;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game1_cup);

        //Bundle extras = getIntent().getExtras();
        //email = extras != null ? extras.getString("Email") : null;

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

        cryingBaby = findViewById(R.id.imageButton_crying_baby);
        toy = findViewById(R.id.imageButton_toy);
        toy2 = findViewById(R.id.imageButton_toy2);
        toy3 = findViewById(R.id.imageButton_toy3);
        toy.setOnTouchListener(this);
        toy2.setOnTouchListener(this);
        toy3.setOnTouchListener(this);
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                deltaX = X - lParams.leftMargin;
                deltaY = Y - lParams.topMargin;
                break;
            case MotionEvent.ACTION_MOVE:
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                layoutParams.leftMargin = X - deltaX;
                layoutParams.topMargin = Y - deltaY;
                v.setLayoutParams(layoutParams);
                break;

            case MotionEvent.ACTION_UP:
                if (isOverlap(v, cryingBaby)) {
                    Toast.makeText(this, "Button Overlapped!", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        return true;
    }

    private boolean isOverlap(View view1, View view2) {
        Rect rect1 = new Rect();
        Rect rect2 = new Rect();

        view1.getHitRect(rect1);
        view2.getHitRect(rect2);

        return Rect.intersects(rect1, rect2);
    }
}
