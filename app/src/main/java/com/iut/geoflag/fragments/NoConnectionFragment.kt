package com.iut.geoflag.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.iut.geoflag.databinding.FragmentNoConnectionBinding

class NoConnectionFragment: Fragment() {

    private lateinit var binding: FragmentNoConnectionBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNoConnectionBinding.inflate(inflater, container, false)

        return binding.root
    }

}