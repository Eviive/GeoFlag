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
import com.iut.geoflag.activities.GameActivity
import com.iut.geoflag.databinding.FragmentQuizBinding
import com.iut.geoflag.models.Country
import com.iut.geoflag.models.Game

class QuizFragment(private var countries: ArrayList<Country>): Fragment() {

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
            val game = Game(30, countries)
            intent.putExtra("game", game)
            startGameForResult.launch(intent)
        }

        return binding.root
    }

}