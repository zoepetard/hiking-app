package ch.epfl.sweng.team7.hikingapp;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;

public final class HikeInfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);

        View view = findViewById(android.R.id.content);

        // load main content into the navigations drawer's framelayout
        FrameLayout mainContentFrame = (FrameLayout) findViewById(R.id.main_content_frame);

        View hikeInfoLayout = getLayoutInflater().inflate(R.layout.activity_hike_info,null);

        mainContentFrame.addView(hikeInfoLayout);

        HikeInfoView hikeInfoView = new HikeInfoView(view, this);

        // set listener methods for UI elements in HikeInfoView
        hikeInfoView.hikeRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                    /* Here we would actually save the new rating in our Data Model and let it notify us of the change.
                    There won't be a need to update the UI directly from here.
                    */

                ratingBar.setRating(rating);

            }
        });

        // Setting a listener for each imageview.
        for (int i = 0; i < hikeInfoView.imageViews.size(); i++) {

            ImageView imgView = hikeInfoView.imageViews.get(i);
            imgView.setOnClickListener(new ImageViewClickListener());
        }

        hikeInfoView.backButton.setOnClickListener(new BackButtonClickListener());

        hikeInfoView.mapPreview.setOnClickListener(new MapPreviewClickListener());

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
