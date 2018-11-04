package sm.com.camcollection;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.List;

import sm.com.camcollection.data.DatabaseTask;
import sm.com.camcollection.data.MetaDataEntity;

public class UpdateWidgetService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            refreshFromDatabase(this, intent);

        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void refreshFromDatabase(final Context context, final Intent intent) {
        DatabaseTask.GetAllTask getAllTask = new DatabaseTask.GetAllTask(new DatabaseTask.Callback() {
            @Override
            public void onPostResult(List<MetaDataEntity> entities) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                int[] widgtIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                if (widgtIds != null) {
                    for (int widgtId: widgtIds ) {
                        PassWalletWidget.updateAppWidget(context, appWidgetManager, widgtId, entities);

                    }
                }
            }

            @Override
            public void onPostResult(MetaDataEntity entity) {}

            @Override
            public void onPostResult() {}
        });
        getAllTask.execute();
    }
}
