package sm.com.camcollection.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.Executor;

public class MetaDataRepository {
    private MetaDataDao metaDataDao;
    private LiveData<List<MetaDataEntity>>  mAllRecords;
    private Executor executor;

    MetaDataRepository(Application application) {
        MetaDataDatabase db = MetaDataDatabase.getDatabase(application);
        metaDataDao = db.MetaDataDao();
        mAllRecords = metaDataDao.getAll();
    }

    public LiveData<List<MetaDataEntity>> getAllRecords() {
        return mAllRecords;
    }

    public void deleteAll() {
        new DatabaseTask.DeleteAllTask(metaDataDao).execute();
    }

    public void insert(MetaDataEntity entity) {
        new DatabaseTask.InsertTask(metaDataDao).execute(entity);
    }

    public void deleteMetaData(MetaDataEntity entity) {
        new DatabaseTask.deleteByMetaDataTask(metaDataDao).execute(entity);
    }

    public void deleteById(int id) {
        new DatabaseTask.deleteByIdTask(metaDataDao).execute(id);
    }

    public MetaDataEntity getMetadataByid(int id) {
        return metaDataDao.getMetaDatabyId(id);
    }

    public void updateMetaData(MetaDataEntity entity) {
        new DatabaseTask.updateMetaDataTask(metaDataDao).execute(entity);
    }

    public void UpdateByFrequencyAndDomain(int frequency, String domain) {
        new DatabaseTask.UpdateByFrequencyAndDomain(metaDataDao, domain).execute(frequency);
    }

    public void UpdateByIdAndDomain(String domain, int id) {
        new DatabaseTask.UpdateByIdAndDomain(metaDataDao, domain).execute(id);
    }

    private void refreshAllRecords() {
        final MutableLiveData<List<MetaDataEntity>> data = new MutableLiveData<List<MetaDataEntity>>();
        //data.setValue(metaDataDao.getAll());
        //mAllRecords = data;
    }
}
