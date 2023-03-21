package com.iut.geoflag.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.iut.geoflag.R
import com.iut.geoflag.databinding.ActivityDetailsBinding
import com.iut.geoflag.models.Country
import kotlin.math.roundToInt


class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding

    private var country: Country? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        country = intent.getSerializableExtra("country") as Country

        country?.let { bindCountry(it) }

        binding.previous.setOnClickListener {
            val intent = Intent()
            intent.putExtra("map", false)
            setResult(RESULT_OK, intent)
            finish()
        }
        binding.map.setOnClickListener {
            val intent = Intent()
            intent.putExtra("map", true)
            intent.putExtra("country", country)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun bindCountry(country: Country) {

        binding.name.text = country.getName().common

        if (country.capital.isNullOrEmpty())
            binding.capital.text = getString(R.string.no_capital)
        else
            binding.capital.text = country.capital.first()

        binding.region.text =
            if (country.subregion != null)
                "${country.subregion} ${country.region}"
            else
                country.region

        val languages = country.languages?.values?.first() ?: "N/A"
        binding.languages.text = languages

        val coordinate1 = (country.latlng[0] * 100).roundToInt() / 100.0
        val coordinate2 = (country.latlng[1] * 100).roundToInt() / 100.0

        val coordinates = "$coordinate1°N , $coordinate2°E"
        binding.coordinates.text = coordinates

        val area = String.format("%,d km²", country.area.toInt())
        binding.area.text = area

        val population = String.format("%,d %s", country.population, getString(R.string.inhabitants))
        binding.population.text = population

        if (country.flags.containsKey("png")) {
            Glide.with(this)
                .load(country.flags["png"])
                .into(binding.flag)

            if (country.flags.containsKey("alt")) {
                binding.flag.contentDescription = country.flags["alt"]
            } else {
                binding.flag.contentDescription = "Flag of ${country.getName().official}"
            }
        }
    }

}