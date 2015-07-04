package com.haloproject.projectspartanv2.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.haloproject.projectspartanv2.R;

public class SettingsButton extends View {
    private String mText = "SETTINGS ITEM";
    private Drawable mDrawable = getContext().getDrawable(R.drawable.main_button);

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    public SettingsButton(Context context) {
        super(context);
        init(null, 0);
    }

    public SettingsButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SettingsButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.MainButton, defStyle, 0);

        if (a.hasValue(R.styleable.MainButton_Text)) {
            mText = a.getString(R.styleable.MainButton_Text);
        }

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(70);
        mTextPaint.setColor(getResources().getColor(R.color.HaloLightBlue));
        mTextWidth = mTextPaint.measureText(mText);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // Draw the drawable.
        mDrawable.setBounds(paddingLeft, paddingTop, paddingLeft + contentWidth, paddingTop + contentHeight);
        mDrawable.draw(canvas);

        // Draw the text.
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(mText, 0, mText.length() - 1, bounds);
        canvas.drawText(mText, getWidth() / 2 - bounds.centerX(), getHeight() / 2 - bounds.centerY(), mTextPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = mDrawable.getIntrinsicWidth();
        } else {
            width = widthSize;
        }
        if (heightMode == MeasureSpec.UNSPECIFIED) {
            height = mDrawable.getIntrinsicHeight();
        } else {
            height = heightSize;
        }

        setMeasuredDimension(width, height);
    }

    public String getString() {
        return mText;
    }

    public void setString(String string) {
        mText = string;
        invalidateTextPaintAndMeasurements();
    }
}
