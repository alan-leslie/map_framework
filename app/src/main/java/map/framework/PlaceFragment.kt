package map.framework

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import map.framework.databinding.FragmentItemListBinding
import map.framework.viewmodel.PlaceViewModel
import map.framework.viewmodel.PlaceViewModelFactory

/**
 * A fragment representing a list of Items.
 */
class PlaceFragment : Fragment() {
    private val placeViewModel: PlaceViewModel by activityViewModels {
        PlaceViewModelFactory((activity?.application as MapApplication).repository)
    }

    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!!

    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PlaceListAdapter {
//            Log.d("hello", "there")
            Toast.makeText(
                context,
                "Marker clicked: " + it.name,
                Toast.LENGTH_SHORT
            ).show()
         }
        binding.list.layoutManager = LinearLayoutManager(this.context)
        binding.list.adapter = adapter
        // Attach an observer on the allItems list to update the UI automatically when the data
        // changes.
        placeViewModel.places.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            PlaceFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}