package com.example.huangkai.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class RulerView extends View {

    float halfWidth = 0;

    float miniNumber;
    float maxNumber;
    float currentNumber;
    float unitNumber;

    float miniVaule;
    float currentValue;
    float maxVaule;
    float unit;
    float unitSpace;

    float currentDistance;
    float maxDistance;
    float widthRange = -1;

    private int bgColor;
    private int mWidth;
    private int gradationColor;
    /**
     * 普通画笔
     */
    private Paint mPaint;

    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RulerView);
            miniVaule = a.getFloat(R.styleable.RulerView_miniValue, 0f);
            maxVaule = a.getFloat(R.styleable.RulerView_maxValue, 100f);
            currentValue = a.getFloat(R.styleable.RulerView_currentValue, 50f);
            unit = a.getFloat(R.styleable.RulerView_unit, 50f);
            unitSpace = a.getDimension(R.styleable.RulerView_unitSpace, dp2px(10));
            bgColor = a.getColor(R.styleable.RulerView_bgColor, Color.parseColor("#f5f8f5"));
            a.recycle();
        }
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(3);
        gradationColor = Color.BLUE;
    }

    private void changeValue() {
        miniNumber = miniVaule * 10;
        maxNumber = maxVaule * 10;
        currentNumber = currentValue * 10;
        unitNumber = unit * 10;

        //当前值到最左边的距离
        currentDistance = ((currentNumber - miniNumber) / unitNumber) * unitSpace;
        maxDistance = (maxNumber - miniNumber) / unitNumber;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getSize(true, widthMeasureSpec);
        int height = getSize(false, heightMeasureSpec);
        if (width >= 0) {
            halfWidth = width >> 1;
        }
        if (widthRange != -1) {
            widthRange = (width / unitSpace) * unit;
        }
        mWidth = width;
        setMeasuredDimension(width, height);
    }

    private int getSize(boolean isWidth, int spec) {
        int mode = MeasureSpec.getMode(spec);
        int size = MeasureSpec.getSize(spec);

        int result = 0;

        switch (mode) {
            //如果是指定大小或者mathParent
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.AT_MOST:
                if (!isWidth) {
                    //默认的高度
                    int defalutHeight = dp2px(80);
                    result = Math.min(defalutHeight, size);
                }
                break;
            case MeasureSpec.UNSPECIFIED:
                result = dp2px(80);
                break;
        }
        return result;
    }

    private int dp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(bgColor);


    }

    private void drawPointer(Canvas canvas) {
        //画顶上的线
        mPaint.setColor(gradationColor);
        canvas.drawLine(0, 2, mWidth, 2, mPaint);
        //计算最左边的值
       int startNumber =  (int)((currentDistance - halfWidth)/unitSpace+miniNumber);
        //计算最右边的值
       int endNumber  = (int)(startNumber + widthRange);


    }
}
