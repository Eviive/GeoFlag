package com.iut.geoflag.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.formapp.utils.StorageManager
import com.iut.geoflag.activities.GameActivity
import com.iut.geoflag.databinding.FragmentQuizBinding
import com.iut.geoflag.models.Country
import com.iut.geoflag.models.Game

class QuizFragment(private var countries: ArrayList<Country>, private var gameLuncher: ActivityResultLauncher<Intent>): Fragment() {

    private lateinit var binding: FragmentQuizBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentQuizBinding.inflate(inflater, container, false)

        binding.startButton.setOnClickListener {
            val intent = Intent(context, GameActivity::class.java)
            val game = Game(45_000, countries)
            intent.putExtra("game", game)
            gameLuncher.launch(intent)
        }

        updateBestScore()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        updateBestScore()
    }

    private fun updateBestScore() {
        val bestScore = StorageManager.load<Int>(requireContext(), "bestScore") ?: 0
        val text = "Best score: $bestScore"
        binding.bestScore.text = text
    }

}