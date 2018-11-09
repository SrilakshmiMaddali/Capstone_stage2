/**
 * This file is part of Privacy Friendly Password Generator.
 * <p>
 * Privacy Friendly Password Generator is free software:
 * you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or any later version.
 * <p>
 * Privacy Friendly Password Generator is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Privacy Friendly Password Generator. If not, see <http://www.gnu.org/licenses/>.
 */

package sm.com.camcollection.dialogs;


import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import sm.com.camcollection.R;
import sm.com.camcollection.data.DatabaseTask;
import sm.com.camcollection.data.MetaDataDatabase;
import sm.com.camcollection.data.MetaDataEntity;
import sm.com.camcollection.data.MetaDataViewModel;
import sm.com.camcollection.generator.PasswordGeneratorTask;

import static android.content.Context.CLIPBOARD_SERVICE;

public class GeneratePasswordDialog extends DialogFragment {

    private View mRootView;

    private MetaDataDatabase mDatabase;

    private int mPosition;
    private MetaDataEntity mMetaDataEntity;

    private Boolean bindToDevice_enabled;
    private Boolean clipboard_enabled;
    private String hashAlgorithm;
    private int number_iterations;

    private boolean visibility;
    private ImageButton mVisibilityButton;

    private EditText mEditTextMasterpassword;

    ProgressBar mSpinner;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mRootView = inflater.inflate(R.layout.dialog_generate_password, null);

        Bundle bundle = getArguments();

        mPosition = bundle.getInt("position");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        clipboard_enabled = sharedPreferences.getBoolean("clipboard_enabled", false);
        bindToDevice_enabled = bundle.getBoolean("bindToDevice_enabled");
        hashAlgorithm = bundle.getString("hash_algorithm");
        number_iterations = bundle.getInt("number_iterations");
        mMetaDataEntity = bundle.getParcelable("entity");
        visibility = false;

        mSpinner = (ProgressBar) mRootView.findViewById(R.id.progressBar);
        mSpinner.setVisibility(View.GONE);
        create(builder);
        return builder.create();
    }

    private AlertDialog.Builder  create(AlertDialog.Builder builder) {
        TextView domain = (TextView) mRootView.findViewById(R.id.domainHeadingTextView);
        domain.setText(mMetaDataEntity.getDomain());

        TextView username = (TextView) mRootView.findViewById(R.id.domainUsernameTextView);

        username.setText(mMetaDataEntity.getUserName());

        TextView iteration = (TextView) mRootView.findViewById(R.id.textViewIteration);
        iteration.setText(String.valueOf(mMetaDataEntity.getPwVersion()));

        builder.setView(mRootView);
        builder.setIcon(R.mipmap.ic_drawer);
        builder.setTitle(getActivity().getString(R.string.generate_heading));
        builder.setPositiveButton(getActivity().getString(R.string.done), null);

        Button generateButton = (Button) mRootView.findViewById(R.id.generatorButton);
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView textViewPassword = (TextView) mRootView.findViewById(R.id.textViewPassword);
                textViewPassword.setText("");

                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);

                mEditTextMasterpassword = (EditText) mRootView.findViewById(R.id.editTextMasterpassword);

                if (mEditTextMasterpassword.getText().toString().length() == 0) {
                    Toast toast = Toast.makeText(getActivity().getBaseContext(), getString(R.string.enter_masterpassword), Toast.LENGTH_SHORT);
                    toast.show();
                } else if (mEditTextMasterpassword.getText().toString().length() < 8) {
                    Toast toast = Toast.makeText(getActivity().getBaseContext(), getString(R.string.masterpassword_length), Toast.LENGTH_SHORT);
                    toast.show();
                } else {

                    mSpinner.setVisibility(View.VISIBLE);

                    generatePassword();
                }
            }
        });

        ImageButton copyButton = (ImageButton) mRootView.findViewById(R.id.copyButton);

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView password = (TextView) mRootView.findViewById(R.id.textViewPassword);

                if (password.getText().toString().length() > 0) {
                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", password.getText());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getActivity(), getActivity().getString(R.string.password_copied), Toast.LENGTH_SHORT).show();
                }

            }
        });

        mVisibilityButton = (ImageButton) mRootView.findViewById(R.id.visibilityButton);

        mVisibilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mEditTextMasterpassword = (EditText) mRootView.findViewById(R.id.editTextMasterpassword);

                if (!visibility) {
                    mVisibilityButton.setImageResource(R.drawable.ic_visibility_off);
                    mEditTextMasterpassword.setInputType(
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mEditTextMasterpassword.setSelection(mEditTextMasterpassword.getText().length());
                    visibility = true;
                } else {
                    mVisibilityButton.setImageResource(R.drawable.ic_visibility);
                    mEditTextMasterpassword.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mEditTextMasterpassword.setSelection(mEditTextMasterpassword.getText().length());
                    visibility = false;
                }
            }
        });
        return builder;
    }

    public void generatePassword() {

        EditText editTextMasterpassword = (EditText) mRootView.findViewById(R.id.editTextMasterpassword);
        MetaDataViewModel mMetaDataViewModel = ViewModelProviders.of(this).get(MetaDataViewModel.class);
        mMetaDataEntity = mMetaDataViewModel.getMetaDataById(mPosition);

        String deviceID;
        if (bindToDevice_enabled) {
            deviceID = Settings.Secure.getString(getContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            Log.d("DEVICE ID", Settings.Secure.getString(getContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID));
        } else {
            deviceID = "SECUSO";
        }

        //pack parameters to String-Array
        String[] params = new String[12];
        params[0] = mMetaDataEntity.getDomain();
        params[1] = mMetaDataEntity.getUserName();
        params[2] = editTextMasterpassword.getText().toString();
        params[3] = deviceID;
        params[4] = String.valueOf(mMetaDataEntity.getPwVersion());
        params[5] = String.valueOf(number_iterations);
        params[6] = hashAlgorithm;
        params[7] = String.valueOf(mMetaDataEntity.getHasSymbols());
        params[8] = String.valueOf(mMetaDataEntity.getHasLetterLow());
        params[9] = String.valueOf(mMetaDataEntity.getHasLettersUp());
        params[10] = String.valueOf(mMetaDataEntity.getHasNumber());
        params[11] = String.valueOf(mMetaDataEntity.getLength());
        new PasswordGeneratorTask() {
            @Override
            protected void onPostExecute(String result) {
                TextView textViewPassword = (TextView) mRootView.findViewById(R.id.textViewPassword);
                textViewPassword.setText(result);

                passwordToClipboard(clipboard_enabled, result);

                mSpinner.setVisibility(View.GONE);
            }
        }.execute(params);
    }

    public void passwordToClipboard(boolean clipboardEnabled, String password) {
        if (clipboardEnabled) {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Password", password);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getActivity(), getActivity().getString(R.string.password_copied), Toast.LENGTH_SHORT).show();
        }
    }

}
