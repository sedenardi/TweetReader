package com.sandersdenardi.tweetreader;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class TweetsFragment extends Fragment
    implements TextToSpeech.OnInitListener  {

    private TextToSpeech tts;
    private boolean ttsStatus = false;
    private OnTweetSelectedListener listener;
    private ListView tweetList;
    private final Handler speechHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tweetList.setSelection(msg.what);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        tweetList = (ListView) rootView.findViewById(R.id.tweetListView);
        ArrayList<String> array = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            array.add("Tweet " + (i + 1));
        }
        TweetArrayAdapter adapter = new TweetArrayAdapter(getActivity(),array);
        tweetList.setAdapter(adapter);
        tweetList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String s = (String) adapterView.getItemAtPosition(i);
                Logger.Log("Tweet clicked: " + s);
                speak(i);
            }
        });
        tweetList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String s = (String) adapterView.getItemAtPosition(i);
                Logger.Log("Tweet long pressed: " + s);
                clearAndStart(i);
                return true;
            }
        });

        tts = new TextToSpeech(TweetsFragment.this.getActivity(), this);
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener()
        {
            @Override
            public void onDone(String utteranceId) {
                Logger.Log("TTS DONE: " + utteranceId);
            }

            @Override
            public void onError(String utteranceId) {
                Logger.Log("TTS ERROR: " + utteranceId);
            }

            @Override
            public void onStart(String utteranceId) {
                Logger.Log("TTS START: " + utteranceId);
                Message m = Message.obtain(speechHandler,Integer.parseInt(utteranceId));
                m.sendToTarget();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof OnTweetSelectedListener) {
            listener = (OnTweetSelectedListener) activity;
        } else {
            throw new ClassCastException(activity.toString() +
                " must implement OnTweetSelectedListener");
        }
        super.onAttach(activity);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Logger.Log("TTS - This Language is not supported");
            } else {
                ttsStatus = true;
            }
        } else {
            Logger.Log("TTS - Initilization Failed!");
        }
    }

    private void speak(int pos) {
        if (ttsStatus) {
            String s = (String) tweetList.getItemAtPosition(pos);
            HashMap<String,String> map = new HashMap<String,String>();
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,String.valueOf(pos));
            int speakSuccess = tts.speak(s, TextToSpeech.QUEUE_ADD, map);
            if (speakSuccess == TextToSpeech.SUCCESS) {
                listener.onTweetSelected(s);
            } else {
                Logger.Log("Couldn't speak, error adding tweet to queue");
            }
        } else {
            Logger.Log("Couldn't speak, not initialized");
        }
    }

    private void clearAndStart(int pos) {
        if (ttsStatus) {
            int speakSuccess = tts.stop();
            if (speakSuccess == TextToSpeech.SUCCESS) {
                Logger.Log("Adding tweets to queue from: " + pos);
                for (int i = pos; i >= 0; i--){
                    String s = (String) tweetList.getAdapter().getItem(i);
                    HashMap<String,String> map = new HashMap<String,String>();
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,String.valueOf(i));
                    tts.speak(s, TextToSpeech.QUEUE_ADD, map);
                }
                Logger.Log("Done adding tweets to queue");
                listener.onTweetSelected("Reading from: " +
                    tweetList.getAdapter().getItem(pos));
            } else {
                Logger.Log("Couldn't stop speaking");
            }
        } else {
            Logger.Log("Couldn't speak, not initialized");
        }
    }

    public interface OnTweetSelectedListener {
        public void onTweetSelected(String s);
    }
}