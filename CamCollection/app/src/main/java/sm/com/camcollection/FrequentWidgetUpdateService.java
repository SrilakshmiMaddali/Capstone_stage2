package sm.com.camcollection;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.widget.RemoteViews;

import java.sql.Time;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sm.com.camcollection.data.MetaDataDatabase;
import sm.com.camcollection.data.MetaDataEntity;

public class FrequentWidgetUpdateService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */

    public FrequentWidgetUpdateService() {
        super("FrequentWidgetUpdateService");
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        MetaDataDatabase db = MetaDataDatabase.getDatabase(this);
        List<MetaDataEntity> entities = db.MetaDataDao().getAllRecords();
        if (entities != null) {
            Collections.sort(entities, new Comparator<MetaDataEntity>() {
                @Override
                public int compare(MetaDataEntity o1, MetaDataEntity o2) {
                    int fre1 = o1.getFrequency();
                    int fre2 = o2.getFrequency();
                    return fre1 - fre2;
                }
            });
            update(entities, intent);
        }
    }

    private void update(List<MetaDataEntity> list, Intent intent) {
        AppWidgetManager appWidgetManager =
                AppWidgetManager.getInstance(this);
        CharSequence widgetText = this.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views1 = new RemoteViews(this.getPackageName(), R.layout.pass_wallet);
        views1.setTextViewText(R.id.appwidget_text_one, widgetText);
        if (list != null && !list.isEmpty()) {
            views1.setTextViewText(R.id.appwidget_text_two, list.get(list.size()-1).getDomain());
        }
        int[] widgtIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        scheduleNextUpdate(this, widgtIds);
        if (widgtIds != null) {
            for (int widgtId: widgtIds ) {
                PassWalletWidget.updateAppWidget(this, appWidgetManager, widgtId, list);
            }
        }
    }
    public static void scheduleNextUpdate(Context context, int[] widgtId) {
        Intent changePasscodeIntent =
                new Intent(context, FrequentWidgetUpdateService.class);
        changePasscodeIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgtId);
        PendingIntent changePasscodePendingIntent =
                PendingIntent.getService(context, 0, changePasscodeIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        // The update frequency should be user configurable.
        Time time = new Time(System.currentTimeMillis() + 1 *
                DateUtils.MINUTE_IN_MILLIS);
        long nextUpdate = time.getTime();

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, nextUpdate,
                changePasscodePendingIntent);
    }
}
