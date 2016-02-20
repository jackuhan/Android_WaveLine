package com.example.wxy.customwaveview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


// y=Asin(ωx+φ)+k
class Wave extends View {

    private final float WAVE_LENGTH = 0.5f;

    private final float WAVE_HZ = 0.02f;

    public final int DEFAULT_ABOVE_WAVE_ALPHA = 100;
    public final int DEFAULT_BLOW_WAVE_ALPHA = 100;

    private final float X_SPACE = 20;
    private final double PI2 = 2 * Math.PI;

    private Path mAboveWavePath = new Path();
    private Path mBlowWavePath = new Path();
    List<Path> paths = null;

    private Paint mAboveWavePaint = new Paint();
    private Paint mBlowWavePaint = new Paint();

    private int mAboveWaveColor;
    private int mBlowWaveColor;

    private float mWaveMultiple;
    private float mWaveLength;
    private int mWaveHeight;
    private float mMaxRight;
    private float mWaveHz;

    private float mAboveOffset = 0.0f;
    private float mBlowOffset;
    private boolean mWaveLineMulti;

    private RefreshProgressRunnable mRefreshProgressRunnable;

    private int left, right, bottom;
    // ω
    private double omega;

    public Wave(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.waveViewStyle);

    }

    public Wave(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawPath(mBlowWavePath, mBlowWavePaint);

        if (mWaveLineMulti) {
            for (int i = 0; i < paths.size(); i++) {
                canvas.drawPath(paths.get(i), mAboveWavePaint);
            }
        } else {
            canvas.drawPath(mAboveWavePath, mAboveWavePaint);
        }
    }

    public void setAboveWaveColor(int aboveWaveColor) {
        this.mAboveWaveColor = aboveWaveColor;
    }

    public void setBelowWaveColor(int blowWaveColor) {
        this.mBlowWaveColor = blowWaveColor;
    }


    public void initializeWaveSize(int waveMultiple, int waveHeight, int waveHz, boolean WaveLineMulti) {
        mWaveMultiple = getWaveMultiple(waveMultiple);
        mWaveHeight = waveHeight;
        mWaveHz = getWaveHz(waveHz);
        mBlowOffset = mWaveHeight * 2.0f;
        mWaveLineMulti = WaveLineMulti;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mWaveHeight * 2);
        setLayoutParams(params);
        if (mWaveLineMulti) {
            paths = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                paths.add(new Path());
            }
        }
    }

    public void initPaint() {
        mAboveWavePaint.setColor(mAboveWaveColor);
        mAboveWavePaint.setAlpha(255);
        mAboveWavePaint.setStyle(Paint.Style.STROKE);//决定是线还是填充FILL
        mAboveWavePaint.setAntiAlias(true);//抗锯齿

        mBlowWavePaint.setColor(mBlowWaveColor);
        mBlowWavePaint.setAlpha(255);
        mBlowWavePaint.setStyle(Paint.Style.STROKE);
        mBlowWavePaint.setAntiAlias(true);
    }

    private float getWaveMultiple(int size) {
        return WAVE_LENGTH * size;
    }

    private float getWaveHz(int size) {
        return WAVE_HZ * size;
    }

    /**
     * calculate wave track
     */
    private void calculatePath() {
        mAboveWavePath.reset();
        mBlowWavePath.reset();
        if (mWaveLineMulti) {
            for (int i = 0; i < paths.size(); i++) {
                paths.get(i).reset();
            }
        }

        getWaveOffset();

        float y;
        mAboveWavePath.moveTo(left, bottom);
        for (float x = 0; x <= mMaxRight; x += X_SPACE) {
            y = (float) (mWaveHeight * Math.sin(omega * x + mAboveOffset + Math.PI) + mWaveHeight);//todo 计算高度
            mAboveWavePath.lineTo(x, y);
            if (mWaveLineMulti) {
                for (int n = 1; n <= paths.size(); n++) {
                    double yyy = Math.abs(y - mWaveHeight) * Math.cos(2 * 3 * n);
                    double sin;
                    if (y > mWaveHeight) {
                        sin = mWaveHeight + yyy * Math.cos(2 * 3 * n);
                    } else {
                        sin = mWaveHeight - yyy * Math.cos(2 * 3 * n);
                    }

                    paths.get(n - 1).lineTo(x, (float) sin);
                }
            }
        }
        mAboveWavePath.lineTo(right, bottom);
        if (mWaveLineMulti) {
            for (int n = 1; n <= paths.size(); n++) {
                paths.get(n - 1).lineTo(right, bottom);
            }
        }


        mBlowWavePath.moveTo(left, bottom);
        for (float x = 0; x <= mMaxRight; x += X_SPACE) {
            y = (float) (mWaveHeight * Math.sin(omega * x + mAboveOffset) + mWaveHeight);
//            y = mWaveHeight;
            mBlowWavePath.lineTo(x, y);
        }
        mBlowWavePath.lineTo(right, bottom);
    }

    public void showView(boolean show) {
        if (!show) {
            removeCallbacks(mRefreshProgressRunnable);
        } else {
            removeCallbacks(mRefreshProgressRunnable);
            mRefreshProgressRunnable = new RefreshProgressRunnable();
            post(mRefreshProgressRunnable);
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (View.GONE == visibility) {
            removeCallbacks(mRefreshProgressRunnable);
        } else {
            removeCallbacks(mRefreshProgressRunnable);
            mRefreshProgressRunnable = new RefreshProgressRunnable();
            post(mRefreshProgressRunnable);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            if (mWaveLength == 0) {
                startWave();
            }
        }
    }

    public void startWave() {
        if (getWidth() != 0) {
            int width = getWidth();
            mWaveLength = width * mWaveMultiple;
            left = getLeft();
            right = getRight();
            bottom = getBottom() + 2;
            mMaxRight = right + X_SPACE;
            omega = PI2 / mWaveLength;
        }
    }

    private void getWaveOffset() {
        if (mBlowOffset > Float.MAX_VALUE - 100) {
            mBlowOffset = 0;
        } else {
            mBlowOffset += mWaveHz;
        }

        if (mAboveOffset > Float.MAX_VALUE - 100) {
            mAboveOffset = 0;
        } else {
            mAboveOffset += mWaveHz;
        }
    }

    private class RefreshProgressRunnable implements Runnable {
        public void run() {
            synchronized (Wave.this) {
                long start = System.currentTimeMillis();
                calculatePath();
                invalidate();
                long gap = 16 - (System.currentTimeMillis() - start);
                postDelayed(this, gap < 0 ? 0 : gap);
            }
        }
    }


}
