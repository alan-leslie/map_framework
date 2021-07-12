package map.framework.repository

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import map.framework.database.place.PlaceDao
import map.framework.database.place.PlaceData

/**
 * Abstracted Repository as promoted by the Architecture Guide.
 * https://developer.android.com/topic/libraries/architecture/guide.html
 */
class PlaceRepository(private val placeDao: PlaceDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val places: Flow<List<PlaceData>> = placeDao.getPlaces()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(place: PlaceData) {
        placeDao.insert(place)
    }
}
