package com.iut.geoflag.models

import java.io.Serializable

data class Settings(
    var countries: ArrayList<Country>,
    var time: Long,
    var possibilities: Int,
    var difficulty: Difficulty
): Serializable {

    companion object {
        fun countriesFilter(countries : ArrayList<Country>,difficulty: Difficulty): ArrayList<Country> {

            countries.sortBy { it.area }
            val filteredCountries = ArrayList<Country>()

            when (difficulty) {
                Difficulty.HARD -> {
                    for (i in 0 until countries.size / 3) {
                        filteredCountries.add(countries[i])
                    }
                }
                Difficulty.MEDIUM -> {
                    for (i in countries.size / 3 until countries.size / 3 * 2) {
                        filteredCountries.add(countries[i])
                    }
                }
                Difficulty.EASY -> {
                    for (i in countries.size / 3 * 2 until countries.size) {
                        filteredCountries.add(countries[i])
                    }
                }
                else -> {
                    return countries
                }
            }
            return filteredCountries
        }
    }

    init {
        countries = countriesFilter(countries, difficulty)
    }
}
