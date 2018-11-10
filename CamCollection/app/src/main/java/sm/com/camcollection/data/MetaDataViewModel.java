package sm.com.camcollection.data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class MetaDataViewModel extends AndroidViewModel {
    private MetaDataRepository mRepository;
    private LiveData<List<MetaDataEntity>> allRecords = new MutableLiveData();

    public MetaDataViewModel(@NonNull Application application) {
        super(application);
        mRepository = new MetaDataRepository(application);
        allRecords = mRepository.getAllRecords();
    }

    public LiveData<List<MetaDataEntity>> getAllRecords() {
        return this.allRecords;
    }

    private void setAllRecords(List<MetaDataEntity> entitiyList) {
        //allRecords.setValue(entitiyList);
    }
    public LiveData<List<MetaDataEntity>>  insert(MetaDataEntity entity) {
        mRepository.insert(entity);
        return getAllRecords();
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }

    public MetaDataEntity getMetaDataById(int id) {
        return mRepository.getMetadataByid(id);
    }

    public void deleteByMetaData(MetaDataEntity entity) {
        mRepository.deleteMetaData(entity);
    }

    public  void deleteById(int id) {
        mRepository.deleteById(id);
    }

    public void updateMetaData(MetaDataEntity entity) {
        mRepository.updateMetaData(entity);
    }

    public void UpdateByFrequencyAndDomain(int frequency, String domain) {
        mRepository.UpdateByFrequencyAndDomain(frequency, domain);
    }

    public void UpdateByIdAndDomain(String domain, int id) {
        mRepository.UpdateByIdAndDomain(domain, id);
    }
}
