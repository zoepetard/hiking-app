package ch.epfl.sweng.team7.hikingapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

public class UserDataActivity extends Activity {
    private int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);

        // Inflate Navigation Drawer with main content
        View navDrawerView = getLayoutInflater().inflate(R.layout.navigation_drawer,null);
        FrameLayout mainContentFrame = (FrameLayout) findViewById(R.id.main_content_frame);
        View userDataView = getLayoutInflater().inflate(R.layout.activity_user_data, null);
        mainContentFrame.addView(userDataView);

        // load items into the Navigation drawer and add listeners
        ListView navDrawerList = (ListView) findViewById(R.id.nav_drawer);
        NavigationDrawerListFactory navDrawerListFactory = new NavigationDrawerListFactory(navDrawerList,navDrawerView.getContext());

        // TODO: add more field when we decide to store more user information
        TextView user_name = (TextView) findViewById(R.id.user_name);
        TextView user_email = (TextView) findViewById(R.id.user_email);
        TextView num_hikes = (TextView) findViewById(R.id.num_hikes);

        // TODO: use real data stored in local cache after issue #56 is in master
        // for user with this user_id
        user_name.setText("Team 7");
        user_email.setText("team7@epfl.ch");
        num_hikes.setText(getString(R.string.num_hikes_fmt, 100));

        Button back_button = (Button) findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
