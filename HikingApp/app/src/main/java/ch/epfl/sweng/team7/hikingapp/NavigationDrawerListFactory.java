package ch.epfl.sweng.team7.hikingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.app.ActivityCompat.startActivity;

/** Class that creates a listview for the navigation drawer */
public class NavigationDrawerListFactory {

    private Context context;
    private final static String LIST_ITEM_LOGOUT = "Logout";

    public NavigationDrawerListFactory(ListView navDrawerList,Context context) {

        this.context = context;

        // load items into the Navigation drawer and add listeners
        loadNavDrawerItems(navDrawerList);
        navDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemText = (String) parent.getItemAtPosition(position);
                Intent intent;

                switch (itemText) {
                    case LIST_ITEM_LOGOUT:
                        intent = new Intent(view.getContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        view.getContext().startActivity(intent);
                        break;
                }
            }
        });

    }


    private void loadNavDrawerItems(ListView navDrawerList) {

        String[] listViewItems = {LIST_ITEM_LOGOUT};
        ArrayAdapter<String> navDrawerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listViewItems);
        navDrawerList.setAdapter(navDrawerAdapter);

    }


}
