package ch.epfl.sweng.team7.hikingapp.guiProperties;

import android.app.Activity;
import android.content.Context;
import android.widget.Button;
import android.widget.RelativeLayout;

public final class GUIProperties {
    public static final int DEFAULT_BUTTON_SIZE = 64;
    public static final int DEFAULT_BUTTON_MARGIN = 10;

    public static void setupButton(Context context, int buttonId, int backgroundResource) {
        Button backButton = (Button) ((Activity)context).findViewById(buttonId);
        backButton.setText("");
        backButton.setBackgroundResource(backgroundResource);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) backButton.getLayoutParams();
        lp.width = GUIProperties.DEFAULT_BUTTON_SIZE;
        lp.height = GUIProperties.DEFAULT_BUTTON_SIZE;
        lp.setMargins(GUIProperties.DEFAULT_BUTTON_MARGIN, GUIProperties.DEFAULT_BUTTON_MARGIN, GUIProperties.DEFAULT_BUTTON_MARGIN, GUIProperties.DEFAULT_BUTTON_MARGIN);
    }
}
