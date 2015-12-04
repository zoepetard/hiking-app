package ch.epfl.sweng.team7.hikingapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TextView;

public class ChangeNicknameDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_change_nickname, null))
                // Add action buttons
                .setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText nicknameET = (EditText) getActivity().
                                findViewById(R.id.change_nickname);
                        String name = nicknameET.getText().toString();
                        if (!name.equals("")) {
                            TextView nickname = (TextView) getActivity().findViewById(R.id.user_name);
                            nickname.setText(name);
                            UserDataActivity.changeUserName(name);
                            ChangeNicknameDialog.this.getDialog().cancel();
                        }
                    }
                })
                .setNegativeButton(R.string.button_cancel_save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ChangeNicknameDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
