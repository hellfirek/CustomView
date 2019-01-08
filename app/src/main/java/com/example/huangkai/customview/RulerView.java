package com.example.huangkai.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewConfigurationCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;


public class RulerView extends View {
    public static final String TAG = "RulerView";
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
    private Paint centerPaint;
    private TextPaint tPaint;

    private static final int SHORTLINE = 50;
    private static final int LONGLINE = 100;

    private static final int CENTER_LINE = 150;
    private int TOUCH_SLOP = 0;
    private int MINI = 0;
    private int MAX = 0;
    private float lastX, lastY, downX;
    boolean isMoved = false;

    /**
     * 速度跟踪器
     */
    private VelocityTracker mVelocityTracker;

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
            currentValue = a.getFloat(R.styleable.RulerView_currentValue, 100f);
            unit = a.getFloat(R.styleable.RulerView_unit, 0.1f);
            unitSpace = a.getDimension(R.styleable.RulerView_unitSpace, dp2px(10));
            bgColor = a.getColor(R.styleable.RulerView_bgColor, Color.parseColor("#f5f8f5"));
            a.recycle();
        }
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(7);
        gradationColor = Color.BLUE;

        centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerPaint.setStrokeWidth(10);
        centerPaint.setColor(Color.GREEN);

        gradationColor = Color.BLUE;
        tPaint = new TextPaint();
        tPaint.setTextSize(dp2px(14));
        tPaint.setColor(Color.BLACK);

        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        TOUCH_SLOP = configuration.getScaledTouchSlop();
        MAX =  configuration.getScaledMaximumFlingVelocity();
        MINI =  configuration.getScaledMaximumFlingVelocity();
        changeValue();
    }

    private void changeValue() {
        miniNumber = miniVaule * 10;
        maxNumber = maxVaule * 10;
        currentNumber = currentValue * 10;
        unitNumber = unit * 10;

        //当前值到最左边的距离
        currentDistance = ((currentNumber - miniNumber) / unitNumber) * unitSpace;
        Log.i(TAG, "currentDistance = " + currentDistance);
        maxDistance = ((maxNumber - miniNumber) / unitNumber) * unitSpace;
        Log.i(TAG, "maxDistance = " + maxDistance);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getSize(true, widthMeasureSpec);
        int height = getSize(false, heightMeasureSpec);
        if (width >= 0) {
            halfWidth = width >> 1;
        }
        if (widthRange == -1) {
            widthRange = (width / unitSpace) * unit * 10;
            Log.i(TAG, "widthRange = " + widthRange);
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
                result = size;
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
            default:
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
        drawPointer(canvas);
        drawCenterPointer(canvas);

    }

    private void drawPointer(Canvas canvas) {
        //画顶上的线
        mPaint.setColor(gradationColor);
        mPaint.setStrokeWidth(7);
        canvas.drawLine(0, 1, mWidth, 1, mPaint);
        //计算最左边的值
        int startNumber = (int) ((currentDistance - halfWidth) / unitSpace + miniNumber);

        if (startNumber < miniNumber) {
            startNumber = (int) miniNumber;
        }
        Log.i(TAG, "startNumber = " + startNumber);
        //计算最右边的值
        int endNumber = (int) (startNumber + widthRange);
        if (endNumber > maxNumber) {
            endNumber = (int)maxNumber;
        }
        Log.i(TAG, "endNumber = " + endNumber);

        float startPosition = halfWidth - (currentDistance - (startNumber - miniNumber) / unitNumber * unitSpace);
        Log.i(TAG, "startPosition = " + startPosition);

        while (startNumber <= endNumber) {

            if (startNumber % 10 == 0) {
                mPaint.setStrokeWidth(7);
                canvas.drawLine(startPosition, 1, startPosition, 1 + LONGLINE, mPaint);
                String content = String.valueOf(startNumber/10);
                float textWidth = tPaint.measureText(content);
                canvas.drawText(content, (int) (startPosition - textWidth * 0.5f), 1 + LONGLINE + dp2px(14), tPaint);
            } else {
                mPaint.setStrokeWidth(3);
                canvas.drawLine(startPosition, 1, startPosition, 1 + SHORTLINE, mPaint);
            }

            startNumber += 1;
            startPosition += unitSpace;
        }
    }

    private void drawCenterPointer(Canvas canvas) {
        canvas.drawLine(halfWidth, 4, halfWidth, CENTER_LINE, centerPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if(mVelocityTracker == null){
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                downX = event.getX();

                break;
            case MotionEvent.ACTION_MOVE:
                int distanceX = (int) (x - lastX);
                int distanceY = (int) (y - lastY);
                if (Math.abs(distanceY) > Math.abs(distanceX) || Math.abs(x - downX) < TOUCH_SLOP) {
                    Log.i("hked", "" + (x - downX) + " TOUCH_SLOP = " + TOUCH_SLOP);
                    break;
                }
                currentDistance = currentDistance - distanceX;
                if(currentDistance<=miniVaule){
                    currentDistance = miniVaule;
                }
                if(currentDistance>=maxDistance){
                    currentDistance = maxDistance;
                }
                invalidate();

                break;
            case MotionEvent.ACTION_UP:
                Log.i("hked", "currentDistance = " + currentDistance);
                float up = (currentDistance / unitSpace) * unitNumber + miniNumber;
                int nearNumber = Math.round(up);
                currentDistance = (nearNumber - miniNumber) / unitNumber * unitSpace;
                invalidate();
                mVelocityTracker.computeCurrentVelocity(1000,MAX);
                float rate =  mVelocityTracker.getXVelocity();
                Log.i("hked","rate = "+rate);
                break;

            default:
                break;
        }
        lastX = x;
        lastY = y;
        return true;
    }
}
