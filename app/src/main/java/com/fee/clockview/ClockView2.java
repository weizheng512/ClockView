package com.fee.clockview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Calendar;

/**
 * =========================
 * <br/>Created by weizheng on  2017/4/18
 * <br/>email: weiz@mobilereality.org
 * <br/>Version：1.0
 * <br/>description:
 * 继承自 SurfaceView 的时钟控件
 * <br/>
 * =========================
 */

public class ClockView2 extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mSurfaceHolder;
    private RenderThread mRenderThread;
    //绘制开关
    private boolean isDraw = false;

    private Canvas canvas = null;
    private Canvas canvas1 = null;
    private Bitmap bitmapCache = null;

    //画笔
    private Paint mPaint;
    //时 分 秒
    private int h, m, s;
    //每一个刻度之间间隔的值 固定为6°
    private float fixAngle;
    //指针露出一部分的长度
    private int len;
    private Rect mTextBound;
    //大刻度长度
    private int b_len;
    //小刻度长度
    private int s_len;
    //刻度盘半径
    private int radius;

    public ClockView2(Context context) {
        this(context, null);
    }

    public ClockView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);
        mRenderThread = new RenderThread();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        fixAngle = 6;

        len = 30;
        b_len = 50;
        s_len = 30;
        radius = 300;
        mTextBound = new Rect();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isDraw = true;
        mRenderThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isDraw = false;
    }

    private class RenderThread extends Thread {
        @Override
        public void run() {
            while (isDraw) {
                drawUI();
            }
            super.run();
        }
    }

    private void drawUI() {
        try {
            canvas = mSurfaceHolder.lockCanvas();
            //缓存一个图层
            if (bitmapCache == null) {
                bitmapCache = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_4444);
            }
            canvas1 = new Canvas(bitmapCache);
            canvas1.drawColor(Color.parseColor("#d0d9ff"));
            drawCanvas(canvas1);
            canvas.drawBitmap(bitmapCache, 0, 0, mPaint);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void drawCanvas(Canvas canvas) {
        if (canvas == null) return;
        //画刻度盘
        drawScale(canvas);
        //画指针
        drawPointer(canvas);
        //画原点
        drawPoint(canvas);

    }

    private void drawPoint(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(0, 0, 10, mPaint);
    }

    private void drawPointer(Canvas canvas) {
        h = Calendar.getInstance().get(Calendar.HOUR);
        m = Calendar.getInstance().get(Calendar.MINUTE);
        s = Calendar.getInstance().get(Calendar.SECOND);

        //保存画布状态
        canvas.save();

        drawH(canvas);
        drawM(canvas);
        drawS(canvas);
    }

    /**
     * 画秒针
     *
     * @param canvas
     */
    private void drawS(Canvas canvas) {

        double angle = (s * fixAngle - 90) * Math.PI / 180;
        int mSecondLen = 280;
        int startX = -(int) (len * Math.cos(angle));
        int startY = -(int) (len * Math.sin(angle));

        int endX = (int) ((mSecondLen - len) * Math.cos(angle));
        int endY = (int) ((mSecondLen - len) * Math.sin(angle));

        mPaint.setStrokeWidth(3);
        mPaint.setColor(Color.RED);
        canvas.drawLine(startX, startY, endX, endY, mPaint);

    }

    /**
     * 画分针
     *
     * @param canvas
     */
    private void drawM(Canvas canvas) {
        double angle = (m * 6 - 90) * Math.PI / 180;
        int mMinLen = 240;
        int startX = -(int) (len * Math.cos(angle));
        int startY = -(int) (len * Math.sin(angle));

        int endX = (int) ((mMinLen - len) * Math.cos(angle));
        int endY = (int) ((mMinLen - len) * Math.sin(angle));
        mPaint.setStrokeWidth(5);
        mPaint.setColor(Color.YELLOW);
        canvas.drawLine(startX, startY, endX, endY, mPaint);
    }

    /**
     * 画时针
     *
     * @param canvas
     */
    private void drawH(Canvas canvas) {

        double angle = (h * 6 * 5 - 90) * Math.PI / 180 + ((m * 1.0f / 60 * 30) * Math.PI / 180);
        int mHourLen = 200;
        int startX = -(int) (len * Math.cos(angle));
        int startY = -(int) (len * Math.sin(angle));

        int endX = (int) ((mHourLen - len) * Math.cos(angle));
        int endY = (int) ((mHourLen - len) * Math.sin(angle));
        mPaint.setColor(Color.MAGENTA);
        mPaint.setStrokeWidth(8);
        canvas.drawLine(startX, startY, endX, endY, mPaint);
    }

    /**
     * 画刻度盘  通过画直线然后再旋转画布实现
     *
     * @param canvas
     */
    private void drawScale(Canvas canvas) {

        canvas.translate(getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        int currentP = 0;
        String text = "";
        int l;
        mPaint.setStrokeWidth(4);

        for (int i = 0; i < 360 / fixAngle; i++) {
            if (i % 5 == 0) {
                if (currentP == 0) {
                    text = "12";
                } else {
                    text = currentP + "";
                }
                currentP++;
                mPaint.setColor(Color.BLUE);
                mPaint.setTextSize(40);
                mPaint.getTextBounds(text, 0, text.length(), mTextBound);
                //绘制时间刻度值
                canvas.drawText(text, 0, text.length(), -mTextBound.width() / 2, -(radius + b_len + 20), mPaint);

                mPaint.setColor(Color.RED);
                l = b_len;

            } else {
                mPaint.setColor(Color.GRAY);
                l = s_len;
            }
            canvas.drawLine(0, -radius, 0, -(radius + l), mPaint);
            canvas.rotate(fixAngle);
        }
    }
}
