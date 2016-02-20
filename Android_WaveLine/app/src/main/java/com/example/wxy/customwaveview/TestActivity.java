package com.example.wxy.customwaveview;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

/**
 * Created by hanjh on 2016/2/1.
 */
public class TestActivity extends Activity {
    Button bt, bt1;
    CustomWaveLine customWaveLine;
    private Handler mHandler;
    int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        bt = (Button) findViewById(R.id.bt);
        bt1 = (Button) findViewById(R.id.bt1);
        customWaveLine = (CustomWaveLine) findViewById(R.id.cwv);
        mHandler = new Handler();

        if (mMediaRecorder == null)
            mMediaRecorder = new MediaRecorder();

            /* ②setAudioSource/setVedioSource */
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
            /*
             * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
             * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
             */
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            /* ③准备 */
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "hello.log");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mMediaRecorder.setOutputFile(file.getAbsolutePath());
        mMediaRecorder.setMaxDuration(1000 * 60 * 10);
        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /* ④开始 */
        try {
            mMediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                customWaveLine.setAboveWaveColor(getResources().getColor(R.color.color1));
                customWaveLine.setBlowWaveColor(getResources().getColor(R.color.color2));
                customWaveLine.setVisibility(View.VISIBLE);
                customWaveLine.setHeight(0);
                customWaveLine.getLayoutParams().height = 0;
                customWaveLine.showView(true);
                if (height != 0)
                    mHandler.removeCallbacks(stop);
                height = 0;
                mHandler.postDelayed(start, 5);
            }
        });

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeCallbacks(start);
                mHandler.postDelayed(stop, 5);
            }
        });
    }

    private Runnable stop = new Runnable() {
        @Override
        public void run() {
            if (height >= 0) {
                height-=5;
            }
            customWaveLine.getLayoutParams().height = height * 2;
            customWaveLine.setHeight(height);
            mHandler.postDelayed(stop, 100);
            if (height == 0) {
                customWaveLine.setHeight(0);
                customWaveLine.getLayoutParams().height = 0;
                customWaveLine.showView(false);
            }
        }
    };

    private MediaRecorder mMediaRecorder;
    private Runnable start = new Runnable() {
        @Override
        public void run() {
//            if (height < 50) {
//                height+=4;
//            }
            if (height >= 0) {
                height-=1;
            }
            double ratio = (double) mMediaRecorder.getMaxAmplitude() / 100;
            double db = 25;// 分贝
            if (ratio > 1)
                db = 30 * Math.log10(ratio);
            height = (int) (db);

            Log.d("hanjh",""+db);
            customWaveLine.getLayoutParams().height = height * 2;
            customWaveLine.setHeight(height);
//            customWaveLine.setHeight((int) (db));
            mHandler.postDelayed(start, 100);
        }
    };

    @Override
    protected void onDestroy() {
        mMediaRecorder.stop();
        super.onDestroy();
    }

}
