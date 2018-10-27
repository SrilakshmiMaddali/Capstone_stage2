package sm.com.camcollection.data;

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

    @Query("DELETE FROM pass_data_table")
    void deleteAll();

    @Query("SELECT * FROM pass_data_table")
    List<MetaDataEntity> getAll();

    @Query("SELECT * FROM pass_data_table WHERE id = :metaDataId")
    MetaDataEntity getMetaDatabyId(int metaDataId);

    @Update
    void updateMetaData (MetaDataEntity metaDataEntity);

    @Query("DELETE FROM pass_data_table WHERE id = :metaDataId")
    void deleteMetaData(int metaDataId);

    @Delete
    void deleteMetaData(MetaDataEntity metaDataEntity);
}
