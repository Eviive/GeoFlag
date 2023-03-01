package com.iut.geoflag.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iut.geoflag.databinding.CountryItemBinding
import com.iut.geoflag.models.Country

class CountryAdapter(private val countries: List<Country>): ListAdapter<Country, CountryAdapter.ItemViewHolder>(CountryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = CountryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(countries[position])
    }

    override fun getItemCount(): Int {
        return countries.size
    }

    inner class ItemViewHolder(private val binding: CountryItemBinding) : RecyclerView.ViewHolder(binding.root) {

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

            binding.name.text = country.name.official

            itemView.setOnClickListener {
                Toast.makeText(itemView.context, country.name.official, Toast.LENGTH_SHORT).show()
            }
        }

    }

    class CountryDiffCallback: DiffUtil.ItemCallback<Country>() {
        override fun areItemsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem.name.official == newItem.name.official
        }

        override fun areContentsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem == newItem
        }
    }

}