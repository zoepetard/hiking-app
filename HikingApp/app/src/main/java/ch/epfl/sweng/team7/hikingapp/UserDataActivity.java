package ch.epfl.sweng.team7.hikingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class UserDataActivity extends Activity {
    private final static int SELECT_PICTURE = 1;

    private int mUserId;
    private ImageView profilePic;

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
        TextView userName = (TextView) findViewById(R.id.user_name);
        TextView userEmail = (TextView) findViewById(R.id.user_email);
        String nname = getIntent().getStringExtra(ChangeNicknameActivity.EXTRA_MESSAGE);
        TextView nickname = (TextView) findViewById(R.id.nickname);
        TextView numHikes = (TextView) findViewById(R.id.num_hikes);
        Button changeNickname = (Button) findViewById(R.id.change_nickname);
        changeNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ChangeNicknameActivity.class);
                startActivity(i);
            }
        });

        profilePic = (ImageView) findViewById(R.id.profile_pic);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setMessage(R.string.new_profile)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent,
                                        getString(R.string.selete_pic)), SELECT_PICTURE);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        // TODO: use real data stored in local cache after issue #56 is in master
        // for user with this user_id
        userName.setText("Team 7");
        userEmail.setText("team7@epfl.ch");
        if (nname == null) {
            nickname.setText(getString(R.string.nickname_fmt, "team7"));
        } else {
            nickname.setText(getString(R.string.nickname_fmt, nname));
        }
        numHikes.setText(getString(R.string.num_hikes_fmt, 100));

        Button back_button = (Button) findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                profilePic.setImageURI(selectedImageUri);
                ImageView sidePanelPic = (ImageView)findViewById(R.id.profile_pic_side_panel);
                sidePanelPic.setImageURI(selectedImageUri);
            }
        }
    }
}
