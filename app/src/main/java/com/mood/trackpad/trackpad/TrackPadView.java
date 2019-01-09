package com.mood.trackpad.trackpad;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class TrackPadView extends RelativeLayout {

    public TrackPadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateAndInitializeTrackPadView(context);
    }

    private double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public void setTrackPadValueChangedListener(TrackPadValueChangedListener trackPadValueChangedListener) {

        this.trackPadValueChangedListener = trackPadValueChangedListener;

        if (draggableCircle == null)
            return;

        trackPadValueChangedListener.onTrackPadValueChanged(0.5,0.5);

    }

    public TrackPadValueChangedListener getTrackPadValueChangedListener() {
        return trackPadValueChangedListener;
    }

    private TrackPadValueChangedListener trackPadValueChangedListener;

    private View draggableCircle, horizontalLine, verticalLine;
    float dX, dY, dXVerticalLine, dYHorizontalLine;

    public void inflateAndInitializeTrackPadView(Context context) {
        View layout = inflate(context, R.layout.view_track_pad, this);
        draggableCircle = layout.findViewById(R.id.view_track_pad_draggableCircle);
        horizontalLine = layout.findViewById(R.id.view_track_pad_horizontalLine);
        verticalLine = layout.findViewById(R.id.view_track_pad_verticalLine);


        draggableCircle.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();

                        dXVerticalLine = verticalLine.getX() - event.getRawX();
                        dYHorizontalLine = horizontalLine.getY() - event.getRawY();

                        break;

                    case MotionEvent.ACTION_MOVE:

                        animateCircleInsideContainer(event.getRawX() + dX, event.getRawY() + dY, view.getWidth(), view.getHeight(), TrackPadView.this, view);

                        float verticalLineXValueAfterCircleAnimation = view.getX() + (view.getWidth() / 2) - (verticalLine.getWidth() / 2);
                        verticalLine.animate()
                                .x(verticalLineXValueAfterCircleAnimation)
                                .setDuration(0)
                                .start();

                        float horizontalLineYValueAfterCircleAnimation = view.getY() + (view.getHeight() / 2) - (horizontalLine.getHeight() / 2);

                        horizontalLine.animate()
                                .y(horizontalLineYValueAfterCircleAnimation)
                                .setDuration(0)
                                .start();

                        if (trackPadValueChangedListener != null) {

                            double xAxisValue = view.getTranslationX() / TrackPadView.this.getWidth(); //x axis value relative to center


                            xAxisValue = xAxisValue + 0.5f; //relative to left side of the container

                            double yAxisValue = view.getTranslationY() / TrackPadView.this.getHeight();//y axis value relative to center

                            yAxisValue = yAxisValue - 0.5f;//relative to bottom of the container
                            yAxisValue = Math.abs(yAxisValue);

                            //rounding
                            xAxisValue = round(xAxisValue,2);
                            yAxisValue = round(yAxisValue,2);

                            trackPadValueChangedListener.onTrackPadValueChanged(xAxisValue, yAxisValue);

                        }

                        break;
                    default:
                        return false;
                }
                return true;
            }

        });
    }

    private void animateCircleInsideContainer(float suggestedX, float suggestedY, float objWidth, float objHeight, View vContainer, View circleToBeAnimated) {

        if (vContainer == null) {
            return;
        }

        objWidth = objWidth / 2; //width from circle's center
        objHeight = objHeight / 2; //height from circle's center

        float minXValue = objWidth * -1;
        float maxXValue = vContainer.getWidth() - objWidth;

        float minYValue = objHeight * -1;
        float maxYValue = vContainer.getHeight() - objHeight;


        //adjust suggestedX to min,max X boundaries
        if (suggestedX < minXValue) {
            suggestedX = minXValue;
        } else if (suggestedX > maxXValue) {
            suggestedX = maxXValue;
        }

        //adjust suggestedY to min,max Y boundaries
        if (suggestedY < minYValue) {
            suggestedY = minYValue;
        } else if (suggestedY > maxYValue) {
            suggestedY = maxYValue;
        }

        //animate/move the circle
        circleToBeAnimated.animate()
                .x(suggestedX)
                .y(suggestedY)
                .setDuration(0)
                .start();

    }


}
