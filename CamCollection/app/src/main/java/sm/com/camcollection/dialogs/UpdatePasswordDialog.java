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
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
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
import sm.com.camcollection.generator.PasswordGeneratorTask;

import static android.content.Context.CLIPBOARD_SERVICE;

public class UpdatePasswordDialog extends DialogFragment {

    private View mRootView;

    private MetaDataDatabase mDatabase;
    private int position;
    private MetaDataEntity mMetaDataEntity;
    private MetaDataEntity mOldMetaDataEntity;

    private boolean bindToDevice_enabled;
    private String hashAlgorithm;
    private int number_iterations;

    private ProgressBar spinnerOld;
    private ProgressBar spinnerNew;

    private ImageButton visibilityButton;
    private boolean visibility;

    private EditText editTextUpdateMasterpassword;

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        mRootView = inflater.inflate(R.layout.dialog_update_passwords, null);

        visibility = false;

        spinnerOld = (ProgressBar) mRootView.findViewById(R.id.oldProgressBar);
        spinnerOld.setVisibility(View.GONE);

        spinnerNew = (ProgressBar) mRootView.findViewById(R.id.newProgressBar);
        spinnerNew.setVisibility(View.GONE);

        Bundle bundle = getArguments();

        position = bundle.getInt("position");
        bindToDevice_enabled = bundle.getBoolean("bindToDevice_enabled");
        hashAlgorithm = bundle.getString("hash_algorithm");
        setOldMetaData(bundle);
        number_iterations = bundle.getInt("number_iterations");

        mDatabase = MetaDataDatabase.getDatabase(getActivity());

        builder.setView(mRootView);

        builder.setIcon(R.mipmap.ic_drawer);

        builder.setTitle(getActivity().getString(R.string.passwords_heading));

