package map.framework.api

import com.squareup.moshi.Json

/**
 * Data class encapsulating a response from the nearby search call to the Places API.
 */
data class NearbyPlacesResponse(
    @Json(name = "results") val results: List<GPlace>
)