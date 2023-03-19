package com.iut.geoflag.models

import java.io.Serializable
import java.util.Objects.hash

data class Country(
    val name: Name,
    val cca2: String,
    val ccn3: String,
    val capital: List<String>?,
    val region: String,
    val subregion: String?,
    val languages: Map<String, String>?,
    val translations: Map<String, Translation>,
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

    data class Translation(
        val official: String,
        val common: String
    ): Serializable

    data class CapitalInfo(
        val latlng: List<Double>?
    ): Serializable

    override fun hashCode(): Int {
        return hash(ccn3)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (javaClass != other?.javaClass)
            return false

        other as Country
        return ccn3 == other.ccn3
    }
}