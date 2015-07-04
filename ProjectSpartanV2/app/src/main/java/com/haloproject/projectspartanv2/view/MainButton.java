package com.haloproject.projectspartanv2.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.haloproject.projectspartanv2.R;

public class MainButton extends View {
    private String mText = "MENU ITEM";
    private Drawable mDrawable = getContext().getDrawable(R.drawable.main_button);

    private Drawable mIcon = null;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    public MainButton(Context context) {
        super(context);
        init(null, 0);
    }

    public MainButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MainButton(Context context, AttributeSet attrs, int defStyle) {
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
        if (a.hasValue(R.styleable.MainButton_Icon)) {
            mIcon = a.getDrawable(R.styleable.MainButton_Icon);
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

        if (mIcon != null) {
            int iconX = getWidth() / 2 - mIcon.getIntrinsicWidth() / 2;
            int iconY = getHeight() / 2 - mIcon.getIntrinsicHeight() / 2 + 30;
            mIcon.setBounds(iconX, iconY, iconX + mIcon.getIntrinsicWidth(), iconY + mIcon.getIntrinsicHeight());
            mIcon.draw(canvas);
        }
        // Draw the text.
        canvas.drawText(mText, 50, 100, mTextPaint);
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

    public Drawable getIcon() {
        return mIcon;
    }

    public void setIcon(Drawable icon) {
        mIcon = icon;
    }
}
