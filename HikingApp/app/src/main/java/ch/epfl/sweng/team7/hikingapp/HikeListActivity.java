package ch.epfl.sweng.team7.hikingapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HikeListActivity extends Activity {


    //Displays a list of nearby hikes, with a map, the distance and the rating.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike_list);

        int nearbyHikes = 5;
        TableLayout hikeListTable = (TableLayout)findViewById((R.id.hikeListTable));

        for (int i = 0; i < nearbyHikes; i++) {
            TableRow hikeRow = getHikeRow(i);
            hikeListTable.addView(hikeRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hike_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Creates and returns a TableRow with information about a hike.
    public TableRow getHikeRow(int i) {

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        TableRow hikeRow = new TableRow(this);
        hikeRow.setTag("hikeRow" + Integer.toString(i));
        hikeRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

        GridLayout.Spec row1 = GridLayout.spec(0);
        GridLayout.Spec row2 = GridLayout.spec(1);
        GridLayout.Spec row3 = GridLayout.spec(2);
        GridLayout.Spec rowSpan = GridLayout.spec(0,3);

        GridLayout.Spec col1 = GridLayout.spec(0);
        GridLayout.Spec col2 = GridLayout.spec(1);

        GridLayout gridLayout = new GridLayout(this);
        gridLayout.setRowCount(3);
        gridLayout.setColumnCount(2);

        //map column
        GridLayout.LayoutParams mapColumn = new GridLayout.LayoutParams(rowSpan, col1);
        mapColumn.width = screenWidth/3;
        mapColumn.height = screenHeight/5;
        TextView mapText = new TextView(this);
        mapText.setText("Map for hike " + Integer.toString(i + 1) + " goes here.");
        mapText.setLayoutParams(mapColumn);
        gridLayout.addView(mapText,mapColumn);

        //name row
        GridLayout.LayoutParams nameRow = new GridLayout.LayoutParams(row1, col2);
        TextView nameText = new TextView(this);
        nameText.setText("Hike #" + Integer.toString(i + 1));
        nameText.setLayoutParams(nameRow);
        gridLayout.addView(nameText,nameRow);

        //distance row
        GridLayout.LayoutParams distanceRow = new GridLayout.LayoutParams(row2, col2);
        TextView distanceText = new TextView(this);
        distanceText.setText("Distance: " + Integer.toString((i+1)*5) + "km");
        distanceText.setLayoutParams(distanceRow);
        gridLayout.addView(distanceText,distanceRow);

        //rating row
        GridLayout.LayoutParams ratingRow = new GridLayout.LayoutParams(row3, col2);
        TextView ratingText = new TextView(this);
        ratingText.setText("Rating: " + Integer.toString(i));
        ratingText.setLayoutParams(ratingRow);
        gridLayout.addView(ratingText,ratingRow);

        hikeRow.addView(gridLayout);
        return hikeRow;
    }

    public void backToMap(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

}
