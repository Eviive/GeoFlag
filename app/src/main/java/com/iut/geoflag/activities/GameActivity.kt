package com.iut.geoflag.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.iut.geoflag.databinding.ActivityGameBinding
import com.iut.geoflag.fragments.QuestionFragment
import com.iut.geoflag.models.Country
import com.iut.geoflag.models.Game

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        game = intent.getSerializableExtra("game") as Game

        updateQuestion()
    }

    fun submitAnswer(response : Country) {
        if (game.submitAnswer(response)) {
            val text = "Score: ${game.getScore()}"
            binding.score.text = text
        }
        updateQuestion()
    }

    private fun updateQuestion() {

        val question = game.getCurrentQuestion()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(binding.questionFragment.id, QuestionFragment(question))
        transaction.commit()
    }

}