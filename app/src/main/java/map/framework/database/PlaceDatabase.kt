package map.framework.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import map.framework.database.place.PlaceDao
import map.framework.database.place.PlaceData

@Database(entities = [PlaceData::class], version = 1, exportSchema = false)
abstract class PlaceRoomDatabase : RoomDatabase() {

    abstract fun placeDao(): PlaceDao

    companion object {
        @Volatile
        private var INSTANCE: PlaceRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): PlaceRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlaceRoomDatabase::class.java,
                    "word_database"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
                    .fallbackToDestructiveMigration()
                    .addCallback(WordDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private class WordDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Override the onCreate method to populate the database.
             */
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.placeDao())
                    }
                }
            }
        }

        /**
         * Populate the database in a new coroutine.
         * If you want to start with more words, just add them.
         */
        suspend fun populateDatabase(placeDao: PlaceDao) {
            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.
            placeDao.deleteAll()
            var place = PlaceData("Abode Bar", 55.966732939986635, -3.1743685852051162, "265 Leith Walk, Edinburgh", 4.4F)
            placeDao.insert(place)
            place = PlaceData("Victoria Bar", 55.96623487678024, -3.175574115342492, "add", 4.3F)
            placeDao.insert(place)
        }
    }
}
