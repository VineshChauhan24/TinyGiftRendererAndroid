package com.myvideoyun.TinyGiftRenderer.png.tool;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import java.io.IOException;
import java.io.InputStream;

public class PngSurfaceView extends SurfaceView implements Runnable, SurfaceHolder.Callback {

    private SurfaceHolder mHolder;

    private boolean bRunning;

    private int mCurrentPos;

    private String[] mFrames;

    public PngSurfaceView(Context context) {
        super(context);
        init();
    }

    public PngSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PngSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        //保证surfaceview在window最上层
        setZOrderOnTop(true);
        //使窗口支持透明度
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
    }

    public void startAnim(String[] anim) {
        if (bRunning) {
            return;
        }
        mFrames = anim;
        resize();
        // 推荐使用线程池
        new Thread(this).start();
        bRunning = true;
    }

    @Override
    public void run() {
        while (bRunning) {
            drawBitmap();
            mCurrentPos++;
            try {
                Thread.sleep(33);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void drawBitmap() {
        //获取画布并锁定
        Canvas mCanvas = mHolder.lockCanvas();

        if (mCanvas  == null) {
            return;
        }

        //绘制透明色
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        AssetManager asm = getContext().getAssets();
        InputStream is= null;//name:图片的名称
        try {
            is = asm.open(mFrames[mCurrentPos % mFrames.length]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap mBitmap=BitmapFactory.decodeStream(is);

        Paint paint = new Paint();
        Rect mSrcRect = new Rect(0,
                0,
                mBitmap.getWidth(),
                mBitmap.getHeight()); // 图片绘制
        Rect mDestRect = new Rect(0,
                0,
                getWidth(),
                getHeight());//  图片绘制位置

        mCanvas.drawBitmap(mBitmap, mSrcRect, mDestRect, paint);
        //解锁画布，并展示bitmap到surface
        mHolder.unlockCanvasAndPost(mCanvas);
        mBitmap.recycle();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        bRunning = false;
        // 动画执行完毕，清空画面
        Canvas mCanvas = mHolder.lockCanvas();
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCanvas.drawPaint(paint);
        // 解锁画布
        mHolder.unlockCanvasAndPost(mCanvas);
    }

    /**
     * 重置控件尺寸
     */
    public void resize() {

        ViewGroup.LayoutParams params = getLayoutParams();


        AssetManager asm = getContext().getAssets();
        InputStream is= null;//name:图片的名称
        try {
            is = asm.open(mFrames[mCurrentPos % mFrames.length]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap mBitmap=BitmapFactory.decodeStream(is);

        float scaleBmp = mBitmap.getWidth() * 1.0f / mBitmap.getHeight();
        float scale = getWidth() * 1.0f / getHeight();
        if (scale > scaleBmp) {
            params.height = getHeight();
            params.width = (int) (params.height * scaleBmp);
        } else if (scale < scaleBmp) {
            params.width = getWidth();
            params.height = (int) (params.width / scaleBmp);
        }
        setLayoutParams(params);
    }
}