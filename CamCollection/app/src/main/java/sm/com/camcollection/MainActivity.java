package sm.com.camcollection;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.RoomDatabase;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
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
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.List;

import sm.com.camcollection.adapter.ListViewDataAdapter;
import sm.com.camcollection.data.DatabaseTask;
import sm.com.camcollection.data.MetaDataDao;
import sm.com.camcollection.data.MetaDataDatabase;
import sm.com.camcollection.data.MetaDataEntity;
import sm.com.camcollection.data.MetaDataViewModel;
import sm.com.camcollection.dialogs.AddMetaDataDialog;
import sm.com.camcollection.dialogs.GeneratePasswordDialog;
import sm.com.camcollection.dialogs.UpdateMetadataDialog;
import sm.com.camcollection.util.PrefManager;
import sm.com.camcollection.util.RecyclerItemClickListener;
import sm.com.camcollection.util.SwipeableRecyclerViewTouchListener;

import static sm.com.camcollection.data.MetaDataDatabase.INSTANCE;

public class MainActivity extends BaseActivity implements DialogListener {

    private ListViewDataAdapter mAdapter;
    private List<MetaDataEntity> mList;
    private RecyclerView mRecyclerView;
    MetaDataTaskReceiver mMetaDataTaskReceiver;
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    private boolean clipboard_enabled;
    private Parcelable mLayoutManagerState;
    private boolean remove_ads;
    private boolean bindToDevice_enabled;
    private String hash_algorithm;
    private int number_iterations;
    private final static int INIT = 0;
    private final static int REFRESH = 1;
    public final static String MAX_POSITION_ID = "max_id";

    private static final String APP_TAG = "PASSWALLET";
    // remote config keys
    private static final String COPY_TO_CLIPBOARD = "copy_to_clipboard";
    private static final String BUNDLE_RECYCLER_LAYOUT = "bundle_recycler_layout";
    private static final String RECYCLER_POSITION = "recycler_position";
    private static final String NUMBEROF_HASH_ITERATIONS = "number_of_hash_iterations";
    private static final String REMOVE_ADS = "remove_ads";

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

