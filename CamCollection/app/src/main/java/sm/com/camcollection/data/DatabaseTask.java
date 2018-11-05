package sm.com.camcollection.data;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.List;

public class DatabaseTask {
    Context mContext;
    static MetaDataDatabase mDatabase = null;

    public interface Callback {
        void onPostResult(List<MetaDataEntity> entities);
        void onPostResult(MetaDataEntity entity);
        void onPostResult();
    }

    private DatabaseTask(Context context) {
            mDatabase = MetaDataDatabase.getDatabase(context);
    }

    public static void init(Context context) {
        if (mDatabase == null) {
            new DatabaseTask(context);
        }
    }

    public static  class DeleteAllTask extends AsyncTask<Object, Object, Object> {
        @Override
        protected Object doInBackground(Object... objects) {
            deleteAll();
            return null;
        }
    }

    public static  class InsertTask extends AsyncTask<MetaDataEntity, Object, Object> {
        @Override
        protected Object doInBackground(MetaDataEntity... metaDataEntities) {
            insert(metaDataEntities[0]);
            return null;
        }
    }

    public static class GetAllTask extends AsyncTask<Object, Object, List<MetaDataEntity>> {
        Callback mCallback;
        public GetAllTask(@NonNull Callback callback) {
            mCallback = callback;
        }
        @Override
        protected List<MetaDataEntity> doInBackground(Object... objects) {
            return getAll();
        }

        @Override
        protected void onPostExecute(List<MetaDataEntity> result) {
            mCallback.onPostResult(result);
        }
    }

    public static class GetMetaDataById extends AsyncTask<Integer, Object, MetaDataEntity> {
        Callback mCallback;
        public GetMetaDataById(@NonNull Callback callback) {
            mCallback = callback;
        }
        @Override
        protected MetaDataEntity doInBackground(Integer... integers) {
            return getMetaDataEntity(integers[0].intValue());
        }

        @Override
        protected void onPostExecute(MetaDataEntity result) {
            mCallback.onPostResult(result);
        }
    }

    public static  class updateMetaDataTask extends AsyncTask<MetaDataEntity, Object, Object> {
        @Override
        protected Object doInBackground(MetaDataEntity... metaDataEntities) {
            updateMetadata(metaDataEntities[0]);
            return null;
        }
    }

    public static  class deleteByIdTask extends AsyncTask<Integer, Object, Object> {
        Callback mCallback;
        public deleteByIdTask(Callback callback) {
            mCallback = callback;
        }

        @Override
        protected Object doInBackground(Integer... integers) {
            deletebyId(integers[0].intValue());
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            mCallback.onPostResult();
        }
    }

    public static  class deleteByMetaDataTask extends AsyncTask<MetaDataEntity, Object, Object> {
        @Override
        protected Object doInBackground(MetaDataEntity... metaDataEntities) {
            deleteMetaDataEntity(metaDataEntities[0]);
            return null;
        }
    }

    private static  void insert(@NonNull MetaDataEntity entity) {
        if (mDatabase != null) {
            mDatabase.MetaDataDao().insert(entity);
        }
    }

    private static  void deleteAll() {
        if (mDatabase != null) {
            mDatabase.MetaDataDao().deleteAll();
        }
    }

    private static List<MetaDataEntity> getAll() {
        List<MetaDataEntity> ret = null;
        if (mDatabase != null) {
            ret = mDatabase.MetaDataDao().getAll();
        }
        return ret;
    }

    private static  MetaDataEntity getMetaDataEntity(int metaDataId) {
        MetaDataEntity entity = null;
        if (mDatabase != null) {
            entity = mDatabase.MetaDataDao().getMetaDatabyId(metaDataId);
        }
        return entity;
    }

    private static  void updateMetadata(MetaDataEntity entity) {
        if (mDatabase != null) {
            mDatabase.MetaDataDao().updateMetaData(entity);
        }
    }

    private static  void deletebyId(int metaId) {
        if (mDatabase != null) {
            mDatabase.MetaDataDao().deleteMetaData(metaId);
        }
    }

    private static  void deleteMetaDataEntity(MetaDataEntity metaDataEntity) {
        if (mDatabase != null) {
            mDatabase.MetaDataDao().deleteMetaData(metaDataEntity);
        }
    }
}
