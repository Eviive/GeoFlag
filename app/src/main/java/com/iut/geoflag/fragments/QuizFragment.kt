package com.iut.geoflag.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.iut.geoflag.R
import com.iut.geoflag.activities.GameActivity
import com.iut.geoflag.databinding.FragmentQuizBinding

class QuizFragment: Fragment() {

    private lateinit var binding: FragmentQuizBinding

    private val startGameForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val score = data?.getIntExtra("score", 0)
            Toast.makeText(requireContext(), "Score: $score", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentQuizBinding.inflate(inflater, container, false)

        binding.startButton.setOnClickListener {
            val intent = Intent(context, GameActivity::class.java)
            startGameForResult.launch(intent)
        }

        return binding.root
    }

}