package ch.epfl.sweng.team7.hikingapp;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;


/**
 * Class which controls and updates the visual part of the view, not the interaction.
 */


public class HikeInfoView {

    private final static String LOG_FLAG = "Activity_HikeInfoView";

    private TextView hikeName;
    private TextView hikeDistance;
    private RatingBar hikeRatingBar;
    private LinearLayout imgLayout;
    private TextView hikeElevation;
    private View view;
    private Context context;
    private ArrayList<ImageView> galleryImageViews; // make ImageViews accessible in controller.
    private Button backButton;
    private ImageView fullScreenImage;
    private ImageView mapPreview;
    private GraphView hikeGraph;
    private HorizontalScrollView imageScrollView;
    private ListView navDrawerList;
    private ArrayAdapter<String> navDrawerAdapter;


    public HikeInfoView(View view, Context context) {  // add model as argument when creating that

        // initializing UI element in the layout for the HikeInfoView.
        this.context = context;

        hikeName = (TextView) view.findViewById(R.id.hikeinfo_name);

        hikeDistance = (TextView) view.findViewById(R.id.hikeinfo_distance);

        hikeRatingBar = (RatingBar) view.findViewById(R.id.hikeinfo_ratingbar);

        hikeElevation = (TextView) view.findViewById(R.id.hikeinfo_elevation_max_min);

        // Image Gallery
        imgLayout = (LinearLayout) view.findViewById(R.id.image_layout);

        backButton = (Button) view.findViewById(R.id.back_button_fullscreen_image);

        fullScreenImage = (ImageView) view.findViewById(R.id.image_fullscreen);

        mapPreview = (ImageView) view.findViewById(R.id.map_preview_imageview);

        hikeGraph = (GraphView) view.findViewById(R.id.hike_graph);

        imageScrollView = (HorizontalScrollView) view.findViewById(R.id.imageScrollView);

        navDrawerList = (ListView) view.findViewById(R.id.nav_drawer);

        update();

    }

    // method to update info in UI elements
    public void update() {

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

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 1500),
                new DataPoint(1, 1800),
                new DataPoint(2, 1900),
                new DataPoint(3, 2100),
                new DataPoint(4, 2000)
        });

        hikeGraph.removeAllSeries(); // remove placeholder series
        hikeGraph.setTitle("Elevation");
        hikeGraph.getGridLabelRenderer().setHorizontalAxisTitle("Hours");

        hikeGraph.addSeries(series);

        /*
        END
         */

        // Updating the UI with data
        hikeName.setText(name);

        String distanceString = distance + " km";
        hikeDistance.setText(distanceString);

        hikeRatingBar.setRating(rating);

        String elevationString = "Min: " + elevationMin + "m  " + "Max: " + elevationMax + "m";
        hikeElevation.setText(elevationString);

        loadImageScrollView();

        // Add adapter and onclickmethods to the nav drawer listview
        NavigationDrawerListFactory navDrawerListFactory = new NavigationDrawerListFactory(navDrawerList,context);

    }


    // create imageviews and add them to the scrollview
    private void loadImageScrollView() {

        galleryImageViews = new ArrayList<>();

        // TEMPORARY
        Integer img1 = R.drawable.login_background;

        // add imageviews with images to the scrollview
        for (int i = 0; i < 4; i++) {

            imgLayout.addView(createImageView(img1));

        }
    }


    private View createImageView(Integer img) {

        // creating an ImageView and applying layout parameters
        ImageView imageView = new ImageView(context.getApplicationContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        imageView.setAdjustViewBounds(true); // set this to true to preserve aspect ratio of image.
        layoutParams.setMargins(10, 10, 10, 10); // Margin around each image
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE); // scaling down image to fit inside view
        imageView.setImageResource(img);
        galleryImageViews.add(imageView);

        return imageView;

    }

    public Button getBackButton()
    {
        return backButton;
    }

    public RatingBar getHikeRatingBar()
    {

        return hikeRatingBar;
    }

    public ArrayList<ImageView> getGalleryImageViews()
    {
        return galleryImageViews;
    }

    public ImageView getMapPreview()
    {
        return mapPreview;
    }

    public ListView getNavDrawerList()
    {
        return navDrawerList;
    }




}
