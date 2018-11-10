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

package sm.com.camcollection.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import sm.com.camcollection.DialogListener;
import sm.com.camcollection.R;
import sm.com.camcollection.data.MetaDataDatabase;
import sm.com.camcollection.data.MetaDataEntity;

import static sm.com.camcollection.DialogListener.ENTITY_KEY;

public class UpdateMetadataDialog extends DialogFragment {

    private View mRootView;

    private MetaDataDatabase mDatabase;

    private int mPosition;
    private int mId;

    private MetaDataEntity mMetaDataEntity;
    private MetaDataEntity mOldMetaDataEntity;

    private String hash_algorithm;
    private boolean bindToDevice_enabled;
    private int number_iterations;

    private boolean closeDialog;
    private boolean versionVisible;
    private DialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        mRootView = inflater.inflate(R.layout.dialog_update_metadata, null);

        versionVisible = false;

        Bundle bundle = getArguments();

        mPosition = bundle.getInt("position");
        mId = bundle.getInt("id");
        hash_algorithm = bundle.getString("hash_algorithm");
        bindToDevice_enabled = bundle.getBoolean("bindToDevice_enabled");
        mMetaDataEntity = bundle.getParcelable("entity");
        mOldMetaDataEntity = mMetaDataEntity;
        number_iterations = bundle.getInt("number_iterations");

        builder.setView(mRootView);
        setUpData();

        builder.setIcon(R.mipmap.ic_drawer);

        builder.setTitle(getActivity().getString(R.string.add_new_metadata_heading));

