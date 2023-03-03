package com.iut.geoflag.activities

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
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
import com.iut.geoflag.fragments.NoConnectionFragment
import com.iut.geoflag.fragments.QuizFragment
import com.iut.geoflag.models.Country
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var currentTab = 1
    private var loadedData = false

    private val countries = ArrayList<Country>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkForInternet()) {
            loadCountries()
        } else {
            loadFragment(NoConnectionFragment(), 1)
            setupNetworkCallback()
        }

        binding.navigationView.setOnItemSelectedListener {
            if (!loadedData || currentTab == it.itemId) {
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

    @OptIn(DelicateCoroutinesApi::class)
    private fun loadCountries() {
        val countriesAPI = RetrofitHelper.getInstance().create(CountriesAPI::class.java)

        GlobalScope.launch {
            val response = countriesAPI.getCountries()

            if (response.isSuccessful) {
                val result = response.body()

                if (result != null) {
                    countries.addAll(result)
                    loadedData = true
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
    }

    private fun loadFragment(fragment: Fragment, index: Int){
        val transaction = supportFragmentManager.beginTransaction()

        if (currentTab < index) {
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
        } else if (currentTab > index) {
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        } else {
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        currentTab = index

        transaction.replace(binding.fragmentContainer.id, fragment)
        transaction.commit()
    }

    private fun checkForInternet(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false

        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }

        // combiner les 2 techniques pour check internet
        // vérifier une première fois si le téléphone est connecté à internet :
        //     si oui load les données
        //     si non afficher le fragment NoConnectionFragment
        //            setup le network callback pour load les données quand le téléphone sera connecté à internet
    }

    private fun setupNetworkCallback() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                if (!loadedData) {
                    loadCountries()
                }
            }
        }

        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

}