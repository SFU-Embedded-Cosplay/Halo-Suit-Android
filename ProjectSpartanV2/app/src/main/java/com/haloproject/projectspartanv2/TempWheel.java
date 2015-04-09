package com.haloproject.projectspartanv2;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class TempWheel extends View {
    private double maxTemp = 40.0f;
    private double currTemp = 0.0f;
    private int haloBlue;
    private int haloRed;
    private RectF mRectF;


    private Paint mPaint;
    private Paint mStroke;
    private TextPaint mTextPaint;

    public TempWheel(Context context) {
        super(context);
        init(null, 0);
    }

    public TempWheel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TempWheel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TempWheel, defStyle, 0);

        if (a.hasValue(R.styleable.TempWheel_maxTemp)) {
            maxTemp = a.getFloat(R.styleable.TempWheel_maxTemp, 0.0f);
        }
        if (a.hasValue(R.styleable.TempWheel_currTemp)) {
            currTemp = a.getFloat(R.styleable.TempWheel_currTemp, 0.0f);
        }

        a.recycle();

        mRectF = new RectF(0, 0, getWidth(), getHeight());

        haloBlue = getResources().getColor(R.color.HaloLightBlue);
        haloRed = getResources().getColor(R.color.HaloHotRed);

        mPaint = new Paint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(haloBlue);

        mStroke = new Paint();
        mStroke.setFlags(Paint.ANTI_ALIAS_FLAG);
        mStroke.setStyle(Paint.Style.STROKE);
        mStroke.setStrokeWidth(40);
        mStroke.setShader(new SweepGradient(200, 200, new int[]{haloBlue, haloBlue, haloBlue, haloRed}, null));

        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(haloBlue);
        mTextPaint.setTextSize(100);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bmp = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        Canvas bmpCanvas = new Canvas(bmp);
        bmpCanvas.drawCircle(200, 200, 180, mStroke);

        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        double percentage = currTemp / maxTemp;
        bmpCanvas.drawArc(-1, -1, 401, 401, 90, 270 * (float) percentage - 360, true, mPaint);
        mPaint.setXfermode(null);
        canvas.drawBitmap(bmp, 0, 0, mPaint);

        String temperature = String.format("%4.1fC", currTemp);

        Rect bounds = new Rect();
        mTextPaint.getTextBounds(temperature, 0, temperature.length() - 1, bounds);

        canvas.drawText(temperature, 200 - bounds.centerX(), 200 - bounds.centerY(), mTextPaint);
    }

    public void setTemp(double temp) {
        if (temp > maxTemp) {
            currTemp = maxTemp;
        } else {
            currTemp = temp;
        }
        postInvalidate();
    }

    public double getTemp() {
        return currTemp;
    }

    public double getMaxTemp() {
        return maxTemp;
    }
}
