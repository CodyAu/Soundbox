package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class ViewPagerBottomSheetBehavior<V extends View> extends BottomSheetBehavior<V> {
    private static int BOTTOM_SHEET_SWIPE_RATION = 4;

    private ViewPager2 viewPager;
    private float initialX, initialY;

    public ViewPagerBottomSheetBehavior() {
        super();
    }

    public ViewPagerBottomSheetBehavior(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setViewPager(ViewPager2 viewPager) {
        this.viewPager = viewPager;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull V child, @NonNull MotionEvent event) {
        if (parent.isPointInChildBounds(viewPager, (int) event.getX(), (int) event.getY())) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = event.getX();
                    initialY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(Math.abs(event.getX() - initialX) * BOTTOM_SHEET_SWIPE_RATION < Math.abs(event.getY() - initialY))
                        return true;
                    break;
            }
        }
        return super.onInterceptTouchEvent(parent, child, event);
    }

}