        Button displayButton = (Button) mRootView.findViewById(R.id.displayButton);
        displayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);


                displayPasswords();
            }
        });

        ImageButton copyOldButton = (ImageButton) mRootView.findViewById(R.id.copyOldButton);
        ImageButton copyNewButton = (ImageButton) mRootView.findViewById(R.id.copyNewButton);

        copyOldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView oldPassword = (TextView) mRootView.findViewById(R.id.textViewOldPassword);
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", oldPassword.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(), getActivity().getString(R.string.copy_clipboar_old), Toast.LENGTH_SHORT).show();
            }
        });

        copyNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView newPassword = (TextView) mRootView.findViewById(R.id.textViewNewPassword);
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", newPassword.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(), getActivity().getString(R.string.copy_clipboar_new), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton(getActivity().getString(R.string.done), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickDone();
            }
        });

        visibilityButton = (ImageButton) mRootView.findViewById(R.id.visibilityButton);

        visibilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editTextUpdateMasterpassword = (EditText) mRootView.findViewById(R.id.editTextUpdateMasterpassword);

                if (!visibility) {
                    visibilityButton.setImageResource(R.drawable.ic_visibility_off);
                    editTextUpdateMasterpassword.setInputType(
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    editTextUpdateMasterpassword.setSelection(editTextUpdateMasterpassword.getText().length());
                    visibility = true;
                } else {
                    visibilityButton.setImageResource(R.drawable.ic_visibility);
                    editTextUpdateMasterpassword.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    editTextUpdateMasterpassword.setSelection(editTextUpdateMasterpassword.getText().length());
                    visibility = false;
                }
            }
        });

        return builder.create();
    }

    public void onClickDone() {
        getActivity().recreate();
        this.dismiss();
    }

    public void displayPasswords() {
        editTextUpdateMasterpassword = (EditText) mRootView.findViewById(R.id.editTextUpdateMasterpassword);
        TextView textViewOld = (TextView) mRootView.findViewById(R.id.textViewOldPassword);
        TextView textViewNew = (TextView) mRootView.findViewById(R.id.textViewNewPassword);

        textViewOld.setText("");
        textViewNew.setText("");

        if (editTextUpdateMasterpassword.getText().toString().length() == 0) {
            Toast toast = Toast.makeText(getActivity().getBaseContext(), getString(R.string.enter_masterpassword), Toast.LENGTH_SHORT);
            toast.show();
        } else if (editTextUpdateMasterpassword.getText().toString().length() < 8) {
            Toast toast = Toast.makeText(getActivity().getBaseContext(), getString(R.string.masterpassword_length), Toast.LENGTH_SHORT);
            toast.show();
        } else {

            spinnerOld.setVisibility(View.VISIBLE);
            spinnerNew.setVisibility(View.VISIBLE);

            final TextView textViewOldPassword = (TextView) mRootView.findViewById(R.id.textViewOldPassword);
            final TextView textViewNewPassword = (TextView) mRootView.findViewById(R.id.textViewNewPassword);

            final String deviceID;

            if (bindToDevice_enabled) {
                deviceID = Settings.Secure.getString(getContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                Log.d("DEVICE ID", Settings.Secure.getString(getContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID));
            } else {
                deviceID = "SECUSO";
            }

            //pack old parameters to String-Array
            String[] paramsOld = new String[12];
            paramsOld[0] = mOldMetaDataEntity.getDomain();
            paramsOld[1] = mOldMetaDataEntity.getUserName();
            paramsOld[2] = editTextUpdateMasterpassword.getText().toString();
            paramsOld[3] = deviceID;
            paramsOld[4] = String.valueOf(mOldMetaDataEntity.getPwVersion());
            paramsOld[5] = String.valueOf(number_iterations);
            paramsOld[6] = hashAlgorithm;
            paramsOld[7] = String.valueOf(mOldMetaDataEntity.getHasSymbols());
            paramsOld[8] = String.valueOf(mOldMetaDataEntity.getHasLetterLow());
            paramsOld[9] = String.valueOf(mOldMetaDataEntity.getHasLettersUp());
            paramsOld[10] = String.valueOf(mOldMetaDataEntity.getHasNumber());
            paramsOld[11] = String.valueOf(mOldMetaDataEntity.getLength());

            new PasswordGeneratorTask() {
                @Override
                protected void onPostExecute(String result) {
                    textViewOldPassword.setText(result);
                    spinnerOld.setVisibility(View.GONE);
                }
            }.execute(paramsOld);

            //generate new password
            DatabaseTask.GetMetaDataById task = new DatabaseTask.GetMetaDataById(new DatabaseTask.Callback() {
                @Override
                public void onPostResult(List<MetaDataEntity> entities) {

                }

                @Override
                public void onPostResult(MetaDataEntity entity) {
                    mMetaDataEntity = entity;
                    //pack new parameters to String-Array
                    String[] paramsNew = new String[12];
                    paramsNew[0] = mMetaDataEntity.getDomain();
                    paramsNew[1] = mMetaDataEntity.getUserName();
                    paramsNew[2] = editTextUpdateMasterpassword.getText().toString();
                    paramsNew[3] = deviceID;
                    paramsNew[4] = String.valueOf(mMetaDataEntity.getPwVersion());
                    paramsNew[5] = String.valueOf(number_iterations);
                    paramsNew[6] = hashAlgorithm;
                    paramsNew[7] = String.valueOf(mMetaDataEntity.getHasSymbols());
                    paramsNew[8] = String.valueOf(mMetaDataEntity.getHasLetterLow());
                    paramsNew[9] = String.valueOf(mMetaDataEntity.getHasLettersUp());
                    paramsNew[10] = String.valueOf(mMetaDataEntity.getHasNumber());
                    paramsNew[11] = String.valueOf(mMetaDataEntity.getLength());

                    new PasswordGeneratorTask() {
                        @Override
                        protected void onPostExecute(String result) {
                            textViewNewPassword.setText(result);
                            spinnerNew.setVisibility(View.GONE);
                        }
                    }.execute(paramsNew);
                }

                @Override
                public void onPostResult() {}
            });
            task.execute(position);
        }
    }

    public void setOldMetaData(Bundle bundle) {
        mOldMetaDataEntity = new MetaDataEntity(0, 0,
                bundle.getString("olddomain"),
                bundle.getString("oldusername"),
                bundle.getInt("oldlength"),
                bundle.getInt("oldnumbers"),
                bundle.getInt("oldsymbols"),
                bundle.getInt("oldlettersup"),
                bundle.getInt("oldletterslow"),
                bundle.getInt("olditeration")
        );

    }

}
