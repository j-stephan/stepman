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
public class DistanceManager {

    private static DistanceManager instance;
    private Context context;

    private LocationManager manager;
    private LocationListener listener;
    private String provider;

    private boolean initialized;

    private Location lastLocation;
    private float distance;
    private long timeBetween;

    private DistanceManager(Context ctx) {
        context = ctx;
        initialized = false;
    }

    public static DistanceManager getInstance(Context ctx) {
        if(instance == null)
            instance = new DistanceManager(ctx);

        return instance;
    }

    public boolean initialize() {
        if(initialized)
            return true;

        manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        boolean gpsEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!(gpsEnabled || networkEnabled))
            return false;

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                distance = location.distanceTo(lastLocation);
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
        criteria.setAccuracy(Criteria.ACCURACY_HIGH);
        provider = manager.getBestProvider(criteria, true);

        if(context.checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION") != PackageManager.PERMISSION_GRANTED)
            return false;

        manager.requestLocationUpdates(provider, 0, 0, listener);
        lastLocation = manager.getLastKnownLocation(provider);

        return true;
    }

    float getDistance() {
        return distance;
    }

    long getTimeBetween() {
        return timeBetween;
    }
}
