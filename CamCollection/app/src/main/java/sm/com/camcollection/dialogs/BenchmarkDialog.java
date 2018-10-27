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
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import sm.com.camcollection.R;
import sm.com.camcollection.generator.PasswordGenerator;
import sm.com.camcollection.generator.PasswordGeneratorTask;

public class BenchmarkDialog extends DialogFragment {

    private View mRootView;

    private int mIterations;
    private String mHashAlgorithm;

    private ProgressBar mSpinner;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mRootView = inflater.inflate(R.layout.dialog_benchmark, null);

        mSpinner = (ProgressBar) mRootView.findViewById(R.id.progressBar);
        mSpinner.setVisibility(View.GONE);

        Bundle bundle = getArguments();

        mHashAlgorithm = bundle.getString("hash_algorithm");
        mIterations = bundle.getInt("number_iterations");

        builder.setView(mRootView);
        builder.setIcon(R.mipmap.ic_drawer);
        builder.setTitle(getActivity().getString(R.string.dialog_benchmark_title));
        builder.setPositiveButton(getActivity().getString(R.string.done), null);

        final Button benchmarkButton = (Button) mRootView.findViewById(R.id.benchmarkButton);
        benchmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView executionTextView = (TextView) mRootView.findViewById(R.id.benchmark_execution);
                executionTextView.setText(getString(R.string.dialog_benchmark_time));

                TextView benchmarkTextView = (TextView) mRootView.findViewById(R.id.benchmarkTextView);
                benchmarkTextView.setText("");

                mSpinner.setVisibility(View.VISIBLE);

                generatePassword(mIterations, mHashAlgorithm);
            }
        });

        return builder.create();
    }

    public void generatePassword(int iterations, String hashFunction) {

        double startTime = System.currentTimeMillis();

        //pack parameters to String-Array
        String[] params = new String[13];
        params[0] = "abc.com";
        params[1] = "user";
        params[2] = "masterpassword";
        params[3] = "deviceID";
        params[4] = "4242";
        params[5] = String.valueOf(iterations);
        params[6] = hashFunction;
        params[7] = String.valueOf(1);
        params[8] = String.valueOf(1);
        params[9] = String.valueOf(1);
        params[10] = String.valueOf(1);
        params[11] = String.valueOf(20);
        params[12] = String.valueOf(startTime);

        new PasswordGeneratorTask() {

            @Override
            protected String doInBackground(String[] strings) {

                PasswordGenerator generator = new PasswordGenerator(strings[0],
                        strings[1],
                        strings[2],
                        strings[3],
                        Integer.valueOf(strings[4]),
                        Integer.parseInt(strings[5]),
                        strings[6]);

                generator.getPassword(Integer.parseInt(strings[7]), Integer.parseInt(strings[8]), Integer.parseInt(strings[9]), Integer.parseInt(strings[10]), Integer.parseInt(strings[11]));
                return strings[12];
            }

            @Override
            protected void onPostExecute(String result) {
                TextView benchmarkTextView = (TextView) mRootView.findViewById(R.id.benchmarkTextView);
                double stopTime = System.currentTimeMillis();
                mSpinner.setVisibility(View.GONE);
                benchmarkTextView.setText(String.valueOf((stopTime - Double.parseDouble(result)) / 1000) + " " + getString(R.string.dialog_benchmark_seconds));
            }
        }.execute(params);
    }
}



