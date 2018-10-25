package sm.com.passwordwallet;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.List;

import sm.com.passwordwallet.adapter.ListViewDataAdapter;
import sm.com.passwordwallet.data.DatabaseTask;
import sm.com.passwordwallet.data.MetaDataDatabase;
import sm.com.passwordwallet.data.MetaDataEntity;
import sm.com.passwordwallet.dialogs.AddMetaDataDialog;
import sm.com.passwordwallet.dialogs.GeneratePasswordDialog;
import sm.com.passwordwallet.dialogs.UpdateMetadataDialog;
import sm.com.passwordwallet.util.PrefManager;
import sm.com.passwordwallet.util.RecyclerItemClickListener;
import sm.com.passwordwallet.util.SwipeableRecyclerViewTouchListener;

public class MainActivity extends BaseActivity {

    private ListViewDataAdapter mAdapter;
    private List<MetaDataEntity> mList;
    private RecyclerView mRecyclerView;
    private List<MetaDataEntity> recycerList;

    private MetaDataDatabase mDatabase;
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    private boolean clipboard_enabled;
    private boolean bindToDevice_enabled;
    private String hash_algorithm;
    private int number_iterations;
    private final static int INIT = 0;
    private final static int REFRESH = 1;

    private static final String APP_TAG = "PASSWALLET";
    // remote config keys
    private static final String COPY_TO_CLIPBOARD = "copy_to_clipboard";
    private static final String NUMBEROF_HASH_ITERATIONS = "number_of_hash_iterations";

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

