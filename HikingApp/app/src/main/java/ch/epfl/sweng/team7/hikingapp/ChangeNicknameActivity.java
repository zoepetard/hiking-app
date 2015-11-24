package ch.epfl.sweng.team7.hikingapp;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;

public class ChangeNicknameActivity extends Activity {

    private final static String LOG_FLAG = "Activity_ChangeNickname";
    public final static String EXTRA_MESSAGE =
            "ch.epfl.sweng.team7.hikingapp.NICKNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_nickname);
    }


    public void saveName(View view) {
        Intent intent = new Intent(this, UserDataActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_nickname);
        String name = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, name);
        startActivity(intent);
    }

}
