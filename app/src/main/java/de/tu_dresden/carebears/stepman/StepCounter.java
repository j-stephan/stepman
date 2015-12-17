package de.tu_dresden.carebears.stepman;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by jan on 09.12.15.
 */
public class StepCounter extends Service implements SensorHandler{

    private static StepCounter instance;
    private final IBinder mBinder = new LocalBinder();

    private SensorManager mSensorManager;
    private Sensor stepCounter;
    private StepListener stepListener;

    private boolean initialized;
    private boolean firstSteps;
    private int steps;
    private int initialSteps;
    private String status;

    public StepCounter() {
        super();
        this.steps = 0;
        this.initialized = false;
    }

    public static StepCounter getInstance() {
        if(instance == null) {
            instance = new StepCounter();
        }

        return instance;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean initialize() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        this.stepListener = new StepListener();

        this.firstSteps = true;

        if (stepCounter != null) {
            if(!mSensorManager.registerListener(stepListener,stepCounter,  SensorManager.SENSOR_DELAY_NORMAL)) {
                status = getString(R.string.init_step_sensor_fail);
                return false;
            }
            status = getString(R.string.sensor_initialized);
            this.initialized = true;
            return true;
        }

        status = getString(R.string.init_step_sensor_fail);
        return false;
    }

    public void close() {
        mSensorManager.unregisterListener(stepListener);
        steps = 0;
        initialized = false;
    }

    private void setSteps(int steps){
        this.steps = steps - this.initialSteps;
    }

    public float getData() {
        return steps;
    }

    public void reset() {
        initialSteps += steps;
        steps = 0;
    }

    @Override
    public String getStatusMessage() {
        return status;
    }

    private class StepListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if(firstSteps){
                initialSteps = Math.round(event.values[0]);
                firstSteps = false;
            }
            setSteps(Math.round(event.values[0]));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    public int getInitialSteps() {
        return initialSteps;
    }

    public void setInitialSteps(int initialSteps) {
        this.initialSteps = initialSteps;
    }

    public boolean getFirstSteps(){
        return firstSteps;
    }

    public void setFirstSteps(boolean firstSteps) {
        this.firstSteps = firstSteps;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        this.status = getString(R.string.sensor_not_initialized);

        if(initialize()) {
            return START_STICKY;
        }

        return START_FLAG_RETRY;
    }

    public class LocalBinder extends Binder {
        StepCounter getService(){
            return StepCounter.this;
        }
    }
}
