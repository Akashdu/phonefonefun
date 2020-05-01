package com.akash.phonefonefun;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;


import com.quaap.phonefonefun.R;

import java.util.Locale;

/**
 * Created by tom on 12/18/16.
 * <p>
 * Copyright (C) 2016  tom kliethermes
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

public class TextToVoice implements TextToSpeech.OnInitListener {
    private TextToSpeech mTts = null;
    private Context mContext;
    private boolean isInit = false;

    private float mPitch = .8f;
    private float mSpeed = .6f;
    private int utterid = 0;
    private VoiceReadyListener mFil;
    private boolean fullyInited;

    private boolean speaking = false;

    public TextToVoice(Context context) {
        mContext = context;
        try {
            Log.d("TextToVoice", "started " + System.currentTimeMillis());
            mTts = new TextToSpeech(mContext, this);

            setPitch(mPitch);
            setSpeed(mSpeed);
            mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String s) {

                }

                @Override
                public void onDone(String s) {
                    speaking = false;
                    //Log.d("TextToSpeech", "Done!" +  System.currentTimeMillis());
                    if (!fullyInited) {
                        fullyInited = true;
                        if (mFil != null) mFil.onVoiceReady(TextToVoice.this);
                    } else {
                        if (mFil != null) mFil.onSpeakComplete(TextToVoice.this);
                    }
                }

                @Override
                public void onError(String s) {
                    speaking = false;
                    Log.e("TextToSpeech", "Error with " + s);
                    if (mFil != null) mFil.onError(TextToVoice.this);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = mTts.setLanguage(Locale.getDefault());
            isInit = true;

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {

                Log.e("error", "This Language is not supported");
            }
            Log.d("TextToSpeech", "Initialization Suceeded! " + System.currentTimeMillis());

            speak(mContext.getString(R.string.hello1) + ",");
        } else {
            Log.e("error", "Initialization Failed! " + status);
        }
    }

    public void shutDown() {
        isInit = false;
        fullyInited = false;
        if (mTts != null) {
            mTts.shutdown();
            mTts = null;
        }
    }

    public void speak(String text) {
        // Log.d("TextToSpeech", text + " " +  System.currentTimeMillis());
        if (isInit) {
            if (Build.VERSION.SDK_INT >= 21) {
                mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utt" + utterid);
            } else {
                mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
            utterid++;
            speaking = true;
        } else {
            Log.e("error", "TTS Not Initialized");
        }
    }

    public void stop() {
        if (fullyInited) {
            mTts.stop();
        }
    }

    public boolean isSpeaking() {
        return speaking;
    }

    public float getPitch() {
        return mPitch;
    }

    public void setPitch(float pitch) {
        mPitch = pitch;
        mTts.setPitch(pitch);
    }

    public float getSpeed() {
        return mSpeed;
    }

    public void setSpeed(float speed) {
        mSpeed = speed;
        mTts.setSpeechRate(speed);
    }

    public boolean isReady() {
        return fullyInited;
    }

    public void setVoiceReadyListener(VoiceReadyListener fil) {
        mFil = fil;
    }


    public interface VoiceReadyListener {
        void onVoiceReady(TextToVoice ttv);

        void onSpeakComplete(TextToVoice ttv);

        void onError(TextToVoice ttv);
    }
}
