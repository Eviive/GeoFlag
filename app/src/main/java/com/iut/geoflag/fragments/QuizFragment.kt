package com.iut.geoflag.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.iut.geoflag.databinding.FragmentQuizBinding

class QuizFragment: Fragment() {

    private lateinit var binding: FragmentQuizBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentQuizBinding.inflate(inflater, container, false)

        return binding.root
    }

}