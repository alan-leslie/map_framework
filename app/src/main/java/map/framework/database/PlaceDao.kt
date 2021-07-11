package map.framework.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Provides access to read/write operations on the schedule table.
 * Used by the view models to format the query results for use in the UI.
 */
@Dao
interface PlaceDao {
    @Query("SELECT * FROM PlaceData ORDER BY name ASC")
    fun getAll(): Flow<List<PlaceData>>
}