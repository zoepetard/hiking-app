package ch.epfl.sweng.team7.hikingapp;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
 *
 * Class that creates a listview for the navigation drawer
 *
 * */


public class NavigationDrawerListFactory {

    private ListView navDrawerList;
    private View view;

    public NavigationDrawerListFactory(View view, ListView navDrawerList) {

        this.view = view;
        this.navDrawerList = navDrawerList;

        // load items into the Navigation drawer and add listeners
        loadNavDrawerItems(navDrawerList);
        navDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemText = (String) parent.getItemAtPosition(position);
                Intent intent;

                switch (itemText) {
                    case "Account":
                        intent = new Intent(view.getContext(), ChangeNicknameActivity.class);
                        view.getContext().startActivity(intent);
                        break;
                    case "Map":
                        intent = new Intent(view.getContext(), MapActivity.class);
                        view.getContext().startActivity(intent);
                        break;
                    case "Hikes":
                        intent = new Intent(view.getContext(), HikeListActivity.class);
                        view.getContext().startActivity(intent);
                        break;
                    case "Logout":
                        intent = new Intent(view.getContext(), LoginActivity.class);
                        view.getContext().startActivity(intent);
                        break;
                }
            }
        });

    }

    public ListView getListView()
    {
        return navDrawerList;
    }

    private void loadNavDrawerItems(ListView navDrawerList) {

        String[] listViewItems = {"Account", "Map", "Hikes", "Logout"};
        ArrayAdapter<String> navDrawerAdapter = new ArrayAdapter<String>(this.view.getContext(), android.R.layout.simple_list_item_1, listViewItems);
        navDrawerList.setAdapter(navDrawerAdapter);

    }


}
