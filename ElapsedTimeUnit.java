package com.shermdev.will.mpcgo;

/**
 * Created by Will on 4/18/2017.
 */

public class ElapsedTimeUnit {
    private int minute, tenMinute;
    private int second, tenSecond;
    private int milliSec, hundredthSec, tenthSec;

    public ElapsedTimeUnit(){
        this.minute = 0;
        this.tenMinute = 0;
        this.second = 0;
        this.tenSecond = 0;
        this.milliSec = 0;
        this.hundredthSec = 0;
        this.tenthSec = 0;
    }

    public String incrementTime(){
        if(milliSec < 9){
            milliSec++;
        }else{
            milliSec = 0;
            if(hundredthSec < 9){
                hundredthSec++;
            }else{
                hundredthSec = 0;
                if(tenthSec < 9){
                    tenthSec++;
                }else{
                    tenthSec = 0;
                    if(second < 9){
                        second++;
                    }else{
                        second = 0;
                        if(tenSecond < 5){
                            tenSecond++;
                        }else{
                            tenSecond = 0;
                            if(minute < 9){
                                minute++;
                            }else{
                                minute = 0;
                                tenMinute++;
                            }
                        }
                    }
                }
            }
        }

        return getStringValue();
        /*
        if(milliSec < 990){
            milliSec += 10;
        }else{
            milliSec = 0;
            if(second < 59){
                second++;
            }else{
                second = 0;
                minute++;
            }
        }*/
    }

    public String getStringValue(){
        //String timeString = null;
        /*if(String.valueOf(minute).length() == 1){
            timeString = String.valueOf(0).concat(String.valueOf(minute));
        }else{
            timeString = String.valueOf(minute);
        }

        timeString = timeString.concat(":");

        if(String.valueOf(second).length() == 1){
            timeString = timeString.concat(String.valueOf(0)).concat(String.valueOf(second));
        }else{
            timeString = timeString.concat(String.valueOf(second));
        }

        timeString = timeString.concat(".");

        if(String.valueOf(milliSec).length() == 2){
            timeString = timeString.concat(String.valueOf(0)).concat(String.valueOf(milliSec));
        }else{
            timeString = timeString.concat(String.valueOf(milliSec));
        }*/
        return String.valueOf(tenMinute).concat(String.valueOf(minute)).concat(":")
                .concat(String.valueOf(tenSecond)).concat(String.valueOf(second)).concat(".")
                .concat(String.valueOf(tenthSec)).concat(String.valueOf(hundredthSec)).concat(String.valueOf(milliSec));
    }
}
