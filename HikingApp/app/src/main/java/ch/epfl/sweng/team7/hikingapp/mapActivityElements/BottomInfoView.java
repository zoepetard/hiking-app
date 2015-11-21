package ch.epfl.sweng.team7.hikingapp.mapActivityElements;

import android.content.Context;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

public class BottomInfoView {

    private static final float DEFAULT_TITLE_SIZE = 20f;

    private Context context;
    private TableLayout mapTableLayout;
    private TextView title;
    private TextView infoLine1;
    private TextView infoLine2;

    public BottomInfoView(Context context) {
        this.context = context;
        this.mapTableLayout = new TableLayout(context);
        this.title = new TextView(context);
        this.title.setTextSize(DEFAULT_TITLE_SIZE);
        this.infoLine1 = new TextView(context);
        this.infoLine2 = new TextView(context);
    }

    public void show() {
        mapTableLayout.setVisibility(View.VISIBLE);
    }

    public void hide() {
        mapTableLayout.setVisibility(View.INVISIBLE);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mapTableLayout.setOnClickListener(listener);
    }

    private void setTitle(String title) {
        this.title.setText(title);
    }

    private void setInfoLine1(String infoMessage) {
        this.infoLine1.setText(infoMessage);
    }

    private void setInfoLine2(String infoMessage) {
        this.infoLine2.setText(infoMessage);
    }
}
