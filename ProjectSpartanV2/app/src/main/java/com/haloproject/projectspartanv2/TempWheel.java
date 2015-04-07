package com.haloproject.projectspartanv2;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class TempWheel extends View {
    private float maxTemp = 40.0f;
    private float currTemp = 0.0f;

    private Paint mPaint;

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

        mPaint = new Paint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(getResources().getColor(R.color.HaloLightBlue));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(0, 0, getWidth(), getHeight(), 180, 180 * currTemp / maxTemp, true, mPaint);

    }

    public void setTemp(float temp) {
        if (temp > maxTemp) {
            currTemp = maxTemp;
        } else {
            currTemp = temp;
        }
        postInvalidate();
    }

    public float getTemp() {
        return currTemp;
    }
}
