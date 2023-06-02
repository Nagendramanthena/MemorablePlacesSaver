package eu.tutorials.memorableplacessaver;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import eu.tutorials.memorableplacessaver.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    LocationManager lm;
    LocationListener ll;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,ll);
            }
        }
    }

    public void comL(Location loc, String title){
        LatLng userLocation = new LatLng(loc.getLatitude(),loc.getLongitude());
        mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,10));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);

        Intent in = getIntent();

        if(in.getIntExtra("Placenumber",0)==0) {
            lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            ll = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    comL(location, "yourLocation");
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }
            };


            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
                Location ulk = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                comL(ulk, "Your location");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        else{
            Location placeloc = new Location(LocationManager.GPS_PROVIDER);
            placeloc.setLatitude(MainActivity.locations.get(in.getIntExtra("Placenumber",0)).latitude);
            placeloc.setLongitude(MainActivity.locations.get(in.getIntExtra("Placenumber",0)).longitude);

            comL(placeloc,MainActivity.mplaces.get(in.getIntExtra("Placenumber",0)));
        }
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        Geocoder geocoder  =new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = "";
        try{
            List<Address> la = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);

            if(la!=null && la.size()>0){
                if(la.get(0).getThoroughfare()!=null){
                    address += la.get(0).getThoroughfare();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        if(address.equals("")){
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            address += fmt.format(new Date());

        }
        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
        MainActivity.mplaces.add(address);
        MainActivity.locations.add(latLng);
        MainActivity.arrayAdapter.notifyDataSetChanged();
        SharedPreferences sharedPreferences = this.getSharedPreferences("eu.tutorials.memorableplacessaver",Context.MODE_PRIVATE);
        try{
            ArrayList<String> latitudes = new ArrayList<>();
            ArrayList<String> longtitudes = new ArrayList<>();
            for(LatLng coors:MainActivity.locations){
                latitudes.add(Double.toString(coors.latitude));
                longtitudes.add(Double.toString(coors.longitude));
            }

            sharedPreferences.edit().putString("places",ObjectSerializer.serialize(MainActivity.mplaces)).apply();
            sharedPreferences.edit().putString("lats",ObjectSerializer.serialize(latitudes)).apply();
            sharedPreferences.edit().putString("lons",ObjectSerializer.serialize(longtitudes)).apply();
        }catch (Exception e){
            e.printStackTrace();
        }

        Toast.makeText(this,address+" Saved Successfully ",Toast.LENGTH_SHORT).show();
    }
}
