package ch.epfl.sweng.team7.hikingapp.mapActivityElements;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import ch.epfl.sweng.team7.hikingapp.R;

public final class BottomInfoView {

    private static BottomInfoView instance = new BottomInfoView();
    private static final float DEFAULT_TITLE_SIZE = 20f;
    private static final int DEFAULT_BG_COLOR = Color.WHITE;

    private Context context;
    private TableLayout mapTableLayout;
    private TextView title;
    private TextView infoLine1;
    private TextView infoLine2;

    public void initialize(Context context) {
        this.context = context;
        this.mapTableLayout = new TableLayout(context);
        this.mapTableLayout.setId(R.id.mapTextTable);
        this.mapTableLayout.setBackgroundColor(DEFAULT_BG_COLOR);
        this.mapTableLayout.setVisibility(View.INVISIBLE);
        this.title = new TextView(context);
        this.title.setTextSize(DEFAULT_TITLE_SIZE);
        this.infoLine1 = new TextView(context);
        this.infoLine2 = new TextView(context);

        this.mapTableLayout.addView(title);
        this.mapTableLayout.addView(infoLine1);
        this.mapTableLayout.addView(infoLine2);
    }

    public void show() {
        mapTableLayout.setVisibility(View.VISIBLE);
    }

    public void hide() {
        mapTableLayout.setVisibility(View.INVISIBLE);
    }

    public View getView() {
        return this.mapTableLayout;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mapTableLayout.setOnClickListener(listener);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setInfoLine1(String infoMessage) {
        this.infoLine1.setText(infoMessage);
    }

    public void setInfoLine2(String infoMessage) {
        this.infoLine2.setText(infoMessage);
    }

    public static BottomInfoView getInstance() {
        return instance;
    }

    private BottomInfoView() {
    }
}
