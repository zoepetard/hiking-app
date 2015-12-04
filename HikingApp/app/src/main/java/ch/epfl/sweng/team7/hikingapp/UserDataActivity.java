package ch.epfl.sweng.team7.hikingapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

import ch.epfl.sweng.team7.authentication.SignedInUser;
import ch.epfl.sweng.team7.database.DataManager;
import ch.epfl.sweng.team7.database.DataManagerException;
import ch.epfl.sweng.team7.database.HikeData;
import ch.epfl.sweng.team7.database.UserData;

public class UserDataActivity extends FragmentActivity {
    private final static int SELECT_PICTURE = 1;
    private final static String EXTRA_HIKE_ID = "userHikeId";
    public final static String EXTRA_USER_ID = "userProfileId";

    private static UserData mUserData;

    private DataManager mDataManager = DataManager.getInstance();
    SignedInUser mOwner = SignedInUser.getInstance();

    private Long mUserId; // not necessarily the one logged in, but the one whose profile is display
    private ImageView mProfilePic;
    private LinearLayout mHikeList;
    private TextView userName;
    private TextView userEmail;
    private TextView numHikes;

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
                new NavigationDrawerListFactory(navDrawerList, this);

        mUserId = getIntent().getLongExtra(EXTRA_USER_ID, -1);
        userName = (TextView) findViewById(R.id.user_name);
        userEmail = (TextView) findViewById(R.id.user_email);
        numHikes = (TextView) findViewById(R.id.num_hikes);

        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserId == mOwner.getId()) {
                    DialogFragment changeNicknameDialog = new ChangeNicknameDialog();
                    changeNicknameDialog.show(getSupportFragmentManager(), "change");
                }
            }
        });

        mProfilePic = (ImageView) findViewById(R.id.profile_pic);
        mProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserId == mOwner.getId()) {
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
            }
        });

        mHikeList = (LinearLayout) findViewById(R.id.user_hike_list);

        Button back_button = (Button) findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        new GetUserData().execute(mUserId);

        new GetUserHikes().execute(mUserId);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                new LoadNewImage().execute(selectedImageUri.toString());
            }
        }
    }

    public static void changeUserName(String newUserName) {
        mUserData.setUserName(newUserName);
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
            numHikes.setText(getString(R.string.num_hikes_fmt, hikes.size()));
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
            mUserData = userData;
            userName.setText(userData.getUserName());
            userEmail.setText(userData.getMailAddress());

            new GetUserPic().execute(userData.getUserProfilePic());
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

    private class LoadNewImage extends AsyncTask<String, Void, BitmapDrawable> {
        @Override
        protected BitmapDrawable doInBackground(String... urls) {
            try {
                InputStream in = new java.net.URL(urls[0]).openStream();
                return new BitmapDrawable(getResources(), BitmapFactory.decodeStream(in));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(BitmapDrawable profilePic) {
            mProfilePic.setImageDrawable(profilePic);
            new StoreProfileImage().execute(profilePic);
        }
    }

    private class StoreProfileImage extends AsyncTask<Drawable, Void, Long> {
        @Override
        protected Long doInBackground(Drawable... photos) {
            try {
                return mDataManager.storeImage(photos[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Long profilePicId) {
            mUserData.setUserProfilePic(profilePicId);
        }
    }
}
