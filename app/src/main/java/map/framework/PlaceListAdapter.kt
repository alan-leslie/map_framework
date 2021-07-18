package map.framework

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import map.framework.database.place.PlaceData
import map.framework.databinding.FragmentItemBinding

/**
 * [ListAdapter] implementation for the recyclerview.
 */

class PlaceListAdapter(private val onItemClicked: (PlaceData) -> Unit) :
    ListAdapter<PlaceData, PlaceListAdapter.PlaceViewHolder>(DiffCallback) {

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

        fun bind(place: PlaceData) {
            binding.itemNumber.text = place.name
            binding.content.text = place.latLng.toString()
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<PlaceData>() {
            override fun areItemsTheSame(oldPlace: PlaceData, newPlace: PlaceData): Boolean {
                return oldPlace === newPlace
            }

            override fun areContentsTheSame(oldPlace: PlaceData, newPlace: PlaceData): Boolean {
                return oldPlace.name == newPlace.name
            }
        }
    }
}