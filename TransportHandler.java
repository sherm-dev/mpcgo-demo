package com.shermdev.will.mpcgo;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Will on 6/11/2017.
 */

public class TransportHandler extends Handler {
    public static final int BEAT_MESSAGE = 31111;
    public static final int CLICK_SOUND = 31112;
    public static final int CLICK_SILENCE = 31113;
    public static final int TIMER_UPDATE = 31114;

    private static final int CLICK_COLOR_ON = Color.RED;
    private static final int CLICK_COLOR_OFF = Color.TRANSPARENT;

    private TextView timerView;
    private ImageView clickLight;
    private TextView beatView;
    private ElapsedTimeUnit elapsedTimeUnit;
    private boolean clickLightOn;

    public TransportHandler(Looper looper, TextView timerView, ImageView clickLight, TextView beatView) {
        super(looper);
        this.timerView = timerView;
        this.clickLight = clickLight;
        this.beatView = beatView;
        elapsedTimeUnit = new ElapsedTimeUnit();
        clickLightOn = true;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch(msg.what){
            case ClickTrackTask.CLICK_NOTIFICATION_MSG:
                int beat = (int) msg.obj;
                if(beat == TimingTaskManager.SILENT_BEAT){
                    clickLightToggle();
                }else{
                    clickLightToggle();
                    post(new BeatRunnable(beatView, beat));
                    //updateBeatDisplay(beat);
                }
                break;
            case TIMER_UPDATE:
                post(new TimerRunnable(timerView, elapsedTimeUnit.incrementTime()));
                break;
            default:
                break;
        }
    }


    private void clickLightToggle(){
        if(clickLightOn){
            clickLightOn = false;
            post(new ClickLightRunnable(CLICK_COLOR_ON, clickLight));

        }else{
            clickLightOn = true;
            post(new ClickLightRunnable(CLICK_COLOR_OFF, clickLight));

        }
    }
}
