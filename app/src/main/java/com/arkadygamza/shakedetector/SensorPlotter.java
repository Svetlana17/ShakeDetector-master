package com.arkadygamza.shakedetector;

import android.graphics.Color;
import android.hardware.SensorEvent;
import android.support.annotation.NonNull;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import rx.Observable;
import rx.Subscription;

/**
 * Draws graph of sensor events
 */
public class SensorPlotter {
    public static final int MAX_DATA_POINTS = 50;//max число точек
    public static final int VIEWPORT_SECONDS = 5; //число тсчтеов в секунду
    public static final int FPS = 10;

    @NonNull
    private final String mName;

    private final long mStart = System.currentTimeMillis();

    public final LineGraphSeries<DataPoint> mSeriesX;
    protected final LineGraphSeries<DataPoint> mSeriesY;
    protected final LineGraphSeries<DataPoint> mSeriesZ;
    private final Observable<SensorEvent> mSensorEventObservable;
    private long mLastUpdated = mStart;
    private Subscription mSubscription;
    public static boolean flag=true;
    GraphView graphView;
    public SensorPlotter(@NonNull String name, @NonNull  GraphView graphView,
                         @NonNull Observable<SensorEvent> sensorEventObservable) {
        mName = name;
        mSensorEventObservable = sensorEventObservable;
          this.graphView=graphView;
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(VIEWPORT_SECONDS * 1000); // number of ms in viewport

        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(-20);
        graphView.getViewport().setMaxY(20);

        graphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graphView.getGridLabelRenderer().setVerticalLabelsVisible(false);

        mSeriesX = new LineGraphSeries<>();
        mSeriesY = new LineGraphSeries<>();
        mSeriesZ = new LineGraphSeries<>();
        mSeriesX.setColor(Color.RED);
        mSeriesY.setColor(Color.GREEN);
        mSeriesZ.setColor(Color.BLUE);


        if(flag=true){
            graphView.addSeries(mSeriesX);
            System.out.println(flag);
        }else {
            graphView.removeSeries(mSeriesX);}
//            graphView.addSeries(mSeriesY);
//            graphView.addSeries(mSeriesZ);
        System.out.println(flag);
    }

    public void onResume(){
        mSubscription = mSensorEventObservable.subscribe(this::onSensorChanged);
    }

    public void onPause(){
        mSubscription.unsubscribe();
    }
 //подписсчит примет event и добавит series
    private void onSensorChanged(SensorEvent event) {
        if (!canUpdateUi()) {
            return;
        }
        else {
            appendData(mSeriesX, event.values[0]);
            appendData(mSeriesY, event.values[1]);
            appendData(mSeriesZ, event.values[2]);
//            if (!flag) {
//                removeSeries(mSeriesX);
//                removeSeries(mSeriesY);
//                removeSeries(mSeriesZ);
//            } else {
//
//                appendData(mSeriesX, event.values[0]);
//                appendData(mSeriesY, event.values[1]);
//                appendData(mSeriesZ, event.values[2]);
//            }
        }
    }

    private void removeSeries(LineGraphSeries<DataPoint>  dataPointLineGraphSeries){
        graphView.removeSeries(dataPointLineGraphSeries);

    }

    private boolean canUpdateUi() {
        long now = System.currentTimeMillis();
        if (now - mLastUpdated < 1000 / FPS) {
            return false;
        }
        mLastUpdated = now;
        return true;
    }

    private void appendData(LineGraphSeries<DataPoint> series, double value) {
        series.appendData(new DataPoint(getX(), value), true, MAX_DATA_POINTS);
    }

    private long getX() {
        return System.currentTimeMillis() - mStart;
    }
}
