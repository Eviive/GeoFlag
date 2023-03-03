package com.iut.geoflag.utils

class StringUtils {

    companion object {

        fun levenshteinDistance(s: String, t: String): Int {
            val m = s.length
            val n = t.length
            val d = Array(m + 1) { IntArray(n + 1) }

            for (i in 0..m) {
                d[i][0] = i
            }

            for (j in 0..n) {
                d[0][j] = j
            }

            for (j in 1..n) {
                for (i in 1..m) {
                    val substitutionCost = if (s[i - 1] == t[j - 1]) 0 else 1
                    d[i][j] = minOf(
                        d[i - 1][j] + 1, // deletion
                        d[i][j - 1] + 1, // insertion
                        d[i - 1][j - 1] + substitutionCost // substitution
                    )
                }
            }

            return d[m][n]
        }

    }

}