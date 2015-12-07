package ch.epfl.sweng.team7.hikingapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLngBounds;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.team7.authentication.SignedInUser;
import ch.epfl.sweng.team7.database.DataManager;
import ch.epfl.sweng.team7.database.DataManagerException;
import ch.epfl.sweng.team7.database.UserData;

import static android.support.v4.app.ActivityCompat.startActivity;

/** Class that creates a listview for the navigation drawer */
public class NavigationDrawerListFactory {
    private final static String LIST_ITEM_ACCOUNT = "Account";
    private final static String LIST_ITEM_LOGOUT = "Logout";

    private Context mContext;
    private ImageView mProfilePic;
    private TextView mProfileName;
    private TextView mProfileEmailName;
    private TextView mProfileEmailDomain;
    private DataManager mDataManager = DataManager.getInstance();

    public NavigationDrawerListFactory(ListView navDrawerList, final Context context,
                                       final Activity activity) {
        mContext = context;

        mProfilePic = (ImageView) activity.findViewById(R.id.profile_pic_side_panel);
        mProfileName = (TextView) activity.findViewById(R.id.profile_name);
        mProfileEmailName = (TextView) activity.findViewById(R.id.profile_email1);
        mProfileEmailDomain = (TextView) activity.findViewById(R.id.profile_email2);
        new GetUserData().execute(SignedInUser.getInstance().getId());

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
                        intent.putExtra(UserDataActivity.EXTRA_USER_ID,
                                SignedInUser.getInstance().getId());
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        view.getContext().startActivity(intent);
                        break;
                    case LIST_ITEM_LOGOUT:
                        new AlertDialog.Builder(mContext)
                                .setMessage(R.string.logout)
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        LoginActivity.onSignOutClicked();
                                        Intent i = new Intent(mContext, LoginActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        mContext.startActivity(i);
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
        ArrayAdapter<String> navDrawerAdapter = new ArrayAdapter<String>(
                mContext, android.R.layout.simple_list_item_1, listViewItems);
        navDrawerList.setAdapter(navDrawerAdapter);
    }

    private class GetUserData extends AsyncTask<Long, Void, UserData> {

        @Override
        protected UserData doInBackground(Long... userIds) {
            try {
                return mDataManager.getUserData(userIds[0]);
            } catch (DataManagerException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(UserData userData) {
            if (userData != null) {
                mProfileName.setText(userData.getUserName());
                String mail = userData.getMailAddress();
                Integer at = mail.indexOf("@");
                mProfileEmailName.setText(mail.substring(0, at));
                mProfileEmailDomain.setText(mail.substring(at));
                new GetUserPic().execute(userData.getUserProfilePic());
            }
        }
    }

    private class GetUserPic extends AsyncTask<Long, Void, Drawable> {

        @Override
        protected Drawable doInBackground(Long... picIds) {
            try {
                return mDataManager.getImage(picIds[0]);
            } catch (DataManagerException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Drawable pic) {
            mProfilePic.setImageDrawable(pic);
        }
    }
}
