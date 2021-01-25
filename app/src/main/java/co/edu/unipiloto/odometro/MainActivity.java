package co.edu.unipiloto.odometro;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends Activity {

    private OdometerService odometer;
    private boolean bound = false;
    private int tiempo_act = 4;  // setear tiempo de actualizacion del servicio
    public int metros_p = 1;
    EditText tiempo;
    EditText metros;
    private final int PERMISSION_REQUEST_CODE =698;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            OdometerService.OdometerBinder odometerBinder =
                    (OdometerService.OdometerBinder) binder;
            odometer = odometerBinder.getOdometer();
            bound = true;
            tiempo  = (EditText) findViewById(R.id.tiempo);
            metros  = (EditText) findViewById(R.id.metros);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        watchMileage();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, OdometerService.Permission_String) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {OdometerService.Permission_String}, PERMISSION_REQUEST_CODE);
    } else {
        Intent intent = new Intent(this, OdometerService.class);
        intent = intent.putExtra("metros",metros_p);
        intent = intent.putExtra("tiempo",tiempo_act);
            bindService(intent, connection, Context.BIND_AUTO_CREATE);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            unbindService(connection);
            bound = false;
        }
    }

    private void watchMileage() {
        final TextView distanceView = (TextView)findViewById(R.id.distance);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                double distance = 0.0;
                if (odometer != null) {
                    distance = odometer.getMiles();
                }
                String distanceStr = String.format("%1$,.2f miles", distance);
                distanceView.setText(distanceStr);
                handler.postDelayed(this, tiempo_act * 1000);

                TextView tempo_act = findViewById(R.id.tiempo_act); tempo_act.setText(String.valueOf(tiempo_act + "  segundos"));

            }
        });
    }
    public void GetTiempoAct(View view) {

        String time_s = tiempo.getText().toString();
        int time = Integer.parseInt(time_s);
        tiempo_act= time;
        String metros_s = metros.getText().toString();
        int metros = Integer.parseInt(metros_s);
        metros_p = metros;


    }
}