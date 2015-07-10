package com.haloproject.projectspartanv2.view;

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

import com.haloproject.projectspartanv2.R;

public class TempWheel extends View {
    private double maxTemp = 40.0;
    private double minTemp = 0.0;
    private double currTemp = 0.0;
    private int haloLightBlue;
    private int haloDarkBlue;
    private int haloHotRed;
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
        if (a.hasValue(R.styleable.TempWheel_currTemp)) {
            minTemp = a.getFloat(R.styleable.TempWheel_minTemp, 0.0f);
        }

        a.recycle();

        mRectF = new RectF(0, 0, getWidth(), getHeight());

        haloLightBlue = getResources().getColor(R.color.HaloLightBlue);
        haloDarkBlue = getResources().getColor(R.color.HaloDarkBlue);
        haloHotRed = getResources().getColor(R.color.HaloHotRed);

        mPaint = new Paint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(haloLightBlue);

        mStroke = new Paint();
        mStroke.setFlags(Paint.ANTI_ALIAS_FLAG);
        mStroke.setStyle(Paint.Style.STROKE);
        mStroke.setStrokeWidth(40);
        mStroke.setShader(new SweepGradient(200, 200, new int[]{haloDarkBlue, haloDarkBlue, haloLightBlue, haloHotRed}, null));

        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(haloLightBlue);
        mTextPaint.setTextSize(100);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //create progress bar
        Bitmap bmp = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        Canvas bmpCanvas = new Canvas(bmp);
        bmpCanvas.drawCircle(200, 200, 180, mStroke);

        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        double percentage = (currTemp - minTemp) / maxTemp;
        if (percentage >= 1.0) {
            bmpCanvas.drawArc(-1, -1, 401, 401, 90, -90, true, mPaint);
        } else {
            bmpCanvas.drawArc(-1, -1, 401, 401, 90, 270 * (float) percentage - 360, true, mPaint);
        }
        mPaint.setXfermode(null);
        canvas.drawBitmap(bmp, 0, 0, mPaint);

        String temperature = String.format("%4.1fC", currTemp);

        Rect bounds = new Rect();
        mTextPaint.getTextBounds(temperature, 0, temperature.length() - 1, bounds);

        canvas.drawText(temperature, 200 - bounds.centerX() - 40, 200 - bounds.centerY(), mTextPaint);
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
