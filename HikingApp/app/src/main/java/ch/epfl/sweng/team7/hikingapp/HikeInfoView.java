package ch.epfl.sweng.team7.hikingapp;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polyline;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.epfl.sweng.team7.authentication.SignedInUser;
import ch.epfl.sweng.team7.database.DataManager;
import ch.epfl.sweng.team7.database.DataManagerException;
import ch.epfl.sweng.team7.database.DefaultHikeComment;
import ch.epfl.sweng.team7.database.HikeComment;
import ch.epfl.sweng.team7.database.HikeData;
import ch.epfl.sweng.team7.database.HikePoint;
import ch.epfl.sweng.team7.network.RawHikeComment;


/**
 * Class which controls and updates the visual part of the view, not the interaction
 */
public class HikeInfoView {
    private DataManager dataManager = DataManager.getInstance();

    private final static String LOG_FLAG = "Activity_HikeInfoView";

    private long hikeId;
    private long userId;
    private long hikeOwnerId;
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
    private GoogleMap mapPreview;
    private GraphView hikeGraph;
    private HorizontalScrollView imageScrollView;
    private ListView navDrawerList;
    private ArrayAdapter<String> navDrawerAdapter;
    private LinearLayout commentList;
    private Button exportButton;
    private HikeData displayedHike;
    private View overlayView;
    private LinearLayout root;

