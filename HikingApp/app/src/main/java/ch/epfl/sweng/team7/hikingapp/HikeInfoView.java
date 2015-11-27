package ch.epfl.sweng.team7.hikingapp;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.List;

import ch.epfl.sweng.team7.database.DataManager;
import ch.epfl.sweng.team7.database.DataManagerException;
import ch.epfl.sweng.team7.database.HikeData;
import ch.epfl.sweng.team7.database.HikePoint;


/** Class which controls and updates the visual part of the view, not the interaction */
public class HikeInfoView {
    private DataManager dataManager = DataManager.getInstance();

    private final static String LOG_FLAG = "Activity_HikeInfoView";

    private long hikeId;
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

    public HikeInfoView (View view, Context context, long id) {  // add model as argument when creating that
        hikeId = id;

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
        // Add adapter and onclickmethods to the nav drawer listview
        NavigationDrawerListFactory navDrawerListFactory = new NavigationDrawerListFactory(navDrawerList, context);

        galleryImageViews = new ArrayList<>(4);
        /* ABOVE IS A HACK, IMAGES ARE NOT STORED IN THE SERVER YET; RIGHT NOW ACCESS TO
        imageViews.size() IS IN HIKEINFOACTIVITY BUT WE IT'S ASYNC SO WE HAVE AN ERROR:
        EITHER WE STORE NUMBER OF IMAGES IN THE SERVER SO WE CAN CREATE A LIST HERE OR
        ACCESS SIZE ONLY IN ASYNC CALL AND ADD LISTENER
         */

        new GetOneHikeAsync().execute(hikeId);

    }

    private class GetOneHikeAsync extends AsyncTask<Long, Void, HikeData> {

        @Override
        protected HikeData doInBackground(Long... hikeIds) {
            try {
                return dataManager.getHike(hikeIds[0]);
            } catch (DataManagerException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(HikeData result) {
            if (result == null) {
                setErrorState();
                return;
            }
            displayHike(result);
        }

        private void setErrorState() {
            String name = "Hike Data Not Found";
            hikeName.setText(name);
        }

        private void displayHike(HikeData hikeData) {
            final int ELEVATION_POINT_COUNT = 100;
            String name = "The Super Hike";
            double distance = hikeData.getDistance() / 1000;  // in km
            float rating = (float) hikeData.getRating();
            double elevationMin = hikeData.getMinElevation();
            double elevationMax = hikeData.getMaxElevation();

            List<HikePoint> hikePoints = hikeData.getHikePoints();
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
            double lastElapsedTimeInHours = 0;
            for(int i = 0; i < ELEVATION_POINT_COUNT; ++i) {
                HikePoint hikePoint = hikePoints.get((i*hikePoints.size()) / ELEVATION_POINT_COUNT);

                final double elapsedTimeInMilliseconds = (hikePoint.getTime().getTime()
                        - hikePoints.get(0).getTime().getTime());
                final double elapsedTimeInHours = elapsedTimeInMilliseconds / (1000*60*60);

                // Check that data is in ascending order
                if(i > 0 && elapsedTimeInHours <= lastElapsedTimeInHours) {
                    continue;
                }
                lastElapsedTimeInHours = elapsedTimeInHours;

                series.appendData(
                        new DataPoint(elapsedTimeInHours, hikePoint.getElevation()),
                        false, // scrollToEnd
                        hikePoints.size());
            }

            hikeGraph.removeAllSeries(); // remove placeholder series
            hikeGraph.setTitle("Elevation");
            hikeGraph.getGridLabelRenderer().setHorizontalAxisTitle("Hours");

            hikeGraph.addSeries(series);

            // Updating the UI with data
            hikeName.setText(name);

            String distanceString = distance + " km";
            hikeDistance.setText(distanceString);

            hikeRatingBar.setRating(rating);

            String elevationString = "Min: " + elevationMin + "m  " + "Max: " + elevationMax + "m";
            hikeElevation.setText(elevationString);

            loadImageScrollView();
        }

        // create imageviews and add them to the scrollview
        private void loadImageScrollView(){

            // TEMPORARY
            Integer img1 = R.drawable.login_background;

            // add imageviews with images to the scrollview
            for(int i = 0; i<4; i++){

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
    }

    public Button getBackButton() {
        return backButton;
    }

    public RatingBar getHikeRatingBar() {

        return hikeRatingBar;
    }

    public ArrayList<ImageView> getGalleryImageViews() {
        return galleryImageViews;
    }

    public ImageView getMapPreview() {
        return mapPreview;
    }

    public ListView getNavDrawerList() {
        return navDrawerList;
    }

}
