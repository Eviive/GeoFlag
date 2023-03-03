package com.iut.geoflag.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
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

class MainActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val countries = ArrayList<Country>()

    private val homeFragment = HomeFragment(countries)
    private val quizFragment = QuizFragment()
    private val mapFragment = MapFragment()
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Permission denied")
                .setMessage("You denied the permission to receive notifications. You can change this in the settings.")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private var currentTab = 1
    private var loadedData = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        askNotificationPermission()

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
                    loadFragment(quizFragment, 2)
                    true
                }
                R.id.navigation_map -> {
                    loadFragment(mapFragment, 3)
                    true
                }
                else -> {
                    loadFragment(homeFragment, 1)
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
                    loadFragment(homeFragment, 1)
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

    fun seeOnGoogleMaps(country: Country) {
        mapFragment.country = country
        binding.navigationView.selectedItemId = R.id.navigation_map
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                logToken()
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to receive notifications.")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    .show()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            logToken()
        }
    }

    private fun logToken() {
        Firebase.messaging.token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.i("MainActivity", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result

            Log.i("MainActivity", token)
            Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
        })
    }

}