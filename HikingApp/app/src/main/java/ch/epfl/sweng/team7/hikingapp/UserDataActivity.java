package ch.epfl.sweng.team7.hikingapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import ch.epfl.sweng.team7.authentication.SignedInUser;
import ch.epfl.sweng.team7.database.DataManager;
import ch.epfl.sweng.team7.database.DataManagerException;
import ch.epfl.sweng.team7.database.HikeData;
import ch.epfl.sweng.team7.database.UserData;
import ch.epfl.sweng.team7.hikingapp.guiProperties.GUIProperties;

public class UserDataActivity extends FragmentActivity {

    private final static int SELECT_PICTURE = 1;
    public final static String EXTRA_HIKE_ID = "userHikeId";
    public final static String EXTRA_USER_ID = "userProfileId";

    private DataManager mDataManager = DataManager.getInstance();
    SignedInUser mOwner = SignedInUser.getInstance();

    private UserData mUserData;
    private Long mUserId; // not necessarily the one logged in, but the one whose profile is display
    private ImageView mProfilePic;
    private LinearLayout mHikeList;
    private TextView mUserName;
    private TextView mUserEmail;
    private TextView mNumHikes;

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
                new NavigationDrawerListFactory(navDrawerList, navDrawerView.getContext(), this);

        mUserId = getIntent().getLongExtra(EXTRA_USER_ID, -1);
        mUserName = (TextView) findViewById(R.id.user_name);
        mUserEmail = (TextView) findViewById(R.id.user_email);
        mNumHikes = (TextView) findViewById(R.id.num_hikes);

        setupBackButton();

        mUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserId == mOwner.getId()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle(R.string.change_name);
                    final EditText nickname = new EditText(v.getContext());
                    nickname.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(nickname);
                    builder.setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newName = nickname.getText().toString();
                            if (!newName.equals("")) {
                                mUserName.setText(newName);
                                TextView nameSidePanel = (TextView) findViewById(R.id.profile_name);
                                nameSidePanel.setText(newName);
                                mUserData.setUserName(newName);
                                new SaveUserName().execute(newName);
                                dialog.cancel();
                            } else {
                                Toast.makeText(UserDataActivity.this,
                                        R.string.valid_user_name, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setNegativeButton(R.string.button_cancel_save,
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
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

    @Override
    protected void onResume() {
        super.onResume();
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer_layout);
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mUserId = intent.getLongExtra(EXTRA_USER_ID, -1);
        setIntent(intent);
        new GetUserData().execute(mUserId);
        new GetUserHikes().execute(mUserId);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                new LoadNewImage().execute(selectedImageUri);
            }
        }
    }

    private int dpToPx(int dp) {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }

    private BitmapDrawable scaleDrawable(BitmapDrawable original) {
        Bitmap bitmap = original.getBitmap();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int bounding = dpToPx(200);
        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(xScale, yScale);
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return new BitmapDrawable(scaledBitmap);
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
            if (hikes != null) {
                mHikeList.removeAllViews();
                Comparator<HikeData> comparator = new Comparator<HikeData>() {
                    public int compare(HikeData h1, HikeData h2) {
                        return h1.getDate().compareTo(h2.getDate());
                    }
                };
                Collections.sort(hikes, comparator);
                for (HikeData hike : hikes) {
                    displayHike(hike);
                }
                mNumHikes.setText(getString(R.string.num_hikes_fmt, hikes.size()));
            }
        }

        private void displayHike(final HikeData hike) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View hikeRow = inflater.inflate(R.layout.user_hike_listitem, null);
            TextView hikeDate = (TextView) hikeRow
                    .findViewById(R.id.user_hike_date);
            Date date = hike.getDate();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            dateFormat.setTimeZone(TimeZone.getTimeZone("CET"));
            String dateString = dateFormat.format(date);
            hikeDate.setText(dateString);
            TextView hikeName = (TextView) hikeRow
                    .findViewById(R.id.user_hike_name);
            hikeName.setText(hike.getTitle());
            hikeRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), HikeInfoActivity.class);
                    i.putExtra(EXTRA_HIKE_ID, String.valueOf(hike.getHikeId()));
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
            if (userData != null) {
                mUserData = userData;
                mUserName.setText(userData.getUserName());
                mUserEmail.setText(userData.getMailAddress());
                new GetUserPic().execute(userData.getUserProfilePic());
            } else {
                mUserName.setText(R.string.user_not_found);
            }
        }
    }

    private class SaveUserName extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... userNames) {
            try {
                mDataManager.changeUserName(userNames[0], mOwner.getId());
                return true;
            } catch (DataManagerException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
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
            if (pic != null) {
                mProfilePic.setImageDrawable(pic);
            } else {
                mProfilePic.setImageResource(R.drawable.login_background);
            }
        }
    }

    private class LoadNewImage extends AsyncTask<Uri, Void, Drawable> {
        @Override
        protected Drawable doInBackground(Uri... uris) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(uris[0]);
                return Drawable.createFromStream(inputStream, uris[0].toString());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Drawable profilePic) {
            if (profilePic != null) {
                Drawable scaledPic = scaleDrawable((BitmapDrawable) profilePic);
                mProfilePic.setImageDrawable(scaledPic);
                ImageView profileSidePanel = (ImageView) findViewById(R.id.profile_pic_side_panel);
                profileSidePanel.setImageDrawable(profilePic);
                new StoreProfileImage().execute(profilePic);
            }
            // else don't store the image and don't change pic id in user data
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
            if (profilePicId != null) {
                mUserData.setUserProfilePic(profilePicId);
                new StoreUserProfilePic().execute(profilePicId);
            }
        }
    }

    private class StoreUserProfilePic extends AsyncTask<Long, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Long... picIds) {
            try {
                mDataManager.setUserProfilePic(picIds[0], mOwner.getId());
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
        }
    }

    private void setupBackButton() {
        Button backButton = (Button) findViewById(R.id.back_button);
        backButton.setText("");
        backButton.setBackgroundResource(R.drawable.button_back);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) backButton.getLayoutParams();
        lp.width = GUIProperties.DEFAULT_BUTTON_SIZE;
        lp.height = GUIProperties.DEFAULT_BUTTON_SIZE;
        lp.setMargins(GUIProperties.DEFAULT_BUTTON_MARGIN, GUIProperties.DEFAULT_BUTTON_MARGIN, GUIProperties.DEFAULT_BUTTON_MARGIN, GUIProperties.DEFAULT_BUTTON_MARGIN);
    }
}
