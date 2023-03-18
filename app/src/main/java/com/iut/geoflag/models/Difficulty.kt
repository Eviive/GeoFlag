package com.iut.geoflag.models

enum class Difficulty(var difficultyName: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard"),
    ANY("Any");

    override fun toString(): String {
        return difficultyName
    }
}