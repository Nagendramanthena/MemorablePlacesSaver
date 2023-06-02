package eu.tutorials.memorableplacessaver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static List<String> mplaces = new ArrayList<>();
    static  ArrayList<LatLng> locations  = new ArrayList<>();
    static ArrayAdapter<String> arrayAdapter;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ListView lv = findViewById(R.id.listView);

        mplaces.add("Add a new memorable Places....");

        locations.add(new LatLng(0,0));

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