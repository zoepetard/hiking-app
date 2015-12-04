package ch.epfl.sweng.team7.hikingapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import ch.epfl.sweng.team7.authentication.SignedInUser;
import ch.epfl.sweng.team7.database.DataManager;
import ch.epfl.sweng.team7.database.DataManagerException;
import ch.epfl.sweng.team7.gpsService.GPSManager;
import ch.epfl.sweng.team7.network.RatingVote;

public final class HikeInfoActivity extends FragmentActivity {
    private long hikeId;
    private SignedInUser mUser = SignedInUser.getInstance();
    private final static String LOG_FLAG = "Activity_HikeInfo";
    private final static String HIKE_ID = "hikeID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);

        Intent intent = getIntent();
        if (intent.getBooleanExtra(GPSManager.NEW_HIKE, false)) {
            displayEditableHike(intent);
        } else {
            loadStaticHike(intent, savedInstanceState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong(HIKE_ID, hikeId);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void displayEditableHike(Intent intent) {
        EditText hikeName = (EditText) findViewById(R.id.hikeinfo_name);
        //TODO set it to editable

        Button saveButton = new Button(this);
        saveButton.setText("Save");
        saveButton.setId(R.id.button_save_hike);
        //TODO add click listener to saveButton

        addButtonToView();
    }

    private void addButtonToView() {
        //TODO add created button to the current View
    }

    private void loadStaticHike(Intent intent, Bundle savedInstanceState) {
        String hikeIdStr = intent.getStringExtra(HikeListActivity.EXTRA_HIKE_ID);
        if (hikeIdStr == null && savedInstanceState != null) {
            hikeId = savedInstanceState.getLong(HIKE_ID);
        } else if (hikeIdStr != null) {
            hikeId = Long.valueOf(hikeIdStr);
        }
        View view = findViewById(android.R.id.content);

        // load main content into the navigations drawer's framelayout
        FrameLayout mainContentFrame = (FrameLayout) findViewById(R.id.main_content_frame);
        View hikeInfoLayout = getLayoutInflater().inflate(R.layout.activity_hike_info, null);
        mainContentFrame.addView(hikeInfoLayout);

        GoogleMap mapHikeInfo = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapHikeInfo))
                .getMap();

        final HikeInfoView hikeInfoView = new HikeInfoView(view, this, hikeId, mapHikeInfo);

        // set listener methods for UI elements in HikeInfoView
        hikeInfoView.getHikeRatingBar().setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    new SubmitVoteTask().execute(new RatingVote(hikeId, rating));
                }
            }
        });

        // Setting a listener for each imageview.
        for (int i = 0; i < hikeInfoView.getGalleryImageViews().size(); i++) {

            ImageView imgView = hikeInfoView.getGalleryImageViews().get(i);
            imgView.setOnClickListener(new ImageViewClickListener());
        }

        hikeInfoView.getBackButton().setOnClickListener(new BackButtonClickListener());

        if (mapHikeInfo != null) {
            mapHikeInfo.setOnMapClickListener(new MapPreviewClickListener());
        }

        Button back_button = (Button) findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button exportButton = (Button) findViewById(R.id.button_export_hike);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hikeInfoView.getDisplayedHike() != null) {
                    new GPXExporter().execute(hikeInfoView);
                }
            }
        });

        EditText commentEditText = (EditText) findViewById(R.id.comment_text);
        commentEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
    }

    private class SubmitVoteTask extends AsyncTask<RatingVote, Void, Boolean> {
        @Override
        protected Boolean doInBackground(RatingVote... vote) {
            try {
                DataManager.getInstance().postVote(vote[0]);
                return Boolean.TRUE;
            } catch (DataManagerException e) {
                e.printStackTrace();
                return Boolean.FALSE;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (!success) {
                // TODO(simon) display error?
            }
        }
    }

    private class ImageViewClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            // Update image in full screen view
            ImageView imgView = (ImageView) v;
            Drawable drawable = imgView.getDrawable();

            ImageView fullScreenView = (ImageView) findViewById(R.id.image_fullscreen);
            fullScreenView.setImageDrawable(drawable);

            toggleFullScreen();
        }
    }

    private class MapPreviewClickListener implements GoogleMap.OnMapClickListener {
        @Override
        public void onMapClick(LatLng point) {
            // segue to map activity!

        }
    }

    private class BackButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            toggleFullScreen();
        }
    }

    private class GPXExporter extends AsyncTask<HikeInfoView, Void, String> {

        /**
         * Exports a hike in GPX format to the device's internal storage
         *
         * @param params - HikeInfoView
         * @return file path of created file
         */
        @Override
        protected String doInBackground(HikeInfoView... params) {

            String filePath = null;
            try {
                filePath = DataManager.getInstance().saveGPX(params[0].getDisplayedHike(), getApplicationContext());
            } catch (DataManagerException e) {
                Log.d(LOG_FLAG, e.getMessage());
            }
            return filePath;
        }

        @Override
        protected void onPostExecute(String filePath) {
            TextView exportStatusText = (TextView) findViewById(R.id.export_status_text);

            if (filePath != null) {
                exportStatusText.setText(getResources().getString(R.string.export_success));
            } else {
                exportStatusText.setText(getResources().getString(R.string.export_error));
            }
        }
    }

    public void toggleFullScreen() {
        View infoView = findViewById(R.id.info_overview_layout);
        View fullScreenView = findViewById(R.id.image_fullscreen_layout);
        View containerView = findViewById(R.id.info_scrollview);

        // Check which view is currently visible and switch
        if (infoView.getVisibility() == View.VISIBLE) {

            infoView.setVisibility(View.GONE);
            containerView.setBackgroundColor(Color.BLACK);
            fullScreenView.setVisibility(View.VISIBLE);

        } else {

            infoView.setVisibility(View.VISIBLE);
            fullScreenView.setVisibility(View.GONE);
            containerView.setBackgroundColor(Color.WHITE);
        }
    }

}
