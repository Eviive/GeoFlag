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
        binding.name.text = country.name.common

        val capital = getString(R.string.country_capital) + " : " + country.capital[0]
        binding.capital.text = capital

        val region =
            getString(R.string.country_region) + " : " + country.subregion + ", " + country.region
        binding.region.text = region

        val languages =
            getString(R.string.country_languages) + " : " + country.languages.values.joinToString(", ")
        binding.languages.text = languages

        val coordinate1 = (country.latlng[0] * 100).roundToInt() / 100.0
        val coordinate2 = (country.latlng[1] * 100).roundToInt() / 100.0
        val coordinates =
            getString(R.string.country_coordinates) + " : " + coordinate1.toString() + "°N , " + coordinate2.toString() + "°E"
        binding.coordinates.text = coordinates

        val str = String.format("%,d", country.area.toInt()).replace(',', ' ')
        val area = getString(R.string.country_area) + " : " + str + " km²"
        binding.area.text = area

        val str2 = String.format("%,d", country.population).replace(',', ' ')
        val population =
            getString(R.string.country_population) + " : " + str2 + " " + getString(R.string.inhabitants)
        binding.population.text = population

        if (country.flags.containsKey("png")) {
            Glide.with(this)
                .load(country.flags["png"])
                .into(binding.flag)

            if (country.flags.containsKey("alt")) {
                binding.flag.contentDescription = country.flags["alt"]
            } else {
                binding.flag.contentDescription = "Flag of ${country.name.official}"
            }
        }
    }

}