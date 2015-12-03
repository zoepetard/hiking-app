package ch.epfl.sweng.team7.hikingapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.team7.authentication.SignedInUser;
import ch.epfl.sweng.team7.database.UserData;

import static android.support.v4.app.ActivityCompat.startActivity;

/** Class that creates a listview for the navigation drawer */
public class NavigationDrawerListFactory {

    private Context context;
    private final static String LIST_ITEM_ACCOUNT = "Account";
    private final static String LIST_ITEM_LOGOUT = "Logout";

    public NavigationDrawerListFactory(ListView navDrawerList, final Context context) {
        this.context = context;

        // TODO: set text field of profile_name, email etc using data queried from server

        // load items into the Navigation drawer and add listeners
        loadNavDrawerItems(navDrawerList);
        navDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemText = (String) parent.getItemAtPosition(position);
                Intent intent;

                switch (itemText) {
                    case LIST_ITEM_ACCOUNT:
                        intent = new Intent(view.getContext(), UserDataActivity.class);
                        intent.putExtra(UserDataActivity.EXTRA_USER_ID, SignedInUser.getInstance().getId());
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        view.getContext().startActivity(intent);
                        break;
                    case LIST_ITEM_LOGOUT:
                        new AlertDialog.Builder(context)
                                .setMessage(R.string.logout)
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        LoginActivity.onSignOutClicked();
                                        Intent i = new Intent(context, LoginActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        context.startActivity(i);
                                    }
                                })
                                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        break;
                }
            }
        });
    }


    private void loadNavDrawerItems(ListView navDrawerList) {
        String[] listViewItems = {LIST_ITEM_ACCOUNT, LIST_ITEM_LOGOUT};
        ArrayAdapter<String> navDrawerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listViewItems);
        navDrawerList.setAdapter(navDrawerAdapter);
    }
}
