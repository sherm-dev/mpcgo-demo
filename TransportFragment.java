package com.shermdev.will.mpcgo;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;


import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class TransportFragment extends Fragment {

    public static final int PLAYBACK_BEGIN = 31115;
    public static final String PLAYBACK_TIME_UPDATE_KEY = "playback_time_update";
    private static final String TIMER_HANDLER_THREAD_NAME = "TIMER_ht_name";
    private TransportFragmentHandler fragmentHandler;
    private TextView beatCounterView;
    private TextView clockView;
    private ImageView clickIndicatorLight;


    private boolean clickLightOn;
    private boolean timerOn;

    private TransportTimeUpdateRunnable timeUpdateRunnable;
    private ElapsedTimeUnit elapsedTimeUnit;

    public TransportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment
     * */


    public static TransportFragment newInstance() {
        return new TransportFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clickLightOn = true;
        timerOn = false;
        elapsedTimeUnit = new ElapsedTimeUnit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transport, container, false);

        clickIndicatorLight = (ImageView) view.findViewById(R.id.click_indicator_light);
        clickIndicatorLight.setImageResource(R.drawable.click_indicator_light);
        clockView = (TextView) view.findViewById(R.id.transport_clock_view);
        clockView.setText("00:00:00"); //initial clock time (00:00:00 hh:mm:SS)
        beatCounterView = (TextView) view.findViewById(R.id.beat_counter_view);

        return view;
    }






    public void startTimer(){
        timerOn = true;
        incrementTimer();
    }

    public void stopTimer(){
        timerOn = false;
        if(timeUpdateRunnable != null) timeUpdateRunnable.timerOff();
    }

    private void incrementTimer(){
        HandlerThread handlerThread = new HandlerThread("FRAGMENT_HANDLER_THREAD");
        handlerThread.start();
        fragmentHandler = new TransportFragmentHandler(handlerThread.getLooper(),this);
        timeUpdateRunnable = new TransportTimeUpdateRunnable(fragmentHandler);
        new Thread(timeUpdateRunnable).start();
    }

    public void clickNotification(final int beat){
        if(beat == TimingTaskManager.SILENT_BEAT){
            clickLightToggle();
        }else{
            clickLightToggle();
            updateBeatDisplay(beat);
        }
    }

    /*public void beatNotification(final int beat){
       // clickLightToggle();
        updateBeatDisplay(beat);
    }*/

    /*public void updateTimeDisplay(final long playbackPosition){
        clockView.post(new Runnable() {
            @Override
            public void run() {
                clockView.setText(formatPlaybackTime(playbackPosition));
            }
        });
    }*/

    public void incrementTimeDisplay(){

      /*  clockView.post(new Runnable() {
            @Override
            public void run() {
                clockView.setText(elapsedTime.incrementTime());
            }
        });*/

    }

    private void updateBeatDisplay(final int beat){
        beatCounterView.post(new Runnable() {
            @Override
            public void run() {
                beatCounterView.setText(String.valueOf(beat));
            }
        });
    }

    private void clickLightToggle(){
        if(clickLightOn){
            clickLightOn = false;
            clickIndicatorLight.post(new Runnable() {
                @Override
                public void run() {
                    clickIndicatorLight.setBackgroundColor(Color.RED);
                }
            });

        }else{
            clickLightOn = true;
            clickIndicatorLight.post(new Runnable() {
                @Override
                public void run() {
                    clickIndicatorLight.setBackgroundColor(Color.TRANSPARENT);
                }
            });

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       /* if (context instanceof OnTransportUpdateListener) {
            transportCallback = (OnTransportUpdateListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTransportInteract");
        }*/
    }



    @Override
    public void onDetach() {
        super.onDetach();
        //transportCallback = null;
    }

    public TextView getClockView() {
        return clockView;
    }

    public ElapsedTimeUnit getElapsedTimeUnit() {
        return elapsedTimeUnit;
    }

    private static class TransportFragmentHandler extends Handler{
        private WeakReference<TransportFragment> wActRef;

        public TransportFragmentHandler(TransportFragment fragRef){
            wActRef = new WeakReference<TransportFragment>(fragRef);
        }

        public TransportFragmentHandler(Looper looper, TransportFragment fragRef) {
            super(looper);
            wActRef = new WeakReference<TransportFragment>(fragRef);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //final String time = (String) msg.obj;
            switch(msg.what){

                default:

                    break;
            }
        }
    }


}
