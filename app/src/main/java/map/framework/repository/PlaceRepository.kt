package map.framework.repository

import android.content.Context
import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import map.framework.database.place.PlaceDao
import map.framework.database.place.PlaceData
import map.framework.place.Place
import map.framework.place.PlacesReader


/**
 * Abstracted Repository as promoted by the Architecture Guide.
 * https://developer.android.com/topic/libraries/architecture/guide.html
 */
class PlaceRepository(private val placeDao: PlaceDao, private val context: Context) {
    private val _TAG = "PlaceRepository"

    suspend fun dbPlaces(): List<PlaceData>
    {
        return placeDao.getPlaces()
    }

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(place: PlaceData) {
        placeDao.insert(place)
    }

    suspend fun allPlaces(): List<Place> {
        return PlacesReader(context).read()
    }
}
