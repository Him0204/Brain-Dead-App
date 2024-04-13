package com.example.sehh3165_gp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Game2 extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    float xAxis;
    float yAxis;
    int lastAction;

    ImageButton home, sound, game_hint, game_reset, next;
    ImageButton cryingBaby, toy, toy2, toy3;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game2_baby);

        /*
        Bundle extras = getIntent().getExtras();
        email = extras != null ? extras.getString("Email") : null;
        */

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
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                xAxis = v.getX() - event.getRawX();
                yAxis = v.getY() - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
                break;
            case MotionEvent.ACTION_MOVE:
                v.setX(event.getRawX() + xAxis);
                v.setY(event.getRawY() + yAxis);
                lastAction = MotionEvent.ACTION_MOVE;
                break;
            case MotionEvent.ACTION_UP:
                if (lastAction == MotionEvent.ACTION_DOWN) {
                    // Set On Click Event
                    v.performClick();
                    Toast.makeText(this, "Btn Clicked", Toast.LENGTH_SHORT).show();
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
            Intent i = new Intent(Game2.this, LobbyPage.class);
            i.putExtra("Email", email);
            startActivity(i);
        }
        else if (v.getId() == R.id.imageButton_sound) {
            Toast.makeText(this, "Btn Clicked", Toast.LENGTH_SHORT).show();
        }
        else if (v.getId() == R.id.imageButton_hint) {
            Toast.makeText(Game2.this, "", Toast.LENGTH_SHORT).show();
        }
        else if (v.getId() == R.id.imageButton_reset) {
            Toast.makeText(this, "Btn Clicked", Toast.LENGTH_SHORT).show();
        }
        else if (v.getId() == R.id.imageButton_next) {
            Toast.makeText(this, "Btn Clicked", Toast.LENGTH_SHORT).show();
        }
    }


    /*
    private boolean isViewOverlapping(View firstView, View secondView) {
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
    */
}
