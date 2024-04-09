package com.example.touch;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnTouchListener {
    Button btnAddButton;
    boolean checked = false;
    int btnNum = 1;
    private int deltaX;
    private int deltaY;
    ViewGroup _root;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAddButton = (Button) findViewById(R.id.btnAdd);
        _root = (ViewGroup)findViewById(R.id.relative_layout);
        btnAddButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Button btn = new Button(MainActivity.this);
                //btn.setId(i);
                RelativeLayout.LayoutParams layoutParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParam.leftMargin = 500;
                btn.setText("Button " + btnNum);
                _root.addView(btn, layoutParam);
                btn.setOnTouchListener(MainActivity.this);
                btnNum++;
            }
        });
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
                if (checkOverlap((Button) v)) {
                    Toast.makeText(this, "Button Overlapped!", Toast.LENGTH_SHORT).show();
                    /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);*/
                    //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
                break;
        }

        _root.invalidate();
        return true;
    }

    private boolean checkOverlap(Button button) {
        for (int i = 0; i < _root.getChildCount(); i++) {
            View view = _root.getChildAt(i);
            if (view != button && view instanceof Button) { //change ID here?
                int[] firstPosition = new int[2];
                int[] secondPosition = new int[2];

                button.getLocationOnScreen(firstPosition);
                view.getLocationOnScreen(secondPosition);

                // Rect constructor parameters: left, top, right, bottom
                Rect rectButton = new Rect(firstPosition[0], firstPosition[1],
                        firstPosition[0] + button.getMeasuredWidth(), firstPosition[1] + button.getMeasuredHeight());
                Rect rectOtherButton = new Rect(secondPosition[0], secondPosition[1],
                        secondPosition[0] + view.getMeasuredWidth(), secondPosition[1] + view.getMeasuredHeight());

                if (rectButton.intersect(rectOtherButton)) {
                    return true;
                }
            }
        }
        return false;
    }
}