package ch.epfl.sweng.team7.hikingapp;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;


/**

Class which controls and updates the visual part of the view, not the interaction.

*/


public class HikeInfoView {


    TextView hikeName;
    TextView hikeDistance;
    RatingBar hikeRatingBar;
    LinearLayout imgLayout;
    TextView hikeElevation;
    View view;
    Context context;
    ArrayList<ImageView> imageViews; // List to store the imageviews that are created for the scrollview to make them accessible in controller.

    public HikeInfoView (View view, Context context) {  // add model as argument when creating that

        // Initializing UI element in the layout for the HikeInfoView.
        this.context = context;

        hikeName = (TextView) view.findViewById(R.id.hikeinfo_name);

        hikeDistance = (TextView) view.findViewById(R.id.hikeinfo_distance);

        hikeRatingBar = (RatingBar) view.findViewById(R.id.hikeinfo_ratingbar);

        hikeElevation = (TextView) view.findViewById(R.id.hikeinfo_elevation_max_min);

        // Image Gallery
        imgLayout = (LinearLayout) view.findViewById(R.id.image_layout);

        update();

    }

    // method to update in UI elements
    public void update(){

         /*
        Temporary example data!
        This data will be stored and accessed differently later in the project.
        Start
        */

        String name = "The Super Hike";
        double distance = 10.3;  // in km
        float rating = 3;
        int elevationMin = 1500;
        int elevationMax = 2100;

        /*
        END
         */

        // Updating the UI with data

        hikeName.setText(name);

        hikeDistance.setText(distance + " km");

        hikeRatingBar.setRating(rating);

        hikeElevation.setText("Min: " + elevationMin + "m  " + "Max: " + elevationMax + "m");

        loadImageScrollView();

    }

    // create imageviews and add them to the scrollview
    private void loadImageScrollView(){


        imageViews = new ArrayList<>();

        // TEMPORARY
        Integer img1 = R.drawable.login_background;

        // add imageviews with images to the scrollview
        for(int i = 0; i<4; i++){

            imgLayout.addView(createImageView(img1));

        }
    }



    private View createImageView(Integer img){

        // creating an ImageView and applying layout parameters
        ImageView imageView = new ImageView(context.getApplicationContext());

        //
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        imageView.setAdjustViewBounds(true); // set this to true to preserve aspect ratio of image.
        layoutParams.setMargins(10,10,10,10); // Margin around each image

        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE); // scaling down image to fit inside view
        imageView.setImageResource(img);

        return imageView;

    }
}
