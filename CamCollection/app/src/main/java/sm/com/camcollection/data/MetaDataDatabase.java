package sm.com.camcollection.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import sm.com.camcollection.MainActivity;

@Database(entities = {MetaDataEntity.class}, version = 3, exportSchema=false)
public abstract class MetaDataDatabase extends RoomDatabase {

    public abstract MetaDataDao MetaDataDao();

    public static volatile MetaDataDatabase INSTANCE;

    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
            //database.execSQL("DROP TABLE IF EXISTS pass_data_table");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2,3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
            //database.execSQL("DROP TABLE IF EXISTS pass_data_table");
        }
    };

    public static MetaDataDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MetaDataDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MetaDataDatabase.class, "pass_data_database_1")
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
