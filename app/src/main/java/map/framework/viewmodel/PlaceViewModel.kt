package map.framework.viewmodel

import android.location.Location
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import map.framework.BuildConfig.GOOGLE_MAPS_API_KEY
import map.framework.api.GPlace
import map.framework.api.PlacesApi
import map.framework.database.place.PlaceData
import map.framework.place.Place
import map.framework.repository.PlaceRepository

/**
 * View Model to keep a reference to the place repository and
 * an up-to-date list of all places.
 */
class PlaceViewModel(private val repository: PlaceRepository) : ViewModel() {
    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<String>()

    // The external immutable LiveData for the request status
    val status: LiveData<String> = _status

    // Using LiveData and caching what places returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val places: LiveData<List<PlaceData>> = repository.places.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(place: PlaceData) = viewModelScope.launch {
        repository.insert(place)
    }

//    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    fun allPlaces(): List<Place> {
        var repo_places : List<Place> = emptyList()
        viewModelScope.launch {
            repo_places = repository.allPlaces()
        }
        return repo_places
    }
    private val _g_places = MutableLiveData<List<GPlace>>()
    val g_places: LiveData<List<GPlace>> = _g_places

    fun getNearbyPlaces(location: Location) {
        val apiKey = GOOGLE_MAPS_API_KEY
        viewModelScope.launch {
            try {
                val results = PlacesApi.retrofitService.nearbyPlaces(
                    apiKey = apiKey,
                    location = "${location.latitude},${location.longitude}",
                    radiusInMeters = 2000,
                    placeType = "park").results
                _g_places.value = results
                _status.value = "   First Place : ${_g_places.value!![0].name}"
            } catch (e: Exception) {
                _status.value = "Failure: ${e.message}, ${e.stackTraceToString()}"
            }
        }
    }
}

class PlaceViewModelFactory(private val repository: PlaceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlaceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
