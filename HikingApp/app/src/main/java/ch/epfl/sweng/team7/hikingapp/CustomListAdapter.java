package ch.epfl.sweng.team7.hikingapp;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ch.epfl.sweng.team7.database.HikeData;

/**
 * Created by zoepetard on 06/12/15.
 */
public class CustomListAdapter extends BaseAdapter {

    Context context;
    List<HikeData> mHikes;

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

        DisplayMetrics display = context.getResources().getDisplayMetrics();
        int screenHeight = display.heightPixels;
        int screenWidth = display.widthPixels;

        ImageView mapImage = (ImageView) rowView.findViewById(R.id.hikeMap);
        ViewGroup.LayoutParams mapParams = mapImage.getLayoutParams();
        mapParams.height = screenHeight / 5;
        mapParams.width = screenWidth / 3;

        TextView nameText = (TextView) rowView.findViewById(R.id.nameRow);
        nameText.setText(hikeData.getTitle());

        TextView distanceText = (TextView) rowView.findViewById(R.id.distanceRow);
        distanceText.setText(context.getResources().getString(R.string.hikeDistanceText, (long) hikeData.getDistance() / 1000));

        TextView ratingText = (TextView) rowView.findViewById(R.id.ratingRow);
        ratingText.setText(context.getResources().getString(R.string.hikeRatingText, (long) hikeData.getRating().getDisplayRating()));

        return rowView;
    }

}
