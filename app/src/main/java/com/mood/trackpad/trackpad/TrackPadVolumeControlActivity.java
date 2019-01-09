package com.mood.trackpad.trackpad;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class TrackPadVolumeControlActivity extends Activity implements TrackPadValueChangedListener {

    private TrackPadView trackPadView;
    private TextView xAxisTextView;
    private TextView yAxisTextView;

    private MediaPlayer mediaPlayer;

    private View leftContainerForVolumeIndication;
    private View rightContainerForVolumeIndication;

    private float currentVolumeLeft = 0.50f, currentVolumeRight = 0.50f; //initial value of the track pad x : 0.5, y : 0.5

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_pad);
        trackPadView = findViewById(R.id.activity_track_pad_trackPadView);

        xAxisTextView = findViewById(R.id.activity_track_pad_xAxisTextView);
        yAxisTextView = findViewById(R.id.activity_track_pad_yAxisTextView);

        leftContainerForVolumeIndication= findViewById(R.id.activity_track_pad_leftContainer);
        rightContainerForVolumeIndication= findViewById(R.id.activity_track_pad_rightContainer);

        trackPadView.setTrackPadValueChangedListener(this);
        mediaPlayer = MediaPlayer.create(this, R.raw.crowd);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.start(); // loop the audio
            }
        });

        mediaPlayer.start();
        mediaPlayer.setVolume(currentVolumeLeft,currentVolumeRight);

    }

    @Override
    public void onTrackPadValueChanged(double xAxisVal, double yAxisVal) {

        String xValue = "" + xAxisVal;
        xValue = xValue.substring(0, 3);

        String yValue = "" + yAxisVal;
        yValue = yValue.substring(0, 3);

        Log.d("xAxis : ",xValue);
        Log.d("yAxis : ",yValue);

        if (mediaPlayer != null && mediaPlayer.isPlaying()){

            mediaPlayer.setVolume((float)yAxisVal,(float) xAxisVal); //adjust new volume

            leftContainerForVolumeIndication.setAlpha((float)yAxisVal); //view volume indication
            rightContainerForVolumeIndication.setAlpha((float)xAxisVal); //view volume indication

            currentVolumeLeft = (float)yAxisVal;
            currentVolumeRight = (float) xAxisVal;
        }

        xAxisTextView.setText(getString(R.string.x_axis_title) + xValue);
        yAxisTextView.setText(getString(R.string.y_axis_title) + yValue);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mediaPlayer!=null && mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mediaPlayer!=null)
            mediaPlayer.start();
    }
}
