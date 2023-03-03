package com.iut.geoflag.adapters

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
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

class CountryAdapter(private val countries: List<Country>, private val seeOnGoogleMaps: (country: Country) -> Unit): ListAdapter<Country, CountryAdapter.ItemViewHolder>(CountryDiffCallback()) {

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
            return oldItem.name.official == newItem.name.official
        }

        override fun areContentsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem == newItem
        }
    }

}