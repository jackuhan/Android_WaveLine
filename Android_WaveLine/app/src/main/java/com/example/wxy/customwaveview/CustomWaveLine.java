package com.example.wxy.customwaveview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;


/**
 * Created by hanjh on 2014/10/15.
 */
public class CustomWaveLine extends LinearLayout {

    private int mAboveWaveColor;
    private int mBlowWaveColor;
    private int mWaveHeight;
    private int mWaveLength;
    private int mWaveHz;
    private boolean mWaveLineMulti;
    private boolean mBelowLineShow;

    private Wave mWave;
    private Context context;

    private final int DEFAULT_ABOVE_WAVE_COLOR = Color.parseColor("#f5f5f5");
    private final int DEFAULT_BLOW_WAVE_COLOR = Color.parseColor("#0086d0");
    private int waveHeight = 0;
    private CustomWaveLine waveView;


    public CustomWaveLine(Context context) {
        super(context);
    }

    public CustomWaveLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        this.context = context;
        waveView = this;
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomWaveLine, 0, 0);
        mAboveWaveColor = attributes.getColor(R.styleable.CustomWaveLine_above_wave_color, DEFAULT_ABOVE_WAVE_COLOR);
        mBlowWaveColor = attributes.getColor(R.styleable.CustomWaveLine_blow_wave_color, DEFAULT_BLOW_WAVE_COLOR);
        mWaveHeight = attributes.getInt(R.styleable.CustomWaveLine_wave_height, 50);
        mWaveLength = attributes.getInt(R.styleable.CustomWaveLine_wave_length, 2);
        mWaveHz = attributes.getInt(R.styleable.CustomWaveLine_wave_hz, 10);
        mWaveLineMulti = attributes.getBoolean(R.styleable.CustomWaveLine_above_line_multi, false);
        mBelowLineShow = attributes.getBoolean(R.styleable.CustomWaveLine_below_line_show, false);
        attributes.recycle();

        mWave = new Wave(context, null);
        mWave.initializeWaveSize(mWaveLength, mWaveHeight, mWaveHz, mWaveLineMulti,mBelowLineShow);
        mWave.setAboveWaveColor(mAboveWaveColor);
        mWave.setBelowWaveColor(mBlowWaveColor);
        mWave.initPaint();

        addView(mWave);
        waveHeight = mWave.getHeight();
    }

    public void setAboveWaveColor(int intColor) {
        mWave.setAboveWaveColor(intColor);
    }

    public void setBlowWaveColor(int intColor) {
        mWave.setBelowWaveColor(intColor);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    public void showView(boolean show) {
        mWave.showView(show);
    }

    public void setHeight(int height) {
        mWave.initializeWaveSize(mWaveLength, height, mWaveHz, mWaveLineMulti,mBelowLineShow);
        mWave.initPaint();
        invalidate();
    }


}
