package map.framework

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.awaitMapLoad
import map.framework.database.place.PlaceData
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

    private fun getBounds(places :List<PlaceData>) : LatLngBounds.Builder
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

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        lifecycleScope.launchWhenCreated {
            // Get map
            val map = mapFragment.awaitMap()
            mMap = map

//            addClusteredMarkers(googleMap)

            // Wait for map to finish loading
            mMap!!.awaitMapLoad()
            Log.d(TAG, "awaitMapLoad")
        }

        placeViewModel.places.observe(viewLifecycleOwner, { places ->
            try
            {
                val bounds = getBounds(places)
                Log.d(TAG, bounds.build().toString())
                mMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100))
            }
            catch(t: Throwable)
            {
                Log.d(TAG, t.toString())
            }
        })
    }
}