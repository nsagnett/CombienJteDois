package nsapp.com.combienjtedois.listeners;

import android.view.MotionEvent;
import android.view.View;

public class SwipeListener implements View.OnTouchListener {

    public static enum Action {
        LR, // Left to Right
        RL, // Right to Left
        None // when no action was detected
    }

    private static final int MIN_DISTANCE = 100;
    private float downX;
    private Action mSwipeDetected = Action.None;

    public boolean swipeDetected() {
        return mSwipeDetected != Action.None;
    }

    public Action getAction() {
        return mSwipeDetected;
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                mSwipeDetected = Action.None;
                return false;
            }
            case MotionEvent.ACTION_MOVE: {
                float upX = event.getX();

                float deltaX = downX - upX;

                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    if (deltaX < 0) {
                        mSwipeDetected = Action.LR;
                        return true;
                    }
                    if (deltaX > 0) {
                        mSwipeDetected = Action.RL;
                        return true;
                    }
                }
            }
        }
        return false;
    }
}