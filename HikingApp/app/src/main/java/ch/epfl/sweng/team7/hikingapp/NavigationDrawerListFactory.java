package ch.epfl.sweng.team7.hikingapp;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import static android.support.v4.app.ActivityCompat.startActivity;

/** Class that creates a listview for the navigation drawer */
public class NavigationDrawerListFactory {

    private Context context;

    private final static String LIST_ITEM_ACCOUNT = "Account";
    private final static String LIST_ITEM_MAP = "Map";
    private final static String LIST_ITEM_HIKES = "Hikes";
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
                    case LIST_ITEM_ACCOUNT:
                        intent = new Intent(view.getContext(), ChangeNicknameActivity.class);
                        view.getContext().startActivity(intent);
                        break;
                    case LIST_ITEM_MAP:
                        intent = new Intent(view.getContext(), MapActivity.class);
                        view.getContext().startActivity(intent);
                        break;
                    case LIST_ITEM_HIKES:
                        intent = new Intent(view.getContext(), HikeListActivity.class);
                        view.getContext().startActivity(intent);
                        break;
                    case LIST_ITEM_LOGOUT:
                        intent = new Intent(view.getContext(), LoginActivity.class);
                        view.getContext().startActivity(intent);
                        break;
                }
            }
        });

    }


    private void loadNavDrawerItems(ListView navDrawerList) {

        String[] listViewItems = {LIST_ITEM_ACCOUNT, LIST_ITEM_MAP, LIST_ITEM_HIKES, LIST_ITEM_LOGOUT};
        ArrayAdapter<String> navDrawerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listViewItems);
        navDrawerList.setAdapter(navDrawerAdapter);

    }


}
