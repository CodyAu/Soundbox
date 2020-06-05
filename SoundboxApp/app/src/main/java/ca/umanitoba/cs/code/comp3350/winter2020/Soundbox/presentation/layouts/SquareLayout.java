package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SquareLayout extends LinearLayout {

    public SquareLayout(Context context) {
        super(context);
    }

    public SquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = width > height ? width : height;

        setMeasuredDimension(size, size); // make it square
    }

}