    private LinearLayout initialAlert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        mMetaDataTaskReceiver = new MetaDataTaskReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMetaDataTaskReceiver, new IntentFilter(DELETE_ALL_TASK_ACTION));
        mMetaDataViewModel = ViewModelProviders.of(this).get(MetaDataViewModel.class);


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

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new ListViewDataAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mMetaDataViewModel.getAllRecords().observe(this, new Observer<List<MetaDataEntity>>() {
            @Override
            public void onChanged(@Nullable List<MetaDataEntity> entities) {
                mAdapter.setMetaDataList(entities);
                mAdapter.notifyDataSetChanged();
                loadPreferences();
            }
        });
        init();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, mLayoutManagerState);
        outState.putInt(RECYCLER_POSITION, mRecyclerView.getChildAdapterPosition(mRecyclerView.getFocusedChild()));
    }

    private void refreshFromDatabase(final Bundle savedInstanceState) {

        DatabaseTask.GetAllTask getAllTask = new DatabaseTask.GetAllTask(new DatabaseTask.Callback() {
            @Override
            public void onPostResult(List<MetaDataEntity> entities) {
                    mList = entities;
                    initialAlert = (LinearLayout) findViewById(R.id.insert_alert);
                    if (mList != null) {
                        hints(mList.size());
                    }
                    int current = 0;
                    for (MetaDataEntity data : mList) {
                        data.setPositionId(current);
                        mMetaDataViewModel.UpdateByIdAndDomain(data.getDomain(), current);
                        current++;
                    }
                    mAdapter = new ListViewDataAdapter(mList);
                    mRecyclerView.setAdapter(mAdapter);
                    loadPreferences();
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


        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getBaseContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        final Bundle bundle = new Bundle();

                        //Gets ID for look up in DB
                        MetaDataEntity temp = mAdapter.getItem(position);
                        if(temp == null){
                            return;
                        }

                        bundle.putInt(getString(R.string.str_position), temp.getPositionId());
                        bundle.putString(getString(R.string.str_hash_algorithm), hash_algorithm);
                        bundle.putBoolean(getString(R.string.str_clipboard_enabled), clipboard_enabled);
                        bundle.putBoolean(getString(R.string.str_bindToDevice_enabled), bindToDevice_enabled);
                        bundle.putInt(getString(R.string.str_number_iterations), number_iterations);
                        bundle.putParcelable(getString(R.string.str_entity),temp);


                        MetaDataEntity entity = mMetaDataViewModel.getMetaDataById(temp.getPositionId());
                        if (entity != null) {
                            int frq = entity.getFrequency();
                            mMetaDataViewModel.UpdateByFrequencyAndDomain(frq+1, entity.getDomain());
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            GeneratePasswordDialog generatePasswordDialog = new GeneratePasswordDialog();
                            generatePasswordDialog.setArguments(bundle);
                            generatePasswordDialog.show(fragmentManager, getString(R.string.str_generatepassword_dialog_tag));
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Log.d("Main Activity", Integer.toString(position));
                        final Bundle bundle = new Bundle();

                        //Gets ID for look up in DB
                        MetaDataEntity temp = mAdapter.getItem(position);

                        bundle.putInt(getString(R.string.str_position), position);
                        bundle.putInt(getString(R.string.str_id), temp.getId());
                        bundle.putString(getString(R.string.str_hash_algorithm), hash_algorithm);
                        bundle.putInt(getString(R.string.str_number_iterations), number_iterations);
                        bundle.putBoolean(getString(R.string.str_bindToDevice_enabled), bindToDevice_enabled);
                        MetaDataEntity entity = mMetaDataViewModel.getMetaDataById(position);
                        bundle.putParcelable(getString(R.string.str_entity), entity);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        UpdateMetadataDialog updateMetadataDialog = new UpdateMetadataDialog();
                        updateMetadataDialog.setArguments(bundle);
                        updateMetadataDialog.show(fragmentManager, getString(R.string.str_updatedialog_tag));
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
                                    mMetaDataViewModel.deleteById(position);
                                    mAdapter.removeItem(position);
                                    mHandler.sendEmptyMessage(REFRESH);
                                }
                            }
                        });
        mRecyclerView.addOnItemTouchListener(swipeTouchListener);

        FloatingActionButton addFab = (FloatingActionButton) findViewById(R.id.add_fab);
        if (addFab != null) {

            addFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    int itemCount = mAdapter.getItemCount();
                    bundle.putInt(MAX_POSITION_ID, itemCount);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    AddMetaDataDialog addMetaDataDialog = new AddMetaDataDialog();
                    addMetaDataDialog.setArguments(bundle);
                    addMetaDataDialog.show(fragmentManager, getString(R.string.str_addMetadata_tag));
                }
            });

        }
        overridePendingTransition(0, 0);
    }

    public void deleteItem(int position) {
        MetaDataEntity toDeleteMetaData = mList.get(position);
        final MetaDataEntity toDeleteMetaDataFinal = toDeleteMetaData;

        //Removes MetaData from DB
        mMetaDataViewModel.deleteByMetaData(toDeleteMetaData);

        //Removes MetaData from List in View
        mList.remove(position);

        final int finalPosition = position;

        initialAlert.setVisibility(View.VISIBLE);
        hints(position);
        mAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPreferences();
        fetchRemoteConfig();
        loadAds();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected int getNavigationDrawerID() {
        return 0;
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
        clipboard_enabled = sharedPreferences.getBoolean(getString(R.string.str_clipboard_enabled), false);
        bindToDevice_enabled = sharedPreferences.getBoolean(getString(R.string.str_bindToDevice_enabled), false);
        hash_algorithm = sharedPreferences.getString(getString(R.string.str_hash_algorithm), "SHA256");
        String tempIterations = sharedPreferences.getString(getString(R.string.str_hash_iterations), "1000");
        number_iterations = Integer.parseInt(tempIterations);
    }

    private void fetchRemoteConfig() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean copyToClipboard = mFirebaseRemoteConfig.getBoolean(COPY_TO_CLIPBOARD);
        sharedPreferences.edit().putBoolean(getString(R.string.str_clipboard_enabled), copyToClipboard).commit();
        String numberHashIterations = mFirebaseRemoteConfig.getString(NUMBEROF_HASH_ITERATIONS);
        // remove ads, is to control ads remotely.
        remove_ads = mFirebaseRemoteConfig.getBoolean(REMOVE_ADS);


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
                            Toast.makeText(MainActivity.this, getString(R.string.str_fetch_succeeded),
                                    Toast.LENGTH_SHORT).show();

                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            Toast.makeText(MainActivity.this, getString(R.string.str_fetch_failed),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END fetch_config_with_callback]
    }

    private void loadAds() {

        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        //Load the add
        // commenting as we need to display only test ads.
        /*if (!remove_ads) {
            AdRequest adRequest = new AdRequest.Builder().build();
            ((AdView) findViewById(R.id.adView)).loadAd(adRequest);
        } else {
            ((AdView) findViewById(R.id.adView)).setVisibility(View.GONE);
        }*/
    }

    public static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
    };

    @Override
    public void onfinisheditdialog(int resultCode, Bundle data) {
        MetaDataEntity entity;
        switch (resultCode) {
            case ADD_RECORD_SUCCESS:
                entity = (MetaDataEntity) data.getParcelable(ENTITY_KEY);
                mMetaDataViewModel.insert(entity);
                break;
            case UPDATE_METADATA:
                entity = (MetaDataEntity) data.getParcelable(ENTITY_KEY);
                mMetaDataViewModel.updateMetaData(entity);
                break;
        }
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
        private final MetaDataDao dao;
        PopulateDbAsync(MetaDataDatabase db) {
            dao = db.MetaDataDao();
        }
        @Override
        protected Void doInBackground(final Void... params) {
            MetaDataEntity meta1 = new MetaDataEntity(1, 0, "example.email.com", "example.name@email.com", 15, 1, 1, 1, 1, 1);
            dao.insert(meta1);
            return null;
        }
    }

    public class  MetaDataTaskReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction() == DELETE_ALL_TASK_ACTION) {
                mMetaDataViewModel.deleteAll();
            }
        }
    }
 }
