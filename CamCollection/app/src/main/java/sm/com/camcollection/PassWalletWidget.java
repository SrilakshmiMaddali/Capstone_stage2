package sm.com.camcollection;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import java.sql.Time;
import java.util.List;

import sm.com.camcollection.adapter.ListViewDataAdapter;
import sm.com.camcollection.data.DatabaseTask;
import sm.com.camcollection.data.MetaDataEntity;

/**
 * Implementation of App Widget functionality.
 */
public class PassWalletWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, List<MetaDataEntity> mList) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views1 = new RemoteViews(context.getPackageName(), R.layout.pass_wallet);
        views1.setTextViewText(R.id.appwidget_text_one, widgetText);
        if (mList != null && !mList.isEmpty()) {
            views1.setTextViewText(R.id.appwidget_text_two, mList.get(mList.size()-1).getDomain());
            views1.setOnClickPendingIntent(R.id.appwidget_text_two, getPendingIntent(context, 1));
        }

        // Instruct the widget manager to update the widgetƒ
        appWidgetManager.updateAppWidget(appWidgetId, views1);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        Intent intent = new Intent(context, UpdateWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        context.startService(intent);
        Intent intent2 = new Intent(context,
                FrequentWidgetUpdateService.class);
        intent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        context.startService(intent2);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static PendingIntent getPendingIntent(Context context, int value) {
        //1
        Intent intent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context, value, intent, 0);
    }
}

