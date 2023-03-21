package com.iut.geoflag.models

import java.io.Serializable

data class Game(private var settings: Settings) : Serializable {

    private var score: Int = 0
    private var questionNumber: Int = 0
    private var currentQuestion: Question = generateQuestion()
    private var isFinished: Boolean = false

    private fun generateQuestion() : Question {

        questionNumber++
        val country = settings.countries.random()

        val possibilities = ArrayList<Country>()
        possibilities.add(country)

        for (i in 1 until settings.possibilities) {
            lateinit var answer : Country

            do {
                answer = settings.countries.random()
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

    fun submitAnswer(valid: Boolean) {
        if (valid) {
            score++
        }
        currentQuestion = generateQuestion()
    }

    fun getQuestionNumber(): Int {
        return questionNumber
    }

    fun getCurrentQuestion(): Question {
        return currentQuestion
    }

    fun getScore(): Int {
        return score
    }

    fun getTimer(): Long {
        return settings.time
    }

    fun finish() {
        isFinished = true
    }

    fun isFinished(): Boolean {
        return isFinished
    }
}