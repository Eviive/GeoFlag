package com.iut.geoflag.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.iut.geoflag.R
import com.iut.geoflag.api.CountriesAPI
import com.iut.geoflag.api.RetrofitHelper
import com.iut.geoflag.databinding.ActivityMainBinding
import com.iut.geoflag.fragments.HomeFragment
import com.iut.geoflag.fragments.MapFragment
import com.iut.geoflag.fragments.QuizFragment
import com.iut.geoflag.models.Country
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var currentTab = 1
    private val countries = ArrayList<Country>()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val countriesAPI = RetrofitHelper.getInstance().create(CountriesAPI::class.java)

        GlobalScope.launch {
            val response = countriesAPI.getCountries()

            if (response.isSuccessful) {
                val result = response.body()

                if (result != null) {
                    countries.addAll(result)
                    loadFragment(HomeFragment(countries), 1)

                    Log.i("MainActivity", "Number of countries: ${result.size}")

                    for (country in result) {
                        Log.i("MainActivity", country.name.official)
                    }
                }
            } else {
                Log.e("MainActivity", response.errorBody().toString())
                Snackbar.make(binding.root, "Error fetching countries", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.navigationView.setOnItemSelectedListener {
            if (currentTab == it.itemId) {
                return@setOnItemSelectedListener false
            }
            when (it.itemId) {
                R.id.navigation_quiz -> {
                    loadFragment(QuizFragment(), 2)
                    true
                }
                R.id.navigation_map -> {
                    loadFragment(MapFragment(), 3)
                    true
                }
                else -> {
                    loadFragment(HomeFragment(countries), 1)
                    true
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment, index: Int){
        val transaction = supportFragmentManager.beginTransaction()

        if (currentTab < index) {
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
        } else if (currentTab > index) {
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        currentTab = index

        transaction.replace(binding.fragmentContainer.id, fragment)
        transaction.commit()
    }

}