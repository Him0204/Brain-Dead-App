package com.example.sehh3165_gp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;
import java.util.ArrayList;
import java.util.List;

public class Game6 extends AppCompatActivity implements View.OnTouchListener {
    private int deltaX, deltaY;
    private String email;
    private ViewGroup _root;
    private ImageView dumbbell, fat_guy, slim_guy, janitor, vacuum, protein, vacuum_for_guy;
    private RelativeLayout janitor_dead;
    private final List<ImageView> draggableItems = new ArrayList<>();
    private final List<ImageView> interactionTargets = new ArrayList<>();
    private Handler handler;
    private Boolean first_overlap;
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game6_gym);
        initializeViews();
        configureDraggable();
        email = getIntent().getStringExtra("email");
    }

    private void initializeViews() {
        _root = findViewById(R.id.relative_layout);
        ViewGroup gym_layout = findViewById(R.id.gym_layout);
        dumbbell = findViewById(R.id.image_dumbbell);
        fat_guy = findViewById(R.id.fat_guy);
        slim_guy = findViewById(R.id.slim_guy);
        protein = findViewById(R.id.protein);
        janitor = findViewById(R.id.janitor);
        vacuum = findViewById(R.id.vacuum);
        vacuum_for_guy = findViewById(R.id.vacuum_for_guy);
        janitor_dead = findViewById(R.id.janitor_dead);
        first_overlap = false;
    }

    private void configureDraggable() {
        draggableItems.add(dumbbell);
        draggableItems.add(vacuum);
        dumbbell.setOnTouchListener(this);
        protein.setOnTouchListener(this);
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
                moveViewWithTouch(v, X, Y);
                break;
            case MotionEvent.ACTION_UP:
                handleActionUp(v);
                break;
        }
        _root.invalidate();
        return true;
    }

    private void moveViewWithTouch(View v, int X, int Y) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
        layoutParams.leftMargin = X - deltaX;
        layoutParams.topMargin = Y - deltaY;
        v.setLayoutParams(layoutParams);
    }

    private void handleActionUp(View v) {
        if (checkOverlap((ImageView) v, draggableItems)) {
            handleFirstOverlap();
        } else if (first_overlap && checkOverlap((ImageView) v, interactionTargets)) {
            handleSecondOverlap();
        }
    }

    private boolean checkOverlap(ImageView button, List<ImageView> targets) {
        Rect rectButton = getRectOfView(button);
        for (ImageView target : targets) {
            if (button != target && rectButton.intersect(getRectOfView(target))) {
                return true;
            }
        }
        return false;
    }

    private Rect getRectOfView(View view) {
        int[] position = new int[2];
        view.getLocationOnScreen(position);
        return new Rect(position[0], position[1],
                position[0] + view.getWidth(), position[1] + view.getHeight());
    }

    private void handleFirstOverlap() {
        janitor.setVisibility(View.GONE);
        vacuum.setVisibility(View.GONE);
        janitor_dead.setVisibility(View.VISIBLE);
        vacuum_for_guy.setVisibility(View.VISIBLE);

        interactionTargets.add(fat_guy);
        interactionTargets.add(vacuum_for_guy);
        vacuum_for_guy.setOnTouchListener(this);
        first_overlap = true;
    }

    private void handleSecondOverlap() {
        slim_guy.setVisibility(View.VISIBLE);
        vacuum_for_guy.setVisibility(View.GONE);
        fat_guy.setVisibility(View.GONE);
        vacuum.setVisibility(View.VISIBLE);
        showCompletionPopup();
    }

    private void showCompletionPopup() {
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
        progress_menu.setVisibility(View.VISIBLE);
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        int time_taken = 0;
        dbHelper.updateStatus(email, "5", String.valueOf(time_taken));
        ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(progress_menu, "alpha", 0f, 1f);
        fadeInAnimator.setDuration(1000);
        fadeInAnimator.start();
    }
}
