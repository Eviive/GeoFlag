package com.iut.geoflag.models

import java.io.Serializable

data class Country(
    val name: Name,
    val capital: List<String>,
    val region: String,
    val subregion: String,
    val languages: Map<String, String>,
    val latlng: List<Double>,
    val area: Double,
    val population: Int,
    val timezones: List<String>,
    val continents: List<String>,
    val flags: Map<String, String>,
    val capitalInfo: CapitalInfo
): Serializable {
    data class Name(
        val common: String,
        val official: String
    ): Serializable

    data class CapitalInfo(
        val latlng: List<Double>
    ): Serializable
}