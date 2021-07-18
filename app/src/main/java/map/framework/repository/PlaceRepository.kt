package map.framework.repository

import android.content.Context
import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import map.framework.api.PlacesService
import map.framework.database.place.PlaceDao
import map.framework.database.place.PlaceData
import map.framework.place.Place
import map.framework.place.PlacesReader

/**
 * Abstracted Repository as promoted by the Architecture Guide.
 * https://developer.android.com/topic/libraries/architecture/guide.html
 */
class PlaceRepository(private val placeDao: PlaceDao, private val context: Context) {
    private lateinit var placesService: PlacesService
//    placesService = PlacesService.create()
//    private fun getNearbyPlaces(location: Location) {
//        val apiKey = this.getString(R.string.google_maps_key)
//        placesService.nearbyPlaces(
//            apiKey = apiKey,
//            location = "${location.latitude},${location.longitude}",
//            radiusInMeters = 2000,
//            placeType = "park"
//        ).enqueue(
//            object : Callback<NearbyPlacesResponse> {
//                override fun onFailure(call: Call<NearbyPlacesResponse>, t: Throwable) {
//                    Log.e(TAG, "Failed to get nearby places", t)
//                }
//
//                override fun onResponse(
//                    call: Call<NearbyPlacesResponse>,
//                    response: Response<NearbyPlacesResponse>
//                ) {
//                    if (!response.isSuccessful) {
//                        Log.e(TAG, "Failed to get nearby places")
//                        return
//                    }
//
//                    val places = response.body()?.results ?: emptyList()
//                    this@MainActivity.places = places
//                }
//            }
//        )
//    }

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

    suspend fun allPlaces(): List<Place> {
        return PlacesReader(context).read()
    }
}
