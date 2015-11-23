package ch.epfl.sweng.team7.hikingapp;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Activity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import ch.epfl.sweng.team7.gpsService.GPSManager;

public final class HikeInfoActivity extends Activity {
    private long hikeId;

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
        EditText hikeName  = (EditText) findViewById(R.id.hikeinfo_name);
        //hikeName.setInputType(InputType.TYPE_CLASS_TEXT);

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

        HikeInfoView hikeInfoView = new HikeInfoView(view, this, hikeId);
        // set listener methods for UI elements in HikeInfoView
        hikeInfoView.getHikeRatingBar().setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                    /* Here we would actually save the new rating in our Data Model and let it notify us of the change.
                    There won't be a need to update the UI directly from here.
                    */

                ratingBar.setRating(rating);

            }
        });

        // Setting a listener for each imageview.
        for (int i = 0; i < hikeInfoView.getGalleryImageViews().size(); i++) {

            ImageView imgView = hikeInfoView.getGalleryImageViews().get(i);
            imgView.setOnClickListener(new ImageViewClickListener());
        }

        hikeInfoView.getBackButton().setOnClickListener(new BackButtonClickListener());

        hikeInfoView.getMapPreview().setOnClickListener(new MapPreviewClickListener());
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

    private class MapPreviewClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // segue to map activity!

        }
    }

    private class BackButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            toggleFullScreen();
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
