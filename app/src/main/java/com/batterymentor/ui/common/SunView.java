package com.batterymentor.ui.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.batterymentor.R;
import com.batterymentor.constants.Constants;
import com.batterymentor.constants.UIConstants;
import com.batterymentor.ui.theme.Theme;
import com.batterymentor.ui.theme.ThemeManager;

/**
 * Class containing a sun and rays that is used to show a graphic for the screen brightness.
 */
public class SunView extends ImageView {

    /**
     * The rays drawable.
     */
    private Drawable mSunDrawable;

    /**
     * The sun drawable padding.
     */
    private int mSunPadding;

    /**
     * The paint used for drawing the fill background.
     */
    private Paint mPaint;

    /**
     * The brightness as a percent.
     */
    private double mBrightness;

    /**
     * The rectangle used for hiding the rays.
     */
    private RectF mRaysRect;

    /**
     * The start angle.
     */
    private int mStartAngle;

    /**
     * The sweep angle.
     */
    private int mSweepAngle;

    public SunView(Context context) {
        super(context);
        initialize();
    }

    public SunView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public SunView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    /**
     * Initialize the resources used for drawing the sun.
     */
    private void initialize() {
        mSunDrawable = ContextCompat.getDrawable(getContext(), R.drawable.screen_sun_blue);
        mSunPadding = (int) getResources().getDimension(R.dimen.sun_view_padding);
        mRaysRect = new RectF();
        mPaint = new Paint();
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.background));
        mPaint.setStyle(Paint.Style.FILL);
        applyTheme(ThemeManager.getInstance().getCurrentTheme(getContext()));
    }

    /**
     * When the size is changed, adjust the rectangle.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRaysRect.set(0, 0, w, h);
        mSunDrawable.setBounds(mSunPadding, mSunPadding, w - mSunPadding, h - mSunPadding);
    }

    /**
     * Override the draw method to draw the sun and hide the rays.
     */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(mRaysRect, mStartAngle, mSweepAngle, true, mPaint);
        mSunDrawable.draw(canvas);
    }

    public void setBrightness(double brightness) {
        mBrightness = brightness;
        mStartAngle = (int)(UIConstants.SUN_VIEW_ORIGIN_ANGLE + mBrightness * Constants.CIRCLE_MAX_ANGLE / Constants.PERCENT);
        if (mStartAngle >= UIConstants.SUN_VIEW_MAX_ANGLE_THRESHOLD) {
            mSweepAngle = 0;
        } else {
            mSweepAngle = (int)(UIConstants.SUN_VIEW_ORIGIN_ANGLE + Constants.CIRCLE_MAX_ANGLE + UIConstants.SUN_VIEW_OFFSET_ANGLE - mStartAngle);
        }
        postInvalidate();
    }

    public void applyTheme(Theme theme) {
        mSunDrawable = ContextCompat.getDrawable(getContext(), theme.getScreenSunResource());
        mSunDrawable.setBounds(mSunPadding, mSunPadding, getWidth() - mSunPadding, getHeight() - mSunPadding);
        setImageDrawable(ContextCompat.getDrawable(getContext(), theme.getScreenRaysResource()));
    }
}
