package ch.epfl.sweng.team7.hikingapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import ch.epfl.sweng.team7.network.DatabaseClientException;
import ch.epfl.sweng.team7.network.DefaultNetworkProvider;
import ch.epfl.sweng.team7.network.NetworkDatabaseClient;

public class UserDataActivity extends Activity {
    private int user_id;

    SignedInUser mUser = SignedInUser.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);

        // Inflate Navigation Drawer with main content
        View navDrawerView = getLayoutInflater().inflate(R.layout.navigation_drawer, null);
        FrameLayout mainContentFrame = (FrameLayout) findViewById(R.id.main_content_frame);
        View userDataView = getLayoutInflater().inflate(R.layout.activity_user_data, null);
        mainContentFrame.addView(userDataView);

        // load items into the Navigation drawer and add listeners
        ListView navDrawerList = (ListView) findViewById(R.id.nav_drawer);
        NavigationDrawerListFactory navDrawerListFactory =
                new NavigationDrawerListFactory(navDrawerList, navDrawerView.getContext());

        // TODO: add more field when we decide to store more user information
        TextView userName = (TextView) findViewById(R.id.user_name);
        TextView userEmail = (TextView) findViewById(R.id.user_email);
        String nname = getIntent().getStringExtra(ChangeNicknameActivity.EXTRA_MESSAGE);
        TextView nickname = (TextView) findViewById(R.id.nickname);
        TextView numHikes = (TextView) findViewById(R.id.num_hikes);
        Button changeNickname = (Button) findViewById(R.id.change_nickname);
        changeNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ChangeNicknameActivity.class);
                startActivity(i);
            }
        });

        ImageView profilePic = (ImageView) findViewById(R.id.profile_pic);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setMessage(R.string.new_profile)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // TODO: choose a new image and change it in the server
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        // TODO: use real data stored in local cache after issue #56 is in master
        // for user with this user_id
        userName.setText("Team 7");
        userEmail.setText("team7@epfl.ch");
        if (nname == null) {
            nickname.setText(getString(R.string.nickname_fmt, "team7"));
        } else {
            nickname.setText(getString(R.string.nickname_fmt, nname));
        }
        numHikes.setText(getString(R.string.num_hikes_fmt, 100));

        Button back_button = (Button) findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Log.d("SIMON", "Pre-Start Download");
        new DownloadProfilePicture().execute(new Integer(2));
        Log.d("SIMON", "Pre2-Start Download");
    }


    // TODO(simon) THIS IS DEBUG CODE AND SHOULD BE REMOVED
    private class DownloadProfilePicture extends AsyncTask<Integer, Void, Drawable> {
        @Override
        protected Drawable doInBackground(Integer... params) {
            Log.d("SIMON", "Pro-Start Download");
            try {
                NetworkDatabaseClient networkDatabaseClient = new NetworkDatabaseClient("http://footpath-1104.appspot.com", new DefaultNetworkProvider());
                Log.d("SIMON", "Start Download");
                return networkDatabaseClient.getImage(0);
            } catch (DatabaseClientException e) {
                Log.d("SIMON", e.toString());
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            Log.d("SIMON", "Finished Download");
            if(drawable != null) {
                Log.d("SIMON", "Set Picture.");
                ImageView profilePic = (ImageView) findViewById(R.id.profile_pic);
                profilePic.setImageDrawable(drawable);
            }
        }
    }
}
