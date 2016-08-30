package com.haloproject.projectspartanv2.view;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import com.haloproject.projectspartanv2.R;

/**
 * Create a drawable with text.
 * Source: https://android.googlesource.com/platform/packages/apps/Camera/+/master/src/com/android/camera/drawable/TextDrawable.java
 */

public class TextDrawable extends Drawable {
    private static final int DEFAULT_COLOR_ID = R.color.HaloLightBlue;
    private static final int DEFAULT_TEXTSIZE_DIMEN = R.dimen.fragment_gun_ammo_text_size;
    private Paint mPaint;
    private CharSequence mText;
    private int mIntrinsicWidth;
    private int mIntrinsicHeight;

    public TextDrawable(Resources res, CharSequence text) {
        mText = text;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(res.getColor(DEFAULT_COLOR_ID));
        mPaint.setTextAlign(Align.CENTER);
//        float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
//                DEFAULT_TEXTSIZE_DIMEN, res.getDisplayMetrics());
        mPaint.setTextSize(res.getDimension(DEFAULT_TEXTSIZE_DIMEN));
        mIntrinsicWidth = (int) (mPaint.measureText(mText, 0, mText.length()) + .5);
        mIntrinsicHeight = mPaint.getFontMetricsInt(null);
    }
    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        canvas.drawText(mText, 0, mText.length(),
                bounds.centerX(), bounds.centerY(), mPaint);
    }
    @Override
    public int getOpacity() {
        return mPaint.getAlpha();
    }
    @Override
    public int getIntrinsicWidth() {
        return mIntrinsicWidth;
    }
    @Override
    public int getIntrinsicHeight() {
        return mIntrinsicHeight;
    }
    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }
    @Override
    public void setColorFilter(ColorFilter filter) {
        mPaint.setColorFilter(filter);
    }
}