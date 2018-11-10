package sm.com.camcollection.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MetaDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MetaDataEntity data);

    @Query("DELETE FROM pass_data_table_1")
    void deleteAll();

    @Query("SELECT * FROM pass_data_table_1")
    LiveData<List<MetaDataEntity>>  getAll();

    @Query("SELECT * FROM pass_data_table_1")
    List<MetaDataEntity>  getAllRecords();

    @Query("SELECT * FROM pass_data_table_1 WHERE positionId = :metaDataId")
    MetaDataEntity getMetaDatabyId(int metaDataId);

    @Update
    void updateMetaData (MetaDataEntity metaDataEntity);

    @Query("UPDATE pass_data_table_1 SET id= :metaDataId WHERE domain= :domainName")
    void update(int metaDataId, String domainName);

    @Query("UPDATE pass_data_table_1 SET frequency= :frq WHERE domain= :domainName")
    void update(String domainName, int frq);

    @Query("DELETE FROM pass_data_table_1 WHERE positionId = :metaDataId")
    void deleteMetaData(int metaDataId);

    @Delete
    void deleteMetaData(MetaDataEntity metaDataEntity);
}
