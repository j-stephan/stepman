package de.tu_dresden.carebears.stepman;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by jan on 11.12.15.
 */
public class DistanceManager implements SensorHandler{

    private static DistanceManager instance;
    private Context context;

    private LocationManager manager;
    private LocationListener listener;
    private String provider;

    private boolean initialized;
    private String status;

    private Location lastLocation;
    private float distance;
    private long timeBetween;

    private DistanceManager(Context ctx) {
        this.context = ctx;
        this.initialized = false;
        this.distance = 0;
        this.status = ctx.getString(R.string.sensor_not_initialized);
    }

    public static DistanceManager getInstance(Context ctx) {
        if(instance == null)
            instance = new DistanceManager(ctx);

        return instance;
    }

    public boolean initialize() {
        if(initialized) {
            return true;
        }

        manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        boolean gpsEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!(gpsEnabled || networkEnabled)) {
            status = context.getString(R.string.location_providers_offline);
            return false;
        }

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                distance += location.distanceTo(lastLocation);
                timeBetween = location.getTime() - lastLocation.getTime();
                lastLocation = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        provider = manager.getBestProvider(criteria, true);

        if(context.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION") != PackageManager.PERMISSION_GRANTED) {
            status = context.getString(R.string.missing_location_permission);
            return false;
        }

        manager.requestLocationUpdates(provider, 0, 0, listener);
        lastLocation = manager.getLastKnownLocation(provider);

        status = context.getString(R.string.sensor_initialized);
        initialized = true;
        return true;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void close() {

    }

    @Override
    public void reset() {
        distance = 0;
    }

    @Override
    public String getStatusMessage() {
        return status;
    }

    public float getData() {
        return distance;
    }

    public long getTimeBetween() {
        return timeBetween;
    }

    public void setDistance(float distance){
        this.distance = distance;
    }
}
