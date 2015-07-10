package com.haloproject.projectspartanv2.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.haloproject.projectspartanv2.R;

public class BatteryBar extends View {
    private Drawable emptyBattery = getContext().getDrawable(R.drawable.empty_battery);
    private int batteryCharge;

    private TextPaint mTextPaint;
    private Rect mTextBounds;

    private Paint mPaint;

    public BatteryBar(Context context) {
        super(context);
        init(null, 0);
    }

    public BatteryBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BatteryBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.BatteryBar, defStyle, 0);

        if (a.hasValue(R.styleable.BatteryBar_batteryCharge)) {
            batteryCharge = a.getInt(R.styleable.BatteryBar_batteryCharge, 0);
            setBatteryCharge(batteryCharge);
        }


        a.recycle();

        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(getResources().getColor(R.color.HaloLightBlue));
        mTextPaint.setTextSize(80);
        mTextBounds = new Rect();

        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.HaloLightBlue));

        emptyBattery.setBounds(30, 130, 450, 700);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String charge = String.format("%d%%", batteryCharge);
        mTextPaint.getTextBounds(charge, 0, charge.length(), mTextBounds);
        canvas.drawText(charge, 240 - mTextBounds.centerX(), 100, mTextPaint);
        float percentage = (float) batteryCharge / 100.0f;
        canvas.drawRect(64, 655 - percentage * 440, 407, 655, mPaint);
        emptyBattery.draw(canvas);
    }

    public void setBatteryCharge(int charge) {
        if (charge >= 100) {
            batteryCharge = 100;
        } else if (charge <= 0) {
            batteryCharge = 0;
        } else {
            batteryCharge = charge;
        }
        postInvalidate();
    }
}
