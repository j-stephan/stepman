package de.tu_dresden.carebears.stepman;

/**
 * Created by frog on 12.12.15.
 */
public interface SensorHandler {

    public boolean initialize();
    public boolean isInitialized();
    public void close();
    public void reset();

    public String getStatusMessage();
    public float getData();

}
