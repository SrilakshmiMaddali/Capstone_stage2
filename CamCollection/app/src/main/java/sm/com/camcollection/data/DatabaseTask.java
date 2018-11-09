package sm.com.camcollection.data;

import android.app.ProgressDialog;
import android.arch.lifecycle.LiveData;
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
        MetaDataDao mDao;
        public DeleteAllTask(MetaDataDao dao) {
            mDao = dao;
        }
        @Override
        protected Object doInBackground(Object... objects) {
            mDao.deleteAll();
            return null;
        }
    }

    public static  class InsertTask extends AsyncTask<MetaDataEntity, Object, Object> {
        MetaDataDao mDao;
        public InsertTask( MetaDataDao dao) {
            mDao = dao;
        }

        @Override
        protected Object doInBackground(MetaDataEntity... metaDataEntities) {
            mDao.insert(metaDataEntities[0]);
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
            return null;
        }

        @Override
        protected void onPostExecute(List<MetaDataEntity> result) {
            mCallback.onPostResult(result);
        }
    }

    public static class GetMetaDataById extends AsyncTask<Integer, Object, MetaDataEntity> {
        @Override
        protected MetaDataEntity doInBackground(Integer... integers) {
            return getMetaDataEntity(integers[0].intValue());
        }

        @Override
        protected void onPostExecute(MetaDataEntity result) {
            return;
        }
    }

    public static class UpdateByIdAndDomain extends AsyncTask<Integer, Object, Object> {
        String domainName;
        MetaDataDao mDao;
        public UpdateByIdAndDomain(MetaDataDao dao, String domain) {
            mDao = dao;
            domainName = domain;
        }
        @Override
        protected Object doInBackground(Integer... integers) {
            mDao.update(integers[0].intValue(), domainName);
            return null;
        }
    }

    public static class UpdateByFrequencyAndDomain extends AsyncTask<Integer, Object, Object> {
        String domainName;
        MetaDataDao mDao;
        public UpdateByFrequencyAndDomain(MetaDataDao dao, String domain) {
            mDao = dao;
            domainName = domain;
        }
        @Override
        protected Object doInBackground(Integer... integers) {
            mDao.update(domainName, integers[0].intValue());
            return null;
        }
    }
    public static  class updateMetaDataTask extends AsyncTask<MetaDataEntity, Object, Object> {
        MetaDataDao mDao;

        public updateMetaDataTask(MetaDataDao dao) {
            mDao = dao;
        }
        @Override
        protected Object doInBackground(MetaDataEntity... metaDataEntities) {
            mDao.updateMetaData(metaDataEntities[0]);
            return null;
        }
    }

    public static  class deleteByIdTask extends AsyncTask<Integer, Object, Object> {
        MetaDataDao mDao;
        public deleteByIdTask(MetaDataDao dao) {
            mDao = dao;
        }

        @Override
        protected Object doInBackground(Integer... integers) {
            mDao.deleteMetaData(integers[0].intValue());
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
        }
    }

    public static  class deleteByMetaDataTask extends AsyncTask<MetaDataEntity, Object, Object> {
        MetaDataDao mDao;
        public deleteByMetaDataTask(MetaDataDao dao) {
            mDao = dao;
        }
        @Override
        protected Object doInBackground(MetaDataEntity... metaDataEntities) {
            mDao.deleteMetaData(metaDataEntities[0]);
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

    private static LiveData<List<MetaDataEntity>>  getAll() {
        LiveData<List<MetaDataEntity>> ret = null;
        /*if (mDatabase != null) {
            ret = mDatabase.MetaDataDao().getAll();
        }*/
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

    private static void updateById(int id, String domain) {
        if (mDatabase != null) {
            mDatabase.MetaDataDao().update(id, domain);
        }
    }

    private static void updateByFrequencyAndDomain(String domain, int frq) {
        if (mDatabase != null) {
            mDatabase.MetaDataDao().update(domain, frq);
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
