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


/**
 * TODO: document your custom view class.
 */
public class TopBar extends View {
    private Drawable mTopBar = getContext().getDrawable(R.drawable.top_bar);
    private boolean mBluetoothOn = false;
    private String mMenuName = "Hello World";

    private TextPaint mTextPaint;
    private float mTextWidth;
    private Paint mPaintOn;
    private Paint mPaintOff;

    public TopBar(Context context) {
        super(context);
        init(null, 0);
    }

    public TopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TopBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TopBar, defStyle, 0);

        if (a.hasValue(R.styleable.TopBar_menuName)) {
            mMenuName = a.getString(R.styleable.TopBar_menuName);
        }
        if (a.hasValue(R.styleable.TopBar_bluetoothOn)) {
            mBluetoothOn = a.getBoolean(R.styleable.TopBar_bluetoothOn, false);
        }

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setTextSize(100);
        mTextPaint.setColor(Color.argb(0xFF, 0x85, 0xAB, 0xCF));
        mTextWidth = mTextPaint.measureText("Bluetooth");

        mPaintOn = new Paint();
        mPaintOn.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaintOn.setColor(Color.argb(0xFF, 0x87, 0xBA, 0x52));

        mPaintOff = new Paint();
        mPaintOff.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaintOff.setColor(Color.argb(0xFF, 0xDB, 0x42, 0x42));
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

        mTopBar.setBounds(paddingLeft, paddingTop, paddingLeft + contentWidth, paddingTop + contentHeight);
        mTopBar.draw(canvas);

        // Draw the text.
        canvas.drawText(mMenuName, 50, 100, mTextPaint);
        //Bluetooth text
        canvas.drawText("Bluetooth", 1200, 130, mTextPaint);
        if (mBluetoothOn == true) {
            canvas.drawCircle(1260 + mTextWidth, 100, 35, mPaintOn);
        } else {
            canvas.drawCircle(1260 + mTextWidth, 100, 35, mPaintOff);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int widthSize = MeasureSpec.getMode(widthMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        int heightSize = MeasureSpec.getMode(heightMeasureSpec);

        int width = mTopBar.getIntrinsicWidth();
        int height = mTopBar.getIntrinsicHeight();

//        if (widthMode == MeasureSpec.UNSPECIFIED) {
//            width = mTopBar.getIntrinsicWidth();
//        } else {
//            width = widthSize;
//        }
//
//        if (heightMode == MeasureSpec.UNSPECIFIED) {
//            height = mTopBar.getIntrinsicHeight();
//        } else {
//            height = heightSize;
//        }

        setMeasuredDimension(width, height);
    }


}
