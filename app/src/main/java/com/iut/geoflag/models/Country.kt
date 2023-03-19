package com.iut.geoflag.models

import java.io.Serializable
import java.util.Objects.hash

data class Country(
    private val name: Name,
    val cca2: String,
    val cca3: String,
    val ccn3: String,
    val capital: List<String>?,
    val region: String,
    val subregion: String?,
    val languages: Map<String, String>?,
    private val translations: Map<String, Translation>,
    val latlng: List<Double>,
    val area: Double,
    val population: Int,
    val timezones: List<String>,
    val continents: List<String>,
    val flags: Map<String, String>,
    val capitalInfo: CapitalInfo
): Serializable {

    companion object {
        // the CCA3 code of the language to use
        private var language : String? = null

        fun setLanguage(language: String) {
            Companion.language = language.lowercase()
        }
    }

    data class Name(
        val common: String,
        val official: String
    ): Serializable

    data class Translation(
        val common: String,
        val official: String
    ): Serializable {
        fun getName(): Name {
            return Name(common, official)
        }
    }

    data class CapitalInfo(
        val latlng: List<Double>?
    ): Serializable

    fun getName(): Name {

        if (language == null) {
            return name
        }

        translations[language]?.let {
            return it.getName()
        }

        return name
    }

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