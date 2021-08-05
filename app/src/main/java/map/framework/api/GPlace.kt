package map.framework.api

//import com.google.ar.sceneform.math.Vector3
import com.google.android.gms.maps.model.LatLng

/**
 * A model describing details about a Place (location, name, type, etc.).
 */
data class GPlace(
    val place_id: String,
    val icon: String,
    val name: String,
    val geometry: Geometry
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
