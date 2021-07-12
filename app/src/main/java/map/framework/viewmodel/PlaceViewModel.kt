package map.framework.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import map.framework.database.place.PlaceData
import map.framework.repository.PlaceRepository

/**
 * View Model to keep a reference to the place repository and
 * an up-to-date list of all places.
 */

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