package com.iut.geoflag.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.iut.geoflag.R
import com.iut.geoflag.databinding.CountryItemBinding
import com.iut.geoflag.models.Country
import com.iut.geoflag.utils.StringUtils
import java.util.*

class CountryAdapter(
    private val countries: List<Country>,
    private val seeDetail:(country: Country) -> Unit,
    private val seeOnGoogleMaps: (country: Country) -> Unit
) : ListAdapter<Country, CountryAdapter.ItemViewHolder>(CountryDiffCallback()), Filterable {

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
                    val query = constraint.toString().lowercase(Locale.ROOT)
                    filteredList.sortBy {
                        val name = it.getName().common.lowercase(Locale.ROOT)
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

    inner class ItemViewHolder(private val binding: CountryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(country: Country) {
            if (country.flags.containsKey("png")) {
                Glide.with(itemView.context)
                    .load(country.flags["png"])
                    .into(binding.countryFlag)

                if (country.flags.containsKey("alt")) {
                    binding.countryFlag.contentDescription = country.flags["alt"]
                } else {
                    binding.countryFlag.contentDescription = "Flag of ${country.getName().official}"
                }
            }

            binding.countryName.text = country.getName().common
            binding.countryPopulation.text = country.population.toString()

            if (country.capital.isNullOrEmpty()){
                binding.countryCapital.text = itemView.context.getString(R.string.no_capital)
            } else {
                binding.countryCapital.text = country.capital.first()
            }

            itemView.setOnClickListener {
                MaterialAlertDialogBuilder(itemView.context)
                    .setTitle(country.getName().official)
                    .setItems(arrayOf(
                        itemView.context.getString(R.string.see_detail),
                        itemView.context.getString(R.string.see_on_google_maps)
                        )
                    ) { _, which ->
                        when (which) {
                            0 -> {
                                seeDetail(country)
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

    class CountryDiffCallback : DiffUtil.ItemCallback<Country>() {

        override fun areItemsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem == newItem
        }

    }

}