         @Override
         public void handleMessage(Message msg) {
            switch (msg.what) {
                case INIT:
                    init();
                    break;
                case REFRESH:
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private List<MetaDataEntity> mFilteredMetaDataList;

    private LinearLayout initialAlert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        // TODO: Replace sample app ID with your AdMob app ID
        MobileAds.initialize(this, "ca-app-pub-6595532125576760~1922912103");
        //Load the add
        AdRequest adRequest = new AdRequest.Builder().build();
        ((AdView)findViewById(R.id.adView)).loadAd(adRequest);

        mDatabase = MetaDataDatabase.getDatabase(this);
        DatabaseTask.init();

        PrefManager prefManager = new PrefManager(this);

        // Get Remote Config instance.
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // Create a Remote Config Setting to enable developer mode, which you can use to increase
        // the number of fetches available per hour during development.
        // [START enable_dev_mode]
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();

        // Set default Remote Config parameter values. An app uses the in-app default values, and
        // when you need to adjust those defaults, you set an updated value for only the values you
        // want to change in the Firebase console.
        // [START set_default_values]
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        // [END enable_dev_mode]


        boolean isFirstTime = prefManager.isFirstTimeLaunch();
        if (isFirstTime) {
           // recycerList = new ArrayList<>(4);
            addSampleData();
            prefManager.setFirstTimeLaunch(false);
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        refreshFromDatabase();
    }

    private void refreshFromDatabase() {
        DatabaseTask.GetAllTask getAllTask = new DatabaseTask.GetAllTask(new DatabaseTask.Callback() {
            @Override
            public void onPostResult(List<MetaDataEntity> entities) {
                mList = entities;
                initialAlert = (LinearLayout) findViewById(R.id.insert_alert);
                if (mList != null) {
                    hints(mList.size());
                }
                mAdapter = new ListViewDataAdapter(mList);
                mRecyclerView.setAdapter(mAdapter);
                loadPreferences();

                int current = 0;
                for (MetaDataEntity data : mList) {
                    data.setPositionId(current);
                    current++;
                }
                init();
            }

            @Override
            public void onPostResult(MetaDataEntity entity) {

            }

            @Override
            public void onPostResult() {}
        });
        getAllTask.execute();
    }

    private void init() {
        //No screenshot
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getBaseContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        Bundle bundle = new Bundle();

                        //Gets ID for look up in DB
                        MetaDataEntity temp = mAdapter.getItem(position);

                        bundle.putInt("position", temp.getId());
                        bundle.putString("hash_algorithm", hash_algorithm);
                        bundle.putBoolean("clipboard_enabled", clipboard_enabled);
                        bundle.putBoolean("bindToDevice_enabled", bindToDevice_enabled);
                        bundle.putInt("number_iterations", number_iterations);

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        GeneratePasswordDialog generatePasswordDialog = new GeneratePasswordDialog();
                        generatePasswordDialog.setArguments(bundle);
                        generatePasswordDialog.show(fragmentManager, "GeneratePasswordDialog");

                        /*PrefManager prefManager = new PrefManager(getBaseContext());
                        if (prefManager.isFirstTimeGen()) {
                            prefManager.setFirstTimeGen(false);
                            Intent intent = new Intent(MainActivity.this, MasterPWTutorialActivity.class);
                            startActivity(intent);
                        }*/
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Log.d("Main Activity", Integer.toString(position));
                        Bundle bundle = new Bundle();

                        //Gets ID for look up in DB
                        MetaDataEntity temp = mList.get(position);

                        bundle.putInt("position", temp.getId());
                        bundle.putString("hash_algorithm", hash_algorithm);
                        bundle.putInt("number_iterations", number_iterations);
                        bundle.putBoolean("bindToDevice_enabled", bindToDevice_enabled);

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        UpdateMetadataDialog updateMetadataDialog = new UpdateMetadataDialog();
                        updateMetadataDialog.setArguments(bundle);
                        updateMetadataDialog.show(fragmentManager, "UpdateMetadataDialog");
                    }
                })
        );
        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(mRecyclerView,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {
                            @Override
                            public boolean canSwipeLeft(int position) {
                                return true;
                            }

                            @Override
                            public boolean canSwipeRight(int position) {
                                return false;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (final int position : reverseSortedPositions) {
                                    //deleteItem(position);
                                    DatabaseTask.deleteByIdTask task = new DatabaseTask.deleteByIdTask(new DatabaseTask.Callback() {
                                        @Override
                                        public void onPostResult(List<MetaDataEntity> entities) {

                                        }

                                        @Override
                                        public void onPostResult(MetaDataEntity entity) {

                                        }

                                        @Override
                                        public void onPostResult() {
                                            mAdapter.removeItem(position);
                                            mHandler.sendEmptyMessage(REFRESH);
                                        }
                                    });
                                    task.execute(position+1);
                                }
                            }
                        });
        mRecyclerView.addOnItemTouchListener(swipeTouchListener);

        FloatingActionButton addFab = (FloatingActionButton) findViewById(R.id.add_fab);
        if (addFab != null) {

            addFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    AddMetaDataDialog addMetaDataDialog = new AddMetaDataDialog();
                    addMetaDataDialog.show(fragmentManager, "AddMetaDataDialog");
                }
            });

        }
        overridePendingTransition(0, 0);
    }

    public void deleteItem(int position) {
        MetaDataEntity toDeleteMetaData = mList.get(position);
        final MetaDataEntity toDeleteMetaDataFinal = toDeleteMetaData;

        //Removes MetaData from DB
        DatabaseTask.deleteByMetaDataTask task = new DatabaseTask.deleteByMetaDataTask();
        task.execute(toDeleteMetaData);

        //Removes MetaData from List in View
        mList.remove(position);

        final int finalPosition = position;

        initialAlert.setVisibility(View.VISIBLE);
        hints(position);

        /*Snackbar.make(findViewById(android.R.id.content), getString(R.string.domain) + " " + toDeleteMetaData.getDomain() + " " + getString(R.string.item_deleted), Snackbar.LENGTH_SHORT)
                .setAction(getString(R.string.undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDatabase.MetaDataDao().insert(toDeleteMetaDataFinal);
                        mList.add(finalPosition, toDeleteMetaDataFinal);
                        mAdapter.notifyItemInserted(finalPosition);
                        mAdapter.notifyDataSetChanged();
                        initialAlert.setVisibility(View.GONE);
                        hints(1);
                    }
                }).show();*/
        mAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPreferences();
        fetchRemoteConfig();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected int getNavigationDrawerID() {
        return 0;
    }

    public void addSampleData() {
        MetaDataEntity meta1 = new MetaDataEntity(1, 1, getString(R.string.sample_domain1), getString(R.string.sample_username1), 15, 1, 1, 1, 1, 1);
        MetaDataEntity meta2 = new MetaDataEntity(2, 2, getString(R.string.sample_domain2), getString(R.string.sample_username2), 20, 1, 1, 1, 1, 1);
        MetaDataEntity meta3 = new MetaDataEntity(3, 3, getString(R.string.sample_domain3), getString(R.string.sample_username3), 4, 1, 0, 0, 0, 1);

        DatabaseTask.InsertTask insert1 = new DatabaseTask.InsertTask();
        insert1.execute(meta1);
       // mList.add(0, meta1);

        DatabaseTask.InsertTask insert2 = new DatabaseTask.InsertTask();
        insert2.execute(meta2);
      //  mList.add(1, meta2);

        DatabaseTask.InsertTask insert3 = new DatabaseTask.InsertTask();
        insert3.execute(meta3);
      //  mList.add(2, meta3);
    }

    public void hints(int position) {

        Animation anim = new AlphaAnimation(0.0f, 1.0f);

        if (mList.size() == 0 || position == 0) {

            initialAlert.setVisibility(View.VISIBLE);
            anim.setDuration(1500);
            anim.setStartOffset(20);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.INFINITE);
            initialAlert.startAnimation(anim);

        } else {
            initialAlert.setVisibility(View.GONE);
            initialAlert.clearAnimation();
        }

    }

    public void loadPreferences() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        clipboard_enabled = sharedPreferences.getBoolean("clipboard_enabled", false);
        bindToDevice_enabled = sharedPreferences.getBoolean("bindToDevice_enabled", false);
        hash_algorithm = sharedPreferences.getString("hash_algorithm", "SHA256");
        String tempIterations = sharedPreferences.getString("hash_iterations", "1000");
        number_iterations = Integer.parseInt(tempIterations);
    }

    private void fetchRemoteConfig() {
        boolean copyToClipboard = mFirebaseRemoteConfig.getBoolean(COPY_TO_CLIPBOARD);
        String numberHashIterations = mFirebaseRemoteConfig.getString(NUMBEROF_HASH_ITERATIONS);

        Log.d(APP_TAG, MainActivity.class.getSimpleName() +" "+copyToClipboard);
        Log.d(APP_TAG, MainActivity.class.getSimpleName() +" "+numberHashIterations);

        long cacheExpiration = 0; // 1 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        // [START fetch_config_with_callback]
        // cacheExpirationSeconds is set to cacheExpiration here, indicating the next fetch request
        // will use fetch data from the Remote Config service, rather than cached parameter values,
        // if cached parameter values are more than cacheExpiration seconds old.
        // See Best Practices in the README for more information.
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Fetch Succeeded",
                                    Toast.LENGTH_SHORT).show();

                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            Toast.makeText(MainActivity.this, "Fetch Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END fetch_config_with_callback]
    }
}