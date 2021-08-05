package map.framework.repository

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import map.framework.BuildConfig.GOOGLE_MAPS_API_KEY
import map.framework.api.GPlace
import map.framework.api.NearbyPlacesResponse
import map.framework.api.PlacesService
import map.framework.database.place.PlaceDao
import map.framework.database.place.PlaceData
import map.framework.place.Place
import map.framework.place.PlacesReader
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Abstracted Repository as promoted by the Architecture Guide.
 * https://developer.android.com/topic/libraries/architecture/guide.html
 */
class PlaceRepository(private val placeDao: PlaceDao, private val context: Context) {
    private val _TAG = "PlaceRepository"

    private lateinit var placesService: PlacesService

    fun getNearbyPlaces(location: Location): List<GPlace> {
        placesService = PlacesService.create()
        var places : List<GPlace> = emptyList()
        val apiKey = GOOGLE_MAPS_API_KEY
        placesService.nearbyPlaces(
            apiKey = apiKey,
            location = "${location.latitude},${location.longitude}",
            radiusInMeters = 2000,
            placeType = "park"
        ).enqueue(
            object : Callback<NearbyPlacesResponse> {
                override fun onFailure(call: Call<NearbyPlacesResponse>, t: Throwable) {
                    Log.e(_TAG, "Failed to get nearby places", t)
                }

                override fun onResponse(
                    call: Call<NearbyPlacesResponse>,
                    response: Response<NearbyPlacesResponse>
                ) {
                    if (!response.isSuccessful) {
                        Log.e(_TAG, "Failed to get nearby places")
                        return
                    }

                    places = response.body()?.results ?: emptyList()
                }
            }
        )

        return places
    }

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
