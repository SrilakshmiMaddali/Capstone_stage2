package sm.com.passwordwallet;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.widget.Toast;

import sm.com.passwordwallet.data.MetaDataDao;
import sm.com.passwordwallet.data.MetaDataDatabase;
import sm.com.passwordwallet.dialogs.BenchmarkDialog;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_settings;
    }


    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {

        Activity activity;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            this.activity = activity;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            bindPreferenceSummaryToValue(findPreference("hash_iterations"));
            bindPreferenceSummaryToValue(findPreference("hash_algorithm"));

            Preference benchmark = findPreference("benchmark");
            benchmark.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    Bundle bundle = new Bundle();

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

                    bundle.putInt("number_iterations", Integer.parseInt(preferences.getString("hash_iterations", "1000")));
                    bundle.putString("hash_algorithm", preferences.getString("hash_algorithm", "SHA256"));

                    FragmentManager fragmentManager = getActivity().getFragmentManager();
                    BenchmarkDialog benchmarkDialog = new BenchmarkDialog();
                    benchmarkDialog.setArguments(bundle);
                    benchmarkDialog.show(fragmentManager, "BenchmarkDialog");

                    return true;
                }
            });

            Preference reset = findPreference("pref_reset_list");
            reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    new AlertDialog.Builder(getActivity())
                            .setMessage(getString(R.string.delete_dialog))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    MetaDataDao dao = MetaDataDatabase.getDatabase(getActivity()).MetaDataDao();
                                    dao.deleteAll();
                                    Toast.makeText(getActivity(), getString(R.string.delete_dialog_success), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton(R.string.cancel, null).show();


                    return true;
                }
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                //getActivity().finish();
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

    }
}