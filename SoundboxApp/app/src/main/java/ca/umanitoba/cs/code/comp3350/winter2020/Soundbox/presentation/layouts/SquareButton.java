package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.layouts;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.R;

public class SquareButton extends AppCompatButton {
    private static final float DEFAULT_SQUARE_SCALE_FIT = 1.0f;

    private float squareScaleFit;

    public SquareButton(Context context) {
        super(context);

        initAttributes(context, null);
    }

    public SquareButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        initAttributes(context, attrs);
    }

    public SquareButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttributes(context, attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        if (attrs == null) {
            squareScaleFit = DEFAULT_SQUARE_SCALE_FIT;
            return;
        }

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SquareButton);

        squareScaleFit = array.getFloat(R.styleable.SquareButton_squareScaleFit, DEFAULT_SQUARE_SCALE_FIT);
        squareScaleFit = Math.min(Math.max(squareScaleFit, 0.0f), 1.0f); //clamp the value between 0 and 1

        array.recycle();
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = width > height ? height : width;
        size = (int) (squareScaleFit * size);

        setMeasuredDimension(size, size); // make it square
    }

}
