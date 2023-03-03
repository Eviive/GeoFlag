package com.iut.geoflag.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.iut.geoflag.databinding.FragmentMapBinding
import com.iut.geoflag.models.Country

class MapFragment: Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapBinding

    var country: Country? = null

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

        country?.let { c ->
            val latLng = LatLng(c.latlng[0], c.latlng[1])

            val zoom = when (c.area) {
                in 0.0 .. 100000.0 -> 6f
                in 100000.0 .. 1000000.0 -> 5f
                in 1000000.0 .. 10000000.0 -> 4f
                in 10000000.0 .. 100000000.0 -> 3f
                in 100000000.0 .. 1000000000.0 -> 2f
                else -> 1f
            }

            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom)

            val options = MarkerOptions()
                .position(latLng)
                .title(c.name.common)

            val marker = googleMap.addMarker(options)

            marker?.tag = c

            googleMap.animateCamera(cameraUpdate)

            country = null
        }
    }

}