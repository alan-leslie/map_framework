package map.framework.api

import com.google.android.gms.maps.model.LatLng
import map.framework.database.place.PlaceData
import map.framework.place.Place

/**
 * A model describing details about a Place (location, name, type, etc.).
 */
data class GPlace(
    val place_id: String,
    val icon: String,
    val name: String,
    val geometry: Geometry,
    val vicinity: String,
    val rating: Float
) {
    override fun equals(other: Any?): Boolean {
        if (other !is GPlace) {
            return false
        }
        return this.place_id == other.place_id
    }

    override fun hashCode(): Int {
        return this.place_id.hashCode()
    }
}

data class Geometry(
    val location: GeometryLocation
)

data class GeometryLocation(
    val lat: Double,
    val lng: Double
) {
    val latLng: LatLng
        get() = LatLng(lat, lng)
}

fun GPlace.toPlace(): Place = Place(
    name = name,
    latLng = LatLng(geometry.location.lat, geometry.location.lng),
    address = vicinity,
    rating = rating
)

fun GPlace.toPlaceData(): PlaceData = PlaceData(
    name = name,
    latitude = geometry.location.lat,
    longitude = geometry.location.lng,
    address = vicinity,
    rating = rating
)