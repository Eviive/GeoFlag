package com.iut.geoflag.fragments

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.iut.geoflag.activities.DetailsActivity
import com.iut.geoflag.databinding.FragmentMapBinding
import com.iut.geoflag.models.Country
import java.util.*

class MapFragment(private val countries: List<Country>, private val detailsLauncher: ActivityResultLauncher<Intent>) : Fragment(),
    OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var binding: FragmentMapBinding

    var markedCountry: Country? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)

        MapsInitializer.initialize(requireContext(), MapsInitializer.Renderer.LATEST) {
            val mapFragment = childFragmentManager.findFragmentById(binding.map.id) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.clear()

        googleMap.setOnMapClickListener {
            googleMap.clear()

            val geocoder = Geocoder(requireActivity(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)

            if (addresses?.isNotEmpty() == true) {
                val country = countries.find { c -> c.cca2 == addresses[0].countryCode }

                country?.let { c ->
                    createMarker(googleMap, c)
                }
            }
        }

        markedCountry?.let { c ->
            createMarker(googleMap, c)
            markedCountry = null
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val intent = Intent(requireContext(), DetailsActivity::class.java)
        intent.putExtra("country", marker.tag as Country)
        detailsLauncher.launch(intent)
        return true
    }

    private fun createMarker(googleMap: GoogleMap, country: Country) {
        val latLng = LatLng(country.latlng[0], country.latlng[1])

        val zoom = when (country.area) {
            in 0.0..100000.0 -> 6f
            in 100000.0..1000000.0 -> 5f
            in 1000000.0..10000000.0 -> 4f
            in 10000000.0..100000000.0 -> 3f
            in 100000000.0..1000000000.0 -> 2f
            else -> 1f
        }

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom)

        val options = MarkerOptions()
            .position(latLng)
            .title(country.getName().common)

        val marker = googleMap.addMarker(options)

        marker?.tag = country

        googleMap.animateCamera(cameraUpdate)

        googleMap.setOnMarkerClickListener(this)
    }

}