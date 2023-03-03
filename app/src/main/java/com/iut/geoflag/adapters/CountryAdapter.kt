package com.iut.geoflag.adapters

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.iut.geoflag.activities.DetailsActivity
import com.iut.geoflag.databinding.CountryItemBinding
import com.iut.geoflag.models.Country
import com.iut.geoflag.utils.StringUtils
import java.util.*

class CountryAdapter(private val countries: List<Country>, private val seeOnGoogleMaps: (country: Country) -> Unit): ListAdapter<Country, CountryAdapter.ItemViewHolder>(CountryDiffCallback()), Filterable {

    init {
        submitList(countries)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = CountryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<Country>()

                filteredList.addAll(countries)

                if (!constraint.isNullOrEmpty()) {
                    filteredList.sortBy {
                        val name = it.name.common.lowercase(Locale.ROOT)
                        val query = constraint.toString().lowercase(Locale.ROOT)
                        StringUtils.levenshteinDistance(name, query)
                    }

                    filteredList.removeAll(filteredList.subList(10, filteredList.size))
                }

                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                submitList(results?.values as List<Country>)
            }
        }
    }

    inner class ItemViewHolder(private val binding: CountryItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(country: Country) {
            if (country.flags.containsKey("png")) {
                Glide.with(itemView.context)
                    .load(country.flags["png"])
                    .into(binding.flag)

                if (country.flags.containsKey("alt")) {
                    binding.flag.contentDescription = country.flags["alt"]
                } else {
                    binding.flag.contentDescription = "Flag of ${country.name.official}"
                }
            }

            binding.name.text = country.name.common

            itemView.setOnClickListener {
                MaterialAlertDialogBuilder(itemView.context)
                    .setTitle(country.name.official)
                    .setItems(arrayOf("See details", "See on Google Maps")) { _, which ->
                        when (which) {
                            0 -> {
                                Toast.makeText(itemView.context, "See details", Toast.LENGTH_SHORT).show()

                                val intent = Intent(itemView.context, DetailsActivity::class.java)
                                intent.putExtra("country", country)
                                startActivity(itemView.context, intent, Bundle.EMPTY)
                            }
                            1 -> {
                                seeOnGoogleMaps(country)
                            }
                        }
                    }
                    .show()
            }
        }

    }

    class CountryDiffCallback: DiffUtil.ItemCallback<Country>() {

        override fun areItemsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem == newItem
        }

    }

}