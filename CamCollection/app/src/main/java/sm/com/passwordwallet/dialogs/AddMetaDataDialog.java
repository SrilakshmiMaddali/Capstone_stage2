/**
 * This file is part of Privacy Friendly Password Generator.

 Privacy Friendly Password Generator is free software:
 you can redistribute it and/or modify it under the terms of the
 GNU General Public License as published by the Free Software Foundation,
 either version 3 of the License, or any later version.

 Privacy Friendly Password Generator is distributed in the hope
 that it will be useful, but WITHOUT ANY WARRANTY; without even
 the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Privacy Friendly Password Generator. If not, see <http://www.gnu.org/licenses/>.
 */

package sm.com.passwordwallet.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import sm.com.passwordwallet.R;
import sm.com.passwordwallet.data.DatabaseTask;
import sm.com.passwordwallet.data.MetaDataDatabase;
import sm.com.passwordwallet.data.MetaDataEntity;

public class AddMetaDataDialog extends DialogFragment {

    private View mRootView;
    private MetaDataDatabase mDatabase;
    private boolean mCloseDialog;
    private boolean mVersionVisible;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mRootView = inflater.inflate(R.layout.dialog_add_metadata, null);

        mVersionVisible = false;

        builder.setView(mRootView);
        builder.setIcon(R.mipmap.ic_drawer);
        builder.setTitle(getActivity().getString(R.string.add_metadata_heading));

        mDatabase = MetaDataDatabase.getDatabase(getActivity());

        //Seekbar
        SeekBar seekBarLength = (SeekBar) mRootView.findViewById(R.id.seekBarLength);
        final TextView textViewLengthDisplayFinal = (TextView) mRootView.findViewById(R.id.textViewLengthDisplay);
        seekBarLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewLengthDisplayFinal.setText(Integer.toString(progress + 4));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        ImageButton versionInfoImageButton = (ImageButton) mRootView.findViewById(R.id.versionInfoImageButton);
        versionInfoImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder infoBbuilder = new AlertDialog.Builder(getActivity());
                infoBbuilder.setTitle(getString(R.string.dialog_version_title));
                infoBbuilder.setMessage(R.string.dialog_version);
                infoBbuilder.show();
            }

        });

        Button versionButton = (Button) mRootView.findViewById(R.id.versionButton);
        versionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout versionDataLayout = (LinearLayout) mRootView.findViewById(R.id.versionDataLayout);
                TextView versionTextView = (TextView) mRootView.findViewById(R.id.versionButton);
                if (!mVersionVisible) {
                    versionDataLayout.setVisibility(View.VISIBLE);
                    versionTextView.setText(getString(R.string.change_version_opened));
                    versionTextView.setTextColor(Color.BLACK);
                    mVersionVisible = true;
                } else {
                    versionDataLayout.setVisibility(View.GONE);
                    versionTextView.setText(getString(R.string.change_version_closed));
                    versionTextView.setTextColor(Color.parseColor("#d3d3d3"));
                    mVersionVisible = false;
                }

            }

        });

        EditText iterations = (EditText) mRootView.findViewById(R.id.EditTextIteration);
        iterations.setText("1");

        builder.setPositiveButton(getActivity().getString(R.string.add), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                addMetaData();

            }
        });

        builder.setNegativeButton(getActivity().getString(R.string.cancel), null);

        return builder.create();
    }

    public int boolToInt(boolean b) {
        return b ? 1 : 0;
    }

    public void addMetaData() {

        SeekBar seekBarLength = (SeekBar) mRootView.findViewById(R.id.seekBarLength);
        CheckBox hasNumbersCheckBox = (CheckBox) mRootView.findViewById(R.id.checkBoxNumbers);
        CheckBox hasSymbolsCheckBox = (CheckBox) mRootView.findViewById(R.id.checkBoxSpecialCharacter);
        CheckBox hasLettersUpCheckBox = (CheckBox) mRootView.findViewById(R.id.checkBoxLettersUp);
        CheckBox hasLettersLowCheckBox = (CheckBox) mRootView.findViewById(R.id.checkBoxLettersLow);
        EditText domain = (EditText) mRootView.findViewById(R.id.editTextDomain);
        EditText username = (EditText) mRootView.findViewById(R.id.editTextUsername);
        EditText iterations = (EditText) mRootView.findViewById(R.id.EditTextIteration);

        int iterationToAdd = 1;

        if (iterations.getText().toString().length() > 0) {
            iterationToAdd = Integer.parseInt(iterations.getText().toString());
        }
        if (domain.getText().toString().length() == 0) {
            Toast toast = Toast.makeText(getActivity().getBaseContext(), getString(R.string.add_domain_message), Toast.LENGTH_SHORT);
            toast.show();
            mCloseDialog = false;
        } else if (!(hasNumbersCheckBox.isChecked() || hasSymbolsCheckBox.isChecked() || hasLettersUpCheckBox.isChecked() || hasLettersLowCheckBox.isChecked())) {
            Toast toast = Toast.makeText(getActivity().getBaseContext(), getString(R.string.add_character_message), Toast.LENGTH_SHORT);
            toast.show();
        } else {

            MetaDataEntity metaDataToAdd = new MetaDataEntity(0, 0,
                    domain.getText().toString(),
                    username.getText().toString(),
                    seekBarLength.getProgress() + 4,
                    boolToInt(hasNumbersCheckBox.isChecked()),
                    boolToInt(hasSymbolsCheckBox.isChecked()),
                    boolToInt(hasLettersUpCheckBox.isChecked()),
                    boolToInt(hasLettersLowCheckBox.isChecked()),
                    iterationToAdd);

            DatabaseTask.InsertTask insertTask = new DatabaseTask.InsertTask();
            insertTask.execute(metaDataToAdd);

            getActivity().recreate();

            mCloseDialog = true;

        }


    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addMetaData();
                    if (mCloseDialog) {
                        dismiss();
                    }

                }
            });
        }
    }

}
