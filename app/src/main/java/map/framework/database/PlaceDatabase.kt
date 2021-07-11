package map.framework.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PlaceData::class],
    version = 1,
    exportSchema = false
)
abstract class PlaceDatabase : RoomDatabase() {

    abstract fun reposDao(): PlaceDao

    companion object {
        @Volatile
        private var INSTANCE: PlaceDatabase? = null

        fun getInstance(context: Context): PlaceDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                PlaceDatabase::class.java, "places.db")
                .build()
    }
}