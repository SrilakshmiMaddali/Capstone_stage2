package sm.com.passwordwallet.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

@Database(entities = {MetaDataEntity.class}, version = 2, exportSchema=false)
public abstract class MetaDataDatabase extends RoomDatabase {

    public abstract MetaDataDao MetaDataDao();

    private static volatile MetaDataDatabase INSTANCE;

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };

    public static MetaDataDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MetaDataDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MetaDataDatabase.class, "pass_data_database")
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
