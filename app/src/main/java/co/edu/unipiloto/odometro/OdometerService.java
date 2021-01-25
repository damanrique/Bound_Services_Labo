package co.edu.unipiloto.odometro;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.Random;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class OdometerService extends Service {

    private final IBinder binder = new OdometerBinder();
    private static double distanceInMeters;
    private static Location lastLocation = null;
    private LocationListener listener;
    private LocationManager locManager;
    private final Random random = new Random();
    public static final String Permission_String = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private int prec_mt;
    private int prec_ti;


    public class OdometerBinder extends Binder {
        OdometerService getOdometer() {
            return OdometerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        int metros = intent.getIntExtra("metros", 1);
        prec_mt = metros;
        int tiempo = intent.getIntExtra("tiempo", 1000);
        prec_ti= tiempo;
        return binder;

    }

    @Override
    public void onCreate() {
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (lastLocation == null) {
                    lastLocation = location;
                }
                distanceInMeters += location.distanceTo(lastLocation);
                lastLocation = location;
            }

            @Override
            public void onProviderDisabled(String arg0) {
            }

            @Override
            public void onProviderEnabled(String arg0) {
            }

            @Override
            public void onStatusChanged(String arg0, int arg1, Bundle bundle) {
            }
        };
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
            String provider = locManager.getBestProvider(new Criteria(), true);
            if (provider !=null) {
        locManager.requestLocationUpdates(provider, prec_ti, prec_mt, listener);
        }        }
    }

    @Override
    public void onDestroy() {
        if (locManager != null && listener != null) {
            locManager.removeUpdates(listener);
            locManager = null;
            listener = null;
        }
    }

    public double getMiles() {
/*        return random.nextDouble(); //Numero Aleatorio para traer distancia*/
        return this.distanceInMeters ;
    }
}