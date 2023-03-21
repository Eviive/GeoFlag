package com.iut.geoflag.models

import java.io.Serializable

data class Question (
    val number: Int,
    val question: Country,
    val possibilities: ArrayList<Country>,
) : Serializable {

    private var answered: Boolean = false

    fun isCorrect(answer: Country): Boolean {
        return answer == question
    }

    fun isAnswered(): Boolean {
        return answered
    }

    fun setAnswered() {
        answered = true
    }
}