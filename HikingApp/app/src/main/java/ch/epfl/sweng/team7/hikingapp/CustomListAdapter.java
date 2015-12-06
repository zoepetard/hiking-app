package ch.epfl.sweng.team7.hikingapp;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Polyline;

import java.util.Arrays;
import java.util.List;

import ch.epfl.sweng.team7.database.HikeData;

/**
 * Created by zoepetard on 06/12/15.
 */
public class CustomListAdapter extends BaseAdapter {

    HikeListActivity context;
    List<HikeData> mHikes;
    int mapHeight = 0;
    int mapWidth = 0;

    private static LayoutInflater inflater=null;
    public CustomListAdapter(HikeListActivity hikeListActivity, List<HikeData> hikes) {
        mHikes = hikes;
        context = hikeListActivity;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mHikes.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        HikeData hikeData = mHikes.get(position);
        View rowView;
        rowView = inflater.inflate(R.layout.activity_hike_listview, null);
        ViewHolder holder;

        DisplayMetrics display = context.getResources().getDisplayMetrics();
        int screenHeight = display.heightPixels;
        int screenWidth = display.widthPixels;
        mapHeight = screenHeight / 5;
        mapWidth = screenWidth / 3;

        holder = new ViewHolder();
        holder.mapView = (MapView) rowView.findViewById(R.id.mapHikeList);
        holder.initializeMapView(hikeData);

        ViewGroup.LayoutParams mapParams = holder.mapView.getLayoutParams();
        mapParams.height = mapHeight;
        mapParams.width = mapWidth;

        TextView nameText = (TextView) rowView.findViewById(R.id.nameRow);
        nameText.setText(hikeData.getTitle());

        TextView distanceText = (TextView) rowView.findViewById(R.id.distanceRow);
        distanceText.setText(context.getResources().getString(R.string.hikeDistanceText, (long) hikeData.getDistance() / 1000));

        TextView ratingText = (TextView) rowView.findViewById(R.id.ratingRow);
        ratingText.setText(context.getResources().getString(R.string.hikeRatingText, (long) hikeData.getRating().getDisplayRating()));

        return rowView;
    }

    class ViewHolder implements OnMapReadyCallback {
        MapView mapView;
        GoogleMap map;
        HikeData mHikeData;

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(context.getApplicationContext());
            map = googleMap;
            List<HikeData> hikesToDisplay = Arrays.asList(mHikeData);
            List<Polyline> displayedHikes = MapDisplay.displayHikes(hikesToDisplay, map);
            MapDisplay.displayMarkers(hikesToDisplay, map);
            MapDisplay.setOnMapClick(false, displayedHikes, map);
            MapDisplay.setCamera(hikesToDisplay, map, mapWidth, mapHeight);
        }

        public void initializeMapView(HikeData hikeData) {
            if (mapView != null) {
                mHikeData = hikeData;
                // Initialise the MapView
                mapView.onCreate(null);
                mapView.onResume();
                // Set the map ready callback to receive the GoogleMap object
                mapView.getMapAsync(this);
            }
        }

    }

}
