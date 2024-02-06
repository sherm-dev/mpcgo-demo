package com.shermdev.will.mpcgo;

import android.media.SoundPool;

/**
 * Created by Will on 11/23/2019.
 */

public interface SampleLoadListener extends SoundPool.OnLoadCompleteListener{
    void onSampleLoad(Sample sample);
}
