package eu.tutorials.memorableplacessaver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> mplaces = new ArrayList<>();
    static  ArrayList<LatLng> locations  = new ArrayList<>();
    static ArrayAdapter<String> arrayAdapter;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ListView lv = findViewById(R.id.listView);

        SharedPreferences sharedPreferences = this.getSharedPreferences("eu.tutorials.memorableplacessaver",MODE_PRIVATE);
        //sharedPreferences.edit().clear().commit();
        ArrayList<String> latitudes = new ArrayList<>();
        ArrayList<String> longitudes = new ArrayList<>();


        try{
         mplaces = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places",ObjectSerializer.serialize(new ArrayList<>())));
         latitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lats",ObjectSerializer.serialize(new ArrayList<>())));
         longitudes= (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lons",ObjectSerializer.serialize(new ArrayList<>())));

        }catch (Exception e){
            e.printStackTrace();
        }

        if(mplaces.size()>0 && latitudes.size()>0 && longitudes.size()>0){
            if(mplaces.size() == latitudes.size() && mplaces.size() == longitudes.size()){
                for(int i=0;i<latitudes.size();i++){
                    locations.add(new LatLng(Double.parseDouble(latitudes.get(i)),Double.parseDouble(longitudes.get(i))));
                }
            }
        }
        else{
            mplaces.add("Add a new memorable Place........");
            locations.add(new LatLng(0,0));
        }
        for(LatLng c:locations) Log.i("Locations",mplaces.toString());
         arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,mplaces);
        lv.setAdapter(arrayAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);

                intent.putExtra("Placenumber",i);

                startActivity(intent);
            }
        });
    }
}