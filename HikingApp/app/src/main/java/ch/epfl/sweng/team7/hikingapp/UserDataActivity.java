package ch.epfl.sweng.team7.hikingapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

public class UserDataActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);

        // Inflate Navigation Drawer with main content
        View navDrawerView = getLayoutInflater().inflate(R.layout.navigation_drawer, null);
        FrameLayout mainContentFrame = (FrameLayout) findViewById(R.id.main_content_frame);
        View userDataView = getLayoutInflater().inflate(R.layout.activity_user_data, null);
        mainContentFrame.addView(userDataView);

        // load items into the Navigation drawer and add listeners
        ListView navDrawerList = (ListView) findViewById(R.id.nav_drawer);
        NavigationDrawerListFactory navDrawerListFactory =
                new NavigationDrawerListFactory(navDrawerList, navDrawerView.getContext());

        // TODO: add more field when we decide to store more user information
        TextView user_name = (TextView) findViewById(R.id.user_name);
        TextView user_email = (TextView) findViewById(R.id.user_email);
        TextView num_hikes = (TextView) findViewById(R.id.num_hikes);

        // Update view with currently signed-in user's info
        SignedInUser user = SignedInUser.getInstance();
        user_name.setText(user.getUserName());
        user_email.setText(user.getMailAddress());
        num_hikes.setText(getString(R.string.num_hikes_fmt, 100)); // TODO use real data
    }

}
