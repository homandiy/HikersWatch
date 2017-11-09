package homanhuang.hikerswatch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    LocationManager locationManager;
    LocationListener locationListener;

    //location info textviews
    TextView latTextView;
    TextView lonTextView;
    TextView acuTextView;
    TextView altTextView;
    TextView addTextView;

    //request for permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);

        //check grant result
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening();
        }
    }


    //active locatioon service
    public void startListening() {

        //if granted, active location service
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    //update location info
    public void updaeLocationInfo(Location location) {
        Log.i("tms LocationInfo", location.toString());

        String latitude = String.format("%.2f",  location.getLatitude());
        latTextView.setText("Latitude:    " + latitude);

        String longtitude = String.format("%.2f",  location.getLongitude());
        lonTextView.setText("Longtitude:              " + longtitude);

        String altitude = String.format("%.2f",  location.getAltitude());
        altTextView.setText("Altitude:               " + altitude);

        String accuracy = String.format("%.2f",  location.getAccuracy());
        acuTextView.setText("Accuracy:                       " + accuracy);
        //addTextView.setText("Address:\n" + );

        //get address
        String address = "Address Not Found";
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> listAddresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
            if (listAddresses != null && listAddresses.size() > 0) {
                Log.i("tms Place", listAddresses.get(0).toString());

                StringBuffer s = new StringBuffer();
                s.append("Address:\n");

                if (listAddresses.get(0).getAddressLine(0) != null) {
                    s.append(listAddresses.get(0).getAddressLine(0) + "\n");
                }
                if (listAddresses.get(0).getAddressLine(1) != null) {
                    s.append(listAddresses.get(0).getAddressLine(1) + "\n");
                }
                if (listAddresses.get(0).getAddressLine(2) != null) {
                    s.append(listAddresses.get(0).getAddressLine(2));
                }

                address = s.toString();
            }

            addTextView.setText(address);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    startup:
    initial
        location manager for service
        location listener for location
    check SDK version
        < 23,
            request for location update
        >= 23,
            ask for permission
            request for location update
            get last location
            update location
     */
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //info text views
        latTextView = (TextView) findViewById(R.id.latTextView);
        lonTextView = (TextView) findViewById(R.id.lonTextView);
        acuTextView = (TextView) findViewById(R.id.acuTextView);
        altTextView = (TextView) findViewById(R.id.altTextView);
        addTextView = (TextView) findViewById(R.id.addTextView);

        //initial location service and listener
        locationManager = (LocationManager) this.getSystemService((Context.LOCATION_SERVICE));

        //check SDK version
        if (Build.VERSION.SDK_INT < 23) {
            //directly request
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            Location location =  MyLocation.getLastBestLocation(this);
            updaeLocationInfo(location);

        } else {

            //check permission
            if ( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {

                //not granted ask for permission
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 1);

            } else {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

                Location location =  MyLocation.getLastBestLocation(getApplicationContext());

                if (location != null) {
                    updaeLocationInfo(location);
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        updaeLocationInfo(location);
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

    public void checkMap(View view) {
        //update location
        Location location =  MyLocation.getLastBestLocation(getApplicationContext());
        //switch map activity
        Intent i = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(i);
    }
}
