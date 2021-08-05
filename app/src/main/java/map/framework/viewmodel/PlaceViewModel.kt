package map.framework.viewmodel

import android.location.Location
import android.os.Parcel
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import map.framework.api.GPlace
import map.framework.database.place.PlaceData
import map.framework.place.Place
import map.framework.repository.PlaceRepository

/**
 * View Model to keep a reference to the place repository and
 * an up-to-date list of all places.
 */

val parcel: Parcel? = null

class PlaceViewModel(private val repository: PlaceRepository) : ViewModel() {
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

    private val scope = CoroutineScope(Job() + Dispatchers.IO)


    fun allPlaces(): List<Place> {
        var repo_places : List<Place> = emptyList()
        scope.launch {
            repo_places = repository.allPlaces()
        }
        return repo_places
    }

    // TODO - pass in current location
    fun nearbyPlaces(): List<GPlace> {
        val location: Location? = null
        var repo_places : List<GPlace> = emptyList()
        scope.launch {
            repo_places = repository.getNearbyPlaces(location!!)
        }
        return repo_places
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
