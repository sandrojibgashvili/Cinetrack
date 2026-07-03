package com.example.cinetrack.model

data class MediaStats(
    val total: Int,
    val movies: Int,
    val series: Int,
    val watched: Int,
    val watching: Int,
    val favorites: Int
)
