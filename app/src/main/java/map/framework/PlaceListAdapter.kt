package map.framework

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import map.framework.databinding.FragmentItemBinding
import map.framework.place.Place

/**
 * [ListAdapter] implementation for the recyclerview.
 */

class PlaceListAdapter(private val onItemClicked: (Place) -> Unit) :
    ListAdapter<Place, PlaceListAdapter.PlaceViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        return PlaceViewHolder(
            FragmentItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }

    class PlaceViewHolder(private var binding: FragmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(place: Place) {
            binding.itemNumber.text = place.name
            binding.content.text = place.latLng.toString()
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Place>() {
            override fun areItemsTheSame(oldPlace: Place, newPlace: Place): Boolean {
                return oldPlace === newPlace
            }

            override fun areContentsTheSame(oldPlace: Place, newPlace: Place): Boolean {
                return oldPlace.name == newPlace.name
            }
        }
    }
}