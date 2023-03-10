package com.iut.geoflag.activities

import android.os.Bundle
import android.view.SurfaceControl.Transaction
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.iut.geoflag.R
import com.iut.geoflag.databinding.ActivityGameBinding
import com.iut.geoflag.fragments.QuestionFragment
import com.iut.geoflag.models.Country
import com.iut.geoflag.models.Question

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private var score = 0
    private var questionNumber = 1

    private lateinit var countries: ArrayList<Country>;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        countries = intent.getSerializableExtra("countries") as ArrayList<Country>

        questionUpdate()
    }

    fun submitAnswer(valid : Boolean) {

        if (valid) {
            score++

            val scoreText = "Score : $score"
            binding.score.text = scoreText
        }

        questionUpdate()
    }

    private fun questionUpdate() {

        val question = generateQuestion()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(binding.questionFragment.id, QuestionFragment(question))
        transaction.commit()
    }

    private fun generateQuestion() : Question {

        val country = countries.random()

        val possibilities = ArrayList<Country>()
        possibilities.add(country)

        for (i in 1..2) {
            lateinit var answer : Country

            do {
                answer = countries.random()
            } while (possibilities.contains(answer))

            possibilities.add(answer)
        }

        return Question(
            questionNumber++,
            country,
            possibilities
        )
    }
}