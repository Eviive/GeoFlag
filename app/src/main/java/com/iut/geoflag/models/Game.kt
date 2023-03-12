package com.iut.geoflag.models

import java.io.Serializable

data class Game(private var time: Long, private var countries: ArrayList<Country>) : Serializable {

    private var score: Int = 0
    private var questionNumber: Int = 0
    private var currentQuestion: Question = generateQuestion()

    private fun generateQuestion() : Question {

        questionNumber++
        val country = countries.random()

        val possibilities = ArrayList<Country>()
        possibilities.add(country)

        for (i in 1 until 3) {
            lateinit var answer : Country

            do {
                answer = countries.random()
            } while (possibilities.contains(answer))

            possibilities.add(answer)
        }

        possibilities.shuffle()

        this.currentQuestion = Question(
            questionNumber,
            country,
            possibilities
        )

        return currentQuestion
    }

    fun submitAnswer(answer: Country): Boolean {

        val correct = currentQuestion.isCorrect(answer)
        currentQuestion = generateQuestion()

        if (correct) {
            score++
            return true
        }
        return false
    }

    fun getCurrentQuestion(): Question {
        return currentQuestion
    }

    fun getScore(): Int {
        return score
    }
}