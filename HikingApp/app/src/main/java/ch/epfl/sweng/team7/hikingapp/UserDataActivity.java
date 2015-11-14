package ch.epfl.sweng.team7.hikingapp;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

public class UserDataActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);

        // add more field when we decide to store more user information
        TextView user_name = (TextView) findViewById(R.id.user_name);
        TextView user_email = (TextView) findViewById(R.id.user_email);
        TextView num_hikes = (TextView) findViewById(R.id.num_hikes);

        // use real data stored in local cache after issue #56 is in master
        user_name.setText("Team 7");
        user_email.setText("team7@epfl.ch");
        num_hikes.setText(getString(R.string.num_hikes_fmt, 100));
    }

}
