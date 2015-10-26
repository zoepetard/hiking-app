package ch.epfl.sweng.team7.hikingapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;

public class HikeInfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike_info);


        View view = findViewById(android.R.id.content); // current view

        HikeInfoView hikeInfoView = new HikeInfoView(view,this);

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
        for(int i = 0; i<hikeInfoView.imageViews.size(); i++){

            ImageView imgView = hikeInfoView.imageViews.get(i);
            imgView.setOnClickListener(new imageViewClickListener());

        }

    }

    private class imageViewClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // do something

        }
    }
}
