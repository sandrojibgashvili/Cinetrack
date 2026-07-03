package com.example.cinetrack.model

data class MediaItem(
    var id: Long = 0,
    var title: String,
    var genre: String,
    var year: Int,
    var type: String,
    var status: String,
    var rating: Float,
    var favorite: Boolean
) {
    companion object {
        const val TYPE_MOVIE = "Movie"
        const val TYPE_SERIES = "Series"

        const val STATUS_WANT = "Want to Watch"
        const val STATUS_WATCHING = "Watching"
        const val STATUS_WATCHED = "Watched"
    }
}
