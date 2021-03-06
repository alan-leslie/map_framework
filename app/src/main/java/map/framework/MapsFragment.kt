package map.framework

import android.content.Context
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.ktx.addCircle
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.awaitMapLoad
import map.framework.place.Place
import map.framework.place.PlaceRenderer
import map.framework.viewmodel.PlaceViewModel
import map.framework.viewmodel.PlaceViewModelFactory


class MapsFragment : Fragment() {
    private var mMap: GoogleMap? = null

    companion object {
        private val TAG = MapsFragment::class.java.simpleName
    }

    private val placeViewModel: PlaceViewModel by activityViewModels {
        PlaceViewModelFactory((activity?.application as MapApplication).repository)
    }

    private fun getBounds(places :List<Place>) : LatLngBounds.Builder
    {
        val bounds = LatLngBounds.builder()
        places.forEach { bounds.include(it.latLng) }
        return bounds
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        placeViewModel.getDBPlaces()
//        val edinburghCastle = Location(LocationManager.GPS_PROVIDER)
//        edinburghCastle.latitude = 55.9487328
//        edinburghCastle.longitude = -3.199945
        val victoriaBar = Location(LocationManager.GPS_PROVIDER)
        victoriaBar.latitude = 55.965787
        victoriaBar.longitude = -3.175620
        placeViewModel.getNearbyPlaces(victoriaBar)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        lifecycleScope.launchWhenCreated {
            // Get map
            val map = mapFragment.awaitMap()
            mMap = map

            // Wait for map to finish loading
            mMap!!.awaitMapLoad()
            Log.d(TAG, "awaitMapLoad")

            placeViewModel.places.value.let {
                val bounds = getBounds(placeViewModel.places.value!!)
                mMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100))
            }
        }

        placeViewModel.places.observe(viewLifecycleOwner, {
            try
            {
//                addHeatmap(mMap!!, it)
                val bounds = getBounds(it)
                Log.d(TAG, "bounds.build().toString()")
                mMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100))
                addClusteredMarkers(mMap!!, it)
//               addMarkers(mMap!!, it)
            }
            catch(t: Throwable)
            {
                Log.d(TAG, t.toString())
            }
        })
    }

    /**
     * Adds markers to the map with clustering support.
     */
    private fun addClusteredMarkers(googleMap: GoogleMap, places: List<Place>) {
        // Create the ClusterManager class and set the custom renderer
        val clusterManager = ClusterManager<Place>(this.requireContext(), googleMap)
        clusterManager.renderer =
            PlaceRenderer(
                this.requireContext(),
                googleMap,
                clusterManager
            )

        // Set custom info window adapter
        clusterManager.markerCollection.setInfoWindowAdapter(MarkerInfoWindowAdapter(this.requireContext()))

        // Add the places to the ClusterManager
        clusterManager.addItems(places)
        clusterManager.cluster()

        // Show polygon
        clusterManager.setOnClusterItemClickListener { item ->
            addCircle(googleMap, item, this.requireContext())
            return@setOnClusterItemClickListener false
        }

        // When the camera starts moving, change the alpha value of the marker to translucent
        googleMap.setOnCameraMoveStartedListener {
            clusterManager.markerCollection.markers.forEach { it.alpha = 0.3f }
            clusterManager.clusterMarkerCollection.markers.forEach { it.alpha = 0.3f }
        }

        googleMap.setOnCameraIdleListener {
            // When the camera stops moving, change the alpha value back to opaque
            clusterManager.markerCollection.markers.forEach { it.alpha = 1.0f }
            clusterManager.clusterMarkerCollection.markers.forEach { it.alpha = 1.0f }

            // Call clusterManager.onCameraIdle() when the camera stops moving so that re-clustering
            // can be performed when the camera stops moving
            clusterManager.onCameraIdle()
        }
    }

    private var circle: Circle? = null

    // [START maps_android_add_map_codelab_ktx_add_circle]
    /**
     * Adds a [Circle] around the provided [item]
     */
    private fun addCircle(googleMap: GoogleMap, item: Place, context : Context) {
        circle?.remove()
        circle = googleMap.addCircle {
            center(item.latLng)
            radius(1000.0)
            fillColor(ContextCompat.getColor(context, R.color.colorPrimaryTranslucent))
            strokeColor(ContextCompat.getColor(context, R.color.colorPrimary))
        }
    }

    private val barIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(this.requireContext(), R.color.colorPrimary)
        BitmapHelper.vectorToBitmap(this.requireContext(), R.drawable.ic_baseline_local_bar_24, color)
    }

    /**
     * Adds markers to the map. These markers won't be clustered.
     */
    private fun addMarkers(googleMap: GoogleMap, places: List<Place>) {
        for(place in places)
        {
            val marker = googleMap.addMarker(
                            MarkerOptions()
                                .position(place.latLng)
                                .title(place.name)
                                .icon(barIcon)
            )
            marker.tag = place
        }
    }

    /**
     * Adds heatmap  to the map using places from viewModel.
     */
    private fun addHeatmap(googleMap: GoogleMap, places: List<Place>) {
        val latLngs: MutableList<LatLng> = mutableListOf<LatLng>()
        for(place in places)
        {
            val newLatLng = LatLng(place.latLng.latitude, place.latLng.longitude)
            latLngs.add(newLatLng)
        }

        // Create the gradient.
        val colors = intArrayOf(
            Color.rgb(102, 225, 0),  // green
            Color.rgb(255, 0, 0) // red
        )
        val startPoints = floatArrayOf(0.2f, 1f)
        val gradient = Gradient(colors, startPoints)

        // Create the tile provider.
        val provider = HeatmapTileProvider.Builder()
            .data(latLngs)
            .gradient(gradient)
            .radius(50)
            .build()

        // Add the tile overlay to the map.
        val tileOverlay = googleMap.addTileOverlay(
            TileOverlayOptions()
                .tileProvider(provider)
        )
    }
}