    public HikeInfoView(final View view, final Context context, long id, GoogleMap mapHikeInfo) {  // add model as argument when creating that

        hikeId = id;
        userId = SignedInUser.getInstance().getId();

        // initializing UI element in the layout for the HikeInfoView.
        this.context = context;

        hikeName = (TextView) view.findViewById(R.id.hikeinfo_name);

        hikeDistance = (TextView) view.findViewById(R.id.hikeinfo_distance);

        hikeRatingBar = (RatingBar) view.findViewById(R.id.hikeinfo_ratingbar);

        hikeElevation = (TextView) view.findViewById(R.id.hikeinfo_elevation_max_min);

        // Image Gallery
        imgLayout = (LinearLayout) view.findViewById(R.id.image_layout);

        imageScrollView = (HorizontalScrollView) view.findViewById(R.id.imageScrollView);

        fullScreenImage = (ImageView) view.findViewById(R.id.image_fullscreen);

        mapPreview = mapHikeInfo;

        hikeGraph = (GraphView) view.findViewById(R.id.hike_graph);

        imageScrollView = (HorizontalScrollView) view.findViewById(R.id.imageScrollView);

        exportButton = (Button) view.findViewById(R.id.button_export_hike);

        navDrawerList = (ListView) view.findViewById(R.id.nav_drawer);
        // Add adapter and onclickmethods to the nav drawer listview
        NavigationDrawerListFactory navDrawerListFactory = new NavigationDrawerListFactory(navDrawerList, context);

        galleryImageViews = new ArrayList<>();
        /* ABOVE IS A HACK, IMAGES ARE NOT STORED IN THE SERVER YET; RIGHT NOW ACCESS TO
        imageViews.size() IS IN HIKEINFOACTIVITY BUT WE IT'S ASYNC SO WE HAVE AN ERROR:
        EITHER WE STORE NUMBER OF IMAGES IN THE SERVER SO WE CAN CREATE A LIST HERE OR
        ACCESS SIZE ONLY IN ASYNC CALL AND ADD LISTENER
         */

        this.view = view;

        exportButton = (Button) view.findViewById(R.id.button_export_hike);

        Button commentButton = (Button) view.findViewById(R.id.done_edit_comment);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText commentEditText = (EditText) view.findViewById(R.id.comment_text);
                String commentText = commentEditText.getText().toString();
                if (!commentText.isEmpty()) {
                    RawHikeComment rawHikeComment = new RawHikeComment(
                            RawHikeComment.COMMENT_ID_UNKNOWN,
                            hikeId, userId, commentText);
                    DefaultHikeComment comment = new DefaultHikeComment(rawHikeComment);
                    new PostCommentAsync().execute(rawHikeComment);
                    commentEditText.setText("");
                    showNewComment(comment);
                } else {
                    new AlertDialog.Builder(v.getContext())
                            .setMessage(R.string.type_comment);
                }
            }
        });

        commentList = (LinearLayout) view.findViewById(R.id.comments_list);

        root = (LinearLayout) view.findViewById(R.id.hike_info_root_layout);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        overlayView = layoutInflater.inflate(R.layout.hike_info_fullscreen, null);
        backButton = (Button) overlayView.findViewById(R.id.back_button_fullscreen_image);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                root.removeView(overlayView);
            }
        });

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
            displayedHike = result;
        }

        private void setErrorState() {
            String name = "Hike Data Not Found";
            hikeName.setText(name);
        }

        private void displayHike(HikeData hikeData) {
            final int ELEVATION_POINT_COUNT = 100;

            if (mapPreview != null) {
                List<HikeData> hikesToDisplay = Arrays.asList(hikeData);
                List<Polyline> displayedHikes = MapDisplay.displayHikes(hikesToDisplay, mapPreview);
                MapDisplay.displayMarkers(hikesToDisplay, mapPreview);
                MapDisplay.setOnMapClick(false, displayedHikes, mapPreview);

                DisplayMetrics display = context.getResources().getDisplayMetrics();
                int screenHeight = display.heightPixels;
                int screenWidth = display.widthPixels;
                MapDisplay.setCamera(hikesToDisplay, mapPreview, screenWidth, screenHeight / 3);
            }

            double distance = hikeData.getDistance() / 1000;  // in km
            float rating = (float) hikeData.getRating().getDisplayRating();
            Double elevationMin = hikeData.getMinElevation();
            Double elevationMax = hikeData.getMaxElevation();

            Integer elevationMinInteger = elevationMin.intValue();
            Integer elevationMaxInteger = elevationMax.intValue();

            List<HikePoint> hikePoints = hikeData.getHikePoints();
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
            double lastElapsedTimeInHours = 0;
            for (int i = 0; i < ELEVATION_POINT_COUNT; ++i) {
                HikePoint hikePoint = hikePoints.get((i * hikePoints.size()) / ELEVATION_POINT_COUNT);

                final double elapsedTimeInMilliseconds = (hikePoint.getTime().getTime()
                        - hikePoints.get(0).getTime().getTime());
                final double elapsedTimeInHours = elapsedTimeInMilliseconds / (1000 * 60 * 60);

                // Check that data is in ascending order
                if (i > 0 && elapsedTimeInHours <= lastElapsedTimeInHours) {
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

            hikeName.setText(hikeData.getTitle());

            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(1);
            String distanceString = numberFormat.format(distance) + " km";
            hikeDistance.setText(distanceString);

            hikeRatingBar.setRating(rating);

            //String elevationString = "Min: " + String.valueOf(elevationMinInteger) + " m  " + "Max: " + String.valueOf(elevationMaxInteger) + " m";
            String elevationString = String.format(context.getResources().getString(R.string.elevation_min_max), elevationMinInteger, elevationMaxInteger);
            hikeElevation.setText(elevationString);
            hikeOwnerId = hikeData.getOwnerId();

            List<HikeComment> comments = hikeData.getAllComments();
            commentList.removeAllViews();
            for (HikeComment comment : comments) {
                showNewComment(comment);
            }

            exportButton.setVisibility(View.VISIBLE);
            loadImageScrollView();
        }

        // create imageviews and add them to the scrollview
        private void loadImageScrollView() {

            // TEMPORARY
            Integer img1 = R.drawable.login_background;

            // add imageviews with images to the scrollview
            imgLayout.addView(createImageView(img1));


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

            imageView.setOnClickListener(new ImageViewClickListener());

            return imageView;

        }
    }

    private class PostCommentAsync extends AsyncTask<RawHikeComment, Void, Long> {

        @Override
        protected Long doInBackground(RawHikeComment... rawHikeComments) {
            try {
                return dataManager.postComment(rawHikeComments[0]);
            } catch (DataManagerException e) {
                e.printStackTrace();
                return (long) -1;
            }
        }

        @Override
        protected void onPostExecute(Long id) {
            if (id == -1) Log.d("failure", "post comment unsuccessful");
        }
    }

    public Button getBackButton() {
        return backButton;
    }

    public Button getExportButton() {
        return exportButton;
    }

    public RatingBar getHikeRatingBar() {

        return hikeRatingBar;
    }

    public ArrayList<ImageView> getGalleryImageViews() {
        return galleryImageViews;
    }

    public GoogleMap getMapPreview() {
        return mapPreview;
    }

    public ListView getNavDrawerList() {
        return navDrawerList;
    }

    private void showNewComment(HikeComment comment) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View commentRow = inflater.inflate(R.layout.activity_comment_list_adapter, null);
        TextView commentId = (TextView) commentRow
                .findViewById(R.id.comment_userid);
        Log.d("userId", String.valueOf(userId));
        Log.d("ownerId", String.valueOf(hikeOwnerId));
        if (userId == hikeOwnerId) commentId.setTextColor(Color.RED);
        commentId.setText(String.valueOf(comment.getCommentOwnerId()));
        TextView commentText = (TextView) commentRow
                .findViewById(R.id.comment_display_text);
        commentText.setText(comment.getCommentText());
        commentList.addView(commentRow);
    }

    public HikeData getDisplayedHike() {
        return displayedHike;
    }


    public void toggleFullScreen() {
        final View infoView = view.findViewById(R.id.info_overview_layout);

        // Check which view is currently visible and switch
        if (infoView.getVisibility() == View.VISIBLE) {
            root.addView(overlayView, 0);
        } else {
            root.removeView(overlayView);
        }
    }

    public View getOverlayView() {
        return overlayView;
    }

    public LinearLayout getRootLayout() {
        return root;
    }

    private class ImageViewClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // Update image in full screen view
            ImageView imgView = (ImageView) v;
            Drawable drawable = imgView.getDrawable();

            ImageView fullScreenView = (ImageView) view.findViewById(R.id.image_fullscreen);
            fullScreenView.setImageDrawable(drawable);

            toggleFullScreen();
        }
    }

}
