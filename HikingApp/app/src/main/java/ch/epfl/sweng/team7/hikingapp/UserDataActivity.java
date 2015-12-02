package ch.epfl.sweng.team7.hikingapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ch.epfl.sweng.team7.authentication.SignedInUser;
import ch.epfl.sweng.team7.database.DataManager;
import ch.epfl.sweng.team7.database.DataManagerException;
import ch.epfl.sweng.team7.database.HikeData;
import ch.epfl.sweng.team7.network.DatabaseClient;
import ch.epfl.sweng.team7.network.RawHikeComment;

public class UserDataActivity extends Activity {
    private final static int SELECT_PICTURE = 1;
    private final static String EXTRA_HIKE_ID = "userHikeId";

    private ImageView mProfilePic;
    private DataManager mDataManager = DataManager.getInstance();
    private LinearLayout mHikeList;

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

        mProfilePic = (ImageView) findViewById(R.id.profile_pic);
        mProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setMessage(R.string.new_profile)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent,
                                        getString(R.string.selete_pic)), SELECT_PICTURE);
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

        mHikeList = (LinearLayout) findViewById(R.id.user_hike_list);

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

        new GetUserHikes().execute(mUser.getId());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                mProfilePic.setImageURI(selectedImageUri);
                ImageView sidePanelPic = (ImageView)findViewById(R.id.profile_pic_side_panel);
                sidePanelPic.setImageURI(selectedImageUri);
            }
        }
    }

    private class GetUserHikes extends AsyncTask<Long, Void, List<HikeData>> {

        @Override
        protected List<HikeData> doInBackground(Long... userIds) {
            try {
                return mDataManager.getUserHikes(userIds[0]);
            } catch (DataManagerException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<HikeData> hikes) {
            for (HikeData hike : hikes) {
                displayHike(hike);
            }
        }

        private void displayHike(final HikeData hike) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View hikeRow = inflater.inflate(R.layout.user_hike_listitem, null);
            TextView hikeDate = (TextView) hikeRow
                    .findViewById(R.id.user_hike_date);
            hikeDate.setText(hike.getDate().toString());
            TextView hikeName = (TextView) hikeRow
                    .findViewById(R.id.user_hike_name);
            hikeName.setText(hike.getTitle());
            hikeRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), HikeInfoActivity.class);
                    i.putExtra(EXTRA_HIKE_ID, hike.getHikeId());
                    startActivity(i);
                }
            });
            mHikeList.addView(hikeRow);
        }
    }
}
