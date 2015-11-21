package ch.epfl.sweng.team7.hikingapp.mapActivityElements;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ch.epfl.sweng.team7.hikingapp.R;

/**
 * Class used to create a display at the bottom
 * of the MapActivity, with some information.
 */
public final class BottomInfoView {

    private static BottomInfoView instance = new BottomInfoView();
    private static final float DEFAULT_TITLE_SIZE = 20f;
    private static final int DEFAULT_BG_COLOR = Color.WHITE;

    private Context context;
    private TableLayout mapTableLayout;
    private TextView title;
    private ArrayList<TextView> infoLines;

    private int lockEntity = -1;

    /**
     * Method has to be called once for initialization,
     * before performing any other operations.
     * @param context Context of the MapActivity
     */
    public void initialize(Context context) {
        this.context = context;
        this.mapTableLayout = new TableLayout(context);
        this.mapTableLayout.setId(R.id.mapTextTable);
        this.mapTableLayout.setBackgroundColor(DEFAULT_BG_COLOR);
        this.mapTableLayout.setVisibility(View.INVISIBLE);
        this.title = new TextView(context);
        this.title.setTextSize(DEFAULT_TITLE_SIZE);
        this.infoLines = new ArrayList<TextView>();

        this.mapTableLayout.addView(title);
    }

    /**
     * Method called to display the information table.
     */
    public void show(int entity) {
        if (permissionGranted(entity)) {
            mapTableLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Metod called to hide the information table
     * @param entity
     */
    public void hide(int entity) {
        if (permissionGranted(entity)) {
            mapTableLayout.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Method returns the View associated with it
     * @return View corresponding to the mapTableLayout
     */
    public View getView() {
        return this.mapTableLayout;
    }

    /**
     * Sets a OnClickListener for the information display.
     * @param listener View.OnClickListener of the display
     */
    public void setOnClickListener(int entity, View.OnClickListener listener) {
        if (permissionGranted(entity)) {
            mapTableLayout.setOnClickListener(listener);
        }
    }

    /**
     * Method called to set the title of the information table.
     * @param title new title for the table.
     */
    public void setTitle(int entity, String title) {
        if (permissionGranted(entity)) {
            this.title.setText(title);
        }
    }

    /**
     * Method called to edit a specific information line.
     * @param index index of the line
     * @param infoMessage new message to display
     */
    public void setInfoLine(int entity, int index, String infoMessage) {
        if (permissionGranted(entity)) {
            try {
                infoLines.get(index).setText(infoMessage);
            } catch (Exception e) {
            }
        }
    }

    /**
     * Method called to add a new information line.
     * @param infoMessage new message to display
     */
    public void addInfoLine(int entity, String infoMessage) {
        if (permissionGranted(entity)) {
            TextView infoView = new TextView(context);
            infoView.setText(infoMessage);
            infoLines.add(infoView);
            mapTableLayout.addView(infoView);
        }
    }

    /**
     * Method called to clear all the information lines.
     */
    public void clearInfoLines(int entity) {
        if(permissionGranted(entity)) {
            for (TextView infoLineView : infoLines) {
                mapTableLayout.removeView(infoLineView);
            }
            infoLines.clear();
        }
    }

    /**
     * Method called to request a lock on this information table,
     * meaning no other entity will be able to edit its values.
     * @param entity ID of the entity requesting the lock
     */
    public void requestLock(int entity) {
        if (permissionGranted(entity)) {
            this.lockEntity = entity;
        }
    }

    /**
     * Method called to release a lock on this information table,
     * meaning all other entities will be, again, able to edit its values.
     * @param entity
     */
    public void releaseLock(int entity) {
        if (permissionGranted(entity)) {
            this.lockEntity = -1;
        }
    }

    public static BottomInfoView getInstance() {
        return instance;
    }

    private BottomInfoView() {
    }

    /**
     * Checks if caller entity has permission over the table.
     * @param entity calling entity
     * @return true if it has permission, false otherwise
     */
    private boolean permissionGranted(int entity) {
        return (this.lockEntity == entity || this.lockEntity == -1);
    }
}
