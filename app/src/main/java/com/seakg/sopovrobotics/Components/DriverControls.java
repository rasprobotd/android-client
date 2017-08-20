package com.seakg.sopovrobotics.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class DriverControls extends android.support.v7.widget.AppCompatImageView {
    private static final String TAG = DriverControls.class.getSimpleName();
    private int mWidth = 300;
    private int mHeight = 300;
    private int mCursorX = -1;
    private int mCursorY = -1;
    private int mCursorXMax = 0;
    private int mCursorXMin = 0;
    private int mCursorYMax = 0;
    private int mCursorYMin = 0;
    private int mCursorXDiff = 0;
    private int mCursorYDiff = 0;
    private int mCursorR = -1;
    private Paint mPaintBorder = null;
    private Paint mPaintBackground = null;
    private Paint mPaintCursor = null;
    private Paint mPaintArrow = null;

    private boolean mActionDown = false;
    private Path mPathArrowTop = null;
    private Path mPathArrowLeft = null;
    private Path mPathArrowRight = null;
    private Path mPathArrowBottom = null;
    private String mLatestCommand = "";
    private DriverControlsListener mListener = null;

    public DriverControls(Context context) {
        super(context);
        init();
    }

    public DriverControls(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public DriverControls(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        mPaintBorder = new Paint();
        mPaintBorder.setColor(Color.BLACK);
        mPaintBorder.setStyle(Paint.Style.FILL);

        mPaintBackground = new Paint();
        mPaintBackground.setColor(Color.parseColor("#e4e46d"));
        mPaintBackground.setStyle(Paint.Style.FILL);

        mPaintCursor = new Paint();
        mPaintCursor.setColor(Color.parseColor("#e4a74b"));
        mPaintCursor.setStyle(Paint.Style.FILL);

        mPaintArrow = new Paint();
        mPaintArrow.setStrokeWidth(5);
        mPaintArrow.setColor(Color.parseColor("#e4a74b"));
        mPaintArrow.setStyle(Paint.Style.FILL_AND_STROKE);

        mPathArrowTop = new Path();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();// returns only the bar height (without the label);
        if(mWidth != 0 && mHeight != 0 && (mCursorX == -1 || mCursorY == -1)) {
            if (mCursorX == -1) {
                mCursorX = mWidth / 2;
                mCursorR = mWidth / 4; // 1 of 3 width will be cursor radius
                mCursorXMax = mWidth - mCursorR;
                mCursorXMin = mCursorR;
            }
            if (mCursorY == -1) {
                mCursorY = mHeight / 2;
                mCursorYMax = mHeight - mCursorR;
                mCursorYMin = mCursorR;
            }
            mPathArrowBottom = new Path();
            mPathArrowBottom.moveTo(mWidth/2 - 100,mHeight - 120);
            mPathArrowBottom.lineTo(mWidth/2,mHeight-20);
            mPathArrowBottom.lineTo(mWidth/2 + 100,mHeight - 120);
            mPathArrowBottom.lineTo(mWidth/2 - 100,mHeight - 120);
            mPathArrowBottom.close();

            mPathArrowTop = new Path();
            mPathArrowTop.moveTo(mWidth/2 - 100,120);
            mPathArrowTop.lineTo(mWidth/2,20);
            mPathArrowTop.lineTo(mWidth/2 + 100,120);
            mPathArrowTop.lineTo(mWidth/2 - 100,120);
            mPathArrowTop.close();

            mPathArrowLeft = new Path();
            mPathArrowLeft.moveTo(120,mHeight/2 -100);
            mPathArrowLeft.lineTo(20,mHeight/2);
            mPathArrowLeft.lineTo(120,mHeight/2 + 100);
            mPathArrowLeft.lineTo(120,mHeight/2 - 100);
            mPathArrowLeft.close();

            mPathArrowRight = new Path();
            mPathArrowRight.moveTo(mWidth - 120,mHeight/2 -100);
            mPathArrowRight.lineTo(mWidth - 20,mHeight/2);
            mPathArrowRight.lineTo(mWidth - 120,mHeight/2 + 100);
            mPathArrowRight.lineTo(mWidth - 120,mHeight/2 - 100);
            mPathArrowRight.close();
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mWidth/2, mHeight/2 , mWidth/2, mPaintBorder);
        canvas.drawCircle(mWidth/2, mHeight/2 , (mWidth/2)-10, mPaintBackground);

        canvas.drawPath(mPathArrowBottom, mPaintArrow);
        canvas.drawPath(mPathArrowTop, mPaintArrow);
        canvas.drawPath(mPathArrowLeft, mPaintArrow);
        canvas.drawPath(mPathArrowRight, mPaintArrow);

        canvas.drawCircle(mCursorX, mCursorY , mCursorR, mPaintBorder);
        canvas.drawCircle(mCursorX, mCursorY , mCursorR-10, mPaintCursor);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        int mev = event.getAction();
        if(mev == MotionEvent.ACTION_DOWN) {
            int min_x = Math.min(mCursorX, x);
            int max_x = Math.max(mCursorX, x);
            int min_y = Math.min(mCursorY, y);
            int max_y = Math.max(mCursorY, y);
            int x_len = Double.valueOf(Math.sqrt(max_x*max_x - min_x*min_x)).intValue();
            int y_len = Double.valueOf(Math.sqrt(max_y*max_y - min_y*min_y)).intValue();
            if(x_len < mCursorR*1.2 && y_len < mCursorR*1.2){
                mCursorXDiff = mCursorX - x;
                mCursorYDiff = mCursorY - y;
                mActionDown = true;
            }
            return true;
        }

        if(mev == MotionEvent.ACTION_MOVE) {
            if(mActionDown) {
                x += mCursorXDiff;
                y += mCursorYDiff;
                if(x > mCursorXMax){ x = mCursorXMax; }
                if(x < mCursorXMin){ x = mCursorXMin; }
                if(y > mCursorYMax){ y = mCursorYMax; }
                if(y < mCursorYMin){ y = mCursorYMin; }

                // forward or backward
                if(x > mWidth/2 - mCursorR/2 && x < mWidth/2 + mCursorR/2){
                    if(y > mHeight/2 + mCursorR/2) {
                        sendCommand("backward");
                    }else if(y < mHeight/2 - mCursorR/2){
                        sendCommand("forward");
                    }else {
                        sendCommand("stop");
                    }
                }else if(y > mHeight/2 - mCursorR/2 && y < mHeight/2 + mCursorR/2){
                    if(x < mWidth/2 - mCursorR/2) {
                        sendCommand("turnleft");
                    }else if(x > mWidth/2 + mCursorR/2){
                        sendCommand("turnright");
                    }else{
                        sendCommand("stop");
                    }
                }else{
                    sendCommand("stop");
                }
                mCursorX = x;
                mCursorY = y;
                invalidate();
            }
            // detect and call interface forward/left/right/back
            return true;
        }

        if(mev == MotionEvent.ACTION_UP || mev == MotionEvent.ACTION_OUTSIDE) {
            mActionDown = false;
            mCursorX = mWidth/2;
            mCursorY = mHeight/2;
            invalidate();
            sendCommand("stop");
            // call interface stop
            return true;
        }
        return false;
    }

    public void setListener(DriverControlsListener listener){
        mListener = listener;
    }

    private void sendCommand(String cmd){
        if(!mLatestCommand.equals(cmd)){
            Log.i(TAG, cmd);
            mLatestCommand = cmd;
            if(mListener != null){
                mListener.driveCommand(cmd);
            }
        }
    }
}
