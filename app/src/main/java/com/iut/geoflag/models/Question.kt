package com.iut.geoflag.models

data class Question (
    val number: Int,
    val question: Country,
    val possibilities: List<Country>,
) {
    fun isCorrect(answer: Country): Boolean {
        return answer == question
    }
}