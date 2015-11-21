package ch.epfl.sweng.team7.hikingapp.mapActivityElements;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ch.epfl.sweng.team7.hikingapp.R;

public final class BottomInfoView {

    private static BottomInfoView instance = new BottomInfoView();
    private static final float DEFAULT_TITLE_SIZE = 20f;
    private static final int DEFAULT_BG_COLOR = Color.WHITE;

    private Context context;
    private TableLayout mapTableLayout;
    private TextView title;
    private ArrayList<TextView> infoLines;

    private int lockEntity = -1;

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

    public void show(int entity) {
        if (permissionGranted(entity)) {
            mapTableLayout.setVisibility(View.VISIBLE);
        }
    }

    public void hide(int entity) {
        if (permissionGranted(entity)) {
            mapTableLayout.setVisibility(View.INVISIBLE);
        }
    }

    public View getView() {
        return this.mapTableLayout;
    }

    public void setOnClickListener(int entity, View.OnClickListener listener) {
        if (permissionGranted(entity)) {
            mapTableLayout.setOnClickListener(listener);
        }
    }

    public void setTitle(int entity, String title) {
        if (permissionGranted(entity)) {
            this.title.setText(title);
        }
    }

    public void setInfoLine(int entity, int index, String infoMessage) {
        if (permissionGranted(entity)) {
            try {
                infoLines.get(index).setText(infoMessage);
            } catch (Exception e) {
            }
        }
    }

    public void addInfoLine(int entity, String infoMessage) {
        if (permissionGranted(entity)) {
            TextView infoView = new TextView(context);
            infoView.setText(infoMessage);
            infoLines.add(infoView);
            mapTableLayout.addView(infoView);
        }
    }

    public void clearInfoLines(int entity) {
        if(permissionGranted(entity)) {
            for (TextView infoLineView : infoLines) {
                mapTableLayout.removeView(infoLineView);
            }
            infoLines.clear();
        }
    }

    public void requestLock(int entity) {
        if (permissionGranted(entity)) {
            this.lockEntity = entity;
        }
    }

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

    private boolean permissionGranted(int entity) {
        return (this.lockEntity == entity || this.lockEntity == -1);
    }
}
