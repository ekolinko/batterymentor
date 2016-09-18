package com.batterymentor.ui.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.batterymentor.R;
import com.batterymentor.constants.UIConstants;
import com.batterymentor.datamanager.Histogram;
import com.batterymentor.datamanager.HistogramPoint;
import com.batterymentor.datamanager.Point;
import com.batterymentor.ui.theme.Theme;
import com.batterymentor.ui.theme.ThemeManager;
import com.batterymentor.utils.PolynomialSplineFunction;
import com.batterymentor.utils.SplineInterpolator;

/**
 * Class containing a curved line that goes through a preset set of points.
 */
public class HistogramView extends View {

    /**
     * The paint used for drawing the histogram.
     */
    private Paint mPaint;

    /**
     * The paint used for drawing the background.
     */
    private Paint mBackgroundPaint;

    /**
     * The paint used for drawing the border.
     */
    private Paint mBorderPaint;

    /**
     * The stroke color.
     */
    private int mStrokeColor;

    /**
     * The fill color
     */
    private int mFillColor;

    /**
     * The padding of the histogram.
     */
    private float mPadding;

    /**
     * The histogram associated with this view.
     */
    private Histogram mHistogram;

    public HistogramView(Context context) {
        super(context);
        initialize();
    }

    public HistogramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public HistogramView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    /**
     * Initialize the resources used for drawing the sun.
     */
    private void initialize() {
        mPaint = new Paint();
        mStrokeColor = ContextCompat.getColor(getContext(), R.color.material_blue);
        mFillColor = ContextCompat.getColor(getContext(), R.color.material_blue_semitransparent);
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.background));
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBorderPaint = new Paint();
        mBorderPaint.setColor(ContextCompat.getColor(getContext(), R.color.gray));
        mBorderPaint.setStrokeWidth(getResources().getDimension(R.dimen.histogram_view_border_width));
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mPadding = getResources().getDimension(R.dimen.histogram_view_padding);
        applyTheme(ThemeManager.getInstance().getCurrentTheme(getContext()));
    }

    /**
     * Override the draw method to draw the histogram.
     */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPaint(mBackgroundPaint);
        if (mHistogram != null) {
            drawHistogram(canvas, mHistogram.getHistogramData());
        }
        canvas.drawRect(mPadding, 0, getWidth() - mPadding, getHeight(), mBorderPaint);
    }

    /**
     * Draw the histogram using the specified set of points.
     */
    public void drawHistogram(Canvas canvas, HistogramPoint[] points) {
        if (points == null || points.length <= 0)
            return;

        int numPoints = points.length;

        HistogramPoint firstPoint = points[0];
        float minX = (float) firstPoint.getMinX();
        float maxX = (float) firstPoint.getMaxX();
        float minY = (float) firstPoint.getY();
        float maxY = (float) firstPoint.getY();
        for (int i = 1; i < numPoints; i++) {
            HistogramPoint point = points[i];
            if (point.getMinX() < minX)
                minX = (float) point.getMinX();
            if (point.getMaxX() > maxX)
                maxX = (float) point.getMaxX();
            if (point.getY() < minY)
                minY = (float) point.getY();
            if (point.getY() > maxY)
                maxY = (float) point.getY();
        }
        maxY *= UIConstants.HISTOGRAM_Y_AXIS_MAX_BUFFER;

        float rangeX = maxX - minX;
        float rangeY = maxY - minY;

        int width = getWidth() - (int)(mPadding*2);
        int height = getHeight();

        double[] xs = new double[numPoints];
        double[] ys = new double[numPoints];
        for (int i = 0; i < numPoints; i++) {
            PointF point = convertPoint(points[i], minX, minY, width, height, rangeX, rangeY);
            xs[i] = point.x;
            ys[i] = point.y;
        }

        float diff = width / (float) UIConstants.HISTOGRAM_NUM_POINTS_FOR_DRAW;
        PolynomialSplineFunction spline = new SplineInterpolator().interpolate(xs, ys);
        Path path = new Path();
        path.moveTo(mPadding, (float) (height - spline.value(0)));
        float x = 0;
        while (x <= width) {
            path.lineTo(x + mPadding, (float) (height - spline.value(x)));
            x += diff;
        }
        path.lineTo(width + mPadding, height);
        path.lineTo(mPadding, height);
        path.lineTo(mPadding, maxY);
        mPaint.setColor(mFillColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, mPaint);
        mPaint.setColor(mStrokeColor);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, mPaint);
    }

    private PointF convertPoint(Point point, float minX, float minY, float width, float height, float rangeX, float rangeY) {
        float x = (float) (point.getX() - minX) * width / rangeX;
        float y = (float) (point.getY() - minY) * height / rangeY;
        return new PointF(x, y);
    }

    public void applyTheme(Theme theme) {
        mStrokeColor = ContextCompat.getColor(getContext(), theme.getColorResource());
        mFillColor = ContextCompat.getColor(getContext(), theme.getSemitransparentColorResource());
    }

    public void setHistogram(Histogram histogram) {
        mHistogram = histogram;
    }
}