        builder.setPositiveButton(getActivity().getString(R.string.save), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                updateMetadata(mOldMetaDataEntity.getPwVersion());

            }
        });

        builder.setNegativeButton(getActivity().getString(R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                cancelUpdate();

            }
        });

        Button versionButton = (Button) mRootView.findViewById(R.id.versionButton);
        versionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout versionDataLayout = (RelativeLayout) mRootView.findViewById(R.id.updateVersionLayout);
                TextView versionTextView = (TextView) mRootView.findViewById(R.id.versionButton);
                TextView textViewIteration = (TextView) mRootView.findViewById(R.id.textViewIteration);
                if (!versionVisible) {
                    versionDataLayout.setVisibility(View.VISIBLE);
                    textViewIteration.setVisibility(View.VISIBLE);
                    versionTextView.setText(getString(R.string.change_version_opened));
                    versionTextView.setTextColor(Color.BLACK);
                    versionVisible = true;
                } else {
                    versionDataLayout.setVisibility(View.GONE);
                    textViewIteration.setVisibility(View.GONE);
                    versionTextView.setText(getString(R.string.change_version_closed));
                    versionTextView.setTextColor(Color.parseColor("#d3d3d3"));
                    versionVisible = false;
                }

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

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the EditNameDialogListener so we can send events to the host
            mListener = (DialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement EditNameDialogListener");
        }
    }

    public void setCheckBox(CheckBox checkbox, int value) {
        if (value == 1) {
            checkbox.setChecked(true);
        }
    }

    /**
     * Displays old metadata and lets user add new metadata
     */
    public void setUpData() {
        EditText domain = (EditText) mRootView.findViewById(R.id.editTextDomainUpdate);
        EditText username = (EditText) mRootView.findViewById(R.id.editTextUsernameUpdate);
        TextView oldVersion = (TextView) mRootView.findViewById(R.id.textViewIteration);
        EditText newVersion = (EditText) mRootView.findViewById(R.id.EditTextIteration);

        CheckBox checkBoxSpecialCharacterUpdate = (CheckBox) mRootView.findViewById(R.id.checkBoxSpecialCharacterUpdate);
        CheckBox checkBoxLettersLowUpdate = (CheckBox) mRootView.findViewById(R.id.checkBoxLettersLowUpdate);
        CheckBox checkBoxLettersUpUpdate = (CheckBox) mRootView.findViewById(R.id.checkBoxLettersUpUpdate);
        CheckBox checkBoxNumbersUpdate = (CheckBox) mRootView.findViewById(R.id.checkBoxNumbersUpdate);

        setCheckBox(checkBoxSpecialCharacterUpdate, mMetaDataEntity.getHasSymbols());
        setCheckBox(checkBoxLettersLowUpdate, mMetaDataEntity.getHasLetterLow());
        setCheckBox(checkBoxLettersUpUpdate, mMetaDataEntity.getHasLettersUp());
        setCheckBox(checkBoxNumbersUpdate, mMetaDataEntity.getHasNumber());

        domain.setText(mMetaDataEntity.getDomain());
        username.setText(mMetaDataEntity.getUserName());

        oldVersion.setText(getString(R.string.old_version, String.valueOf(mMetaDataEntity.getPwVersion())));
        newVersion.setText(String.valueOf(mMetaDataEntity.getPwVersion() + 1));

        TextView textViewLengthDisplayUpdate = (TextView) mRootView.findViewById(R.id.textViewLengthDisplayUpdate);
        textViewLengthDisplayUpdate.setText(Integer.toString(mMetaDataEntity.getLength()));

        final TextView finalTextViewLengthDisplayUpdate = textViewLengthDisplayUpdate;

        SeekBar seekBarLength = (SeekBar) mRootView.findViewById(R.id.seekBarLengthUpdate);
        seekBarLength.setProgress(mMetaDataEntity.getLength() - 4);

        seekBarLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                finalTextViewLengthDisplayUpdate.setText(Integer.toString(progress + 4));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

    public void updateMetadata(int oldIteration) {

        SeekBar seekBarLength = (SeekBar) mRootView.findViewById(R.id.seekBarLengthUpdate);
        CheckBox hasNumbersCheckBox = (CheckBox) mRootView.findViewById(R.id.checkBoxNumbersUpdate);
        CheckBox hasSymbolsCheckBox = (CheckBox) mRootView.findViewById(R.id.checkBoxSpecialCharacterUpdate);
        CheckBox checkBoxLettersLowUpdate = (CheckBox) mRootView.findViewById(R.id.checkBoxLettersLowUpdate);
        CheckBox checkBoxLettersUpUpdate = (CheckBox) mRootView.findViewById(R.id.checkBoxLettersUpUpdate);
        EditText domain = (EditText) mRootView.findViewById(R.id.editTextDomainUpdate);
        EditText username = (EditText) mRootView.findViewById(R.id.editTextUsernameUpdate);
        EditText iteration = (EditText) mRootView.findViewById(R.id.EditTextIteration);

        if (domain.getText().toString().length() == 0) {
            Toast toast = Toast.makeText(getActivity().getBaseContext(), getString(R.string.add_domain_message), Toast.LENGTH_SHORT);
            toast.show();
            closeDialog = false;
        } else if (!(hasNumbersCheckBox.isChecked() || hasSymbolsCheckBox.isChecked() || checkBoxLettersUpUpdate.isChecked() || checkBoxLettersLowUpdate.isChecked())) {
            Toast toast = Toast.makeText(getActivity().getBaseContext(), getString(R.string.add_character_message), Toast.LENGTH_SHORT);
            toast.show();
        } else {

            int tempIteration;

            if (iteration.getText().length() == 0) {
                tempIteration = oldIteration + 1;
            } else {
                tempIteration = Integer.parseInt(iteration.getText().toString());
            }
            /*DatabaseTask.updateMetaDataTask task = new DatabaseTask.updateMetaDataTask();

            task.execute();*/
            Bundle updateBundle = new Bundle();
            updateBundle.putParcelable(ENTITY_KEY, new MetaDataEntity(mId, mPosition,
                    domain.getText().toString(),
                    username.getText().toString(),
                    seekBarLength.getProgress() + 4,
                    boolToInt(hasNumbersCheckBox.isChecked()),
                    boolToInt(hasSymbolsCheckBox.isChecked()),
                    boolToInt(checkBoxLettersUpUpdate.isChecked()),
                    boolToInt(checkBoxLettersLowUpdate.isChecked()),
                    tempIteration));
            mListener.onfinisheditdialog(DialogListener.UPDATE_METADATA, updateBundle);

            Toast.makeText(getActivity(), getString(R.string.added_message), Toast.LENGTH_SHORT).show();

            Bundle bundle = new Bundle();
            bundle.putInt("position", mPosition);
            bundle.putString("hash_algorithm", hash_algorithm);
            bundle.putInt("number_iterations", number_iterations);
            bundle.putBoolean("bindToDevice_enabled", bindToDevice_enabled);
            bundle.putString("olddomain", mOldMetaDataEntity.getDomain());
            bundle.putString("oldusername", mOldMetaDataEntity.getUserName());

            bundle.putInt("oldlength", mOldMetaDataEntity.getLength());
            bundle.putInt("oldlettersup", mOldMetaDataEntity.getHasLettersUp());
            bundle.putInt("oldletterslow", mOldMetaDataEntity.getHasLetterLow());
            bundle.putInt("oldsymbols", mOldMetaDataEntity.getHasSymbols());
            bundle.putInt("oldnumbers", mOldMetaDataEntity.getHasNumber());
            bundle.putInt("olditeration", mOldMetaDataEntity.getPwVersion());
            FragmentManager fragmentManager = getFragmentManager();
            UpdatePasswordDialog updatePasswordDialog = new UpdatePasswordDialog();
            updatePasswordDialog.setArguments(bundle);
            updatePasswordDialog.show(fragmentManager, "UpdatePasswordDialog");

            closeDialog = true;
        }
    }

    public void cancelUpdate() {

        Toast.makeText(getActivity(), getString(R.string.canceled_message), Toast.LENGTH_SHORT).show();
        this.dismiss();
    }

    public int boolToInt(boolean b) {
        return b ? 1 : 0;
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
                    updateMetadata(mOldMetaDataEntity.getPwVersion());
                    if (closeDialog) {
                        dismiss();
                    }

                }
            });
        }
    }

}

