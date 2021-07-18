package map.framework.api

//import com.google.ar.sceneform.math.Vector3
import com.google.android.gms.maps.model.LatLng

/**
 * A model describing details about a Place (location, name, type, etc.).
 */
data class GPlace(
    val id: String,
    val icon: String,
    val name: String,
    val geometry: Geometry
) {
    override fun equals(other: Any?): Boolean {
        if (other !is GPlace) {
            return false
        }
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return this.id.hashCode()
    }
}

//fun GPlace.getPositionVector(azimuth: Float, latLng: LatLng): Vector3 {
//    val placeLatLng = this.geometry.location.latLng
//    val heading = latLng.sphericalHeading(placeLatLng)
//    val r = -2f
//    val x = r * sin(azimuth + heading).toFloat()
//    val y = 1f
//    val z = r * cos(azimuth + heading).toFloat()
//    return Vector3(x, y, z)
//}

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
