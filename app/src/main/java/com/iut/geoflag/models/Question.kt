package com.iut.geoflag.models

import java.io.Serializable

data class Question (
    val number: Int,
    val question: Country,
    val possibilities: ArrayList<Country>,
) : Serializable {

    private var isAnswered: Boolean = false

    fun isCorrect(answer: Country): Boolean {
        return answer == question
    }

    fun isAnswered(): Boolean {
        return isAnswered
    }

    fun setAnswered() {
        isAnswered = true
    }
}