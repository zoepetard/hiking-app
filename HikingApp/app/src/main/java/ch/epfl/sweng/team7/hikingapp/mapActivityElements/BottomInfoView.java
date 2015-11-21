package ch.epfl.sweng.team7.hikingapp.mapActivityElements;

import android.content.Context;
import android.widget.TableLayout;
import android.widget.TextView;

public class BottomInfoView {

    private Context context;
    private TableLayout mapTableLayout;
    private TextView title;
    private TextView infoLine1;
    private TextView infoLine2;

    public BottomInfoView(Context context) {
        this.context = context;
        this.title = new TextView(context);
        this.infoLine1 = new TextView(context);
        this.infoLine2 = new TextView(context);
    }

    private void setTitle(String title) {

    }

    private void setInfoLine1(String infoMessage) {

    }

    private void setInfoLine2(String infoMessage) {

    }
}
