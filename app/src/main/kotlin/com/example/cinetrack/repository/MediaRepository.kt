package com.example.cinetrack.repository

import android.content.Context
import com.example.cinetrack.data.CineTrackDatabase
import com.example.cinetrack.model.MediaItem
import com.example.cinetrack.model.MediaStats

class MediaRepository(context: Context) {
    private val database = CineTrackDatabase(context.applicationContext)

    fun getMedia(): List<MediaItem> = database.getAllMedia()

    fun save(item: MediaItem) {
        if (item.id == 0L) {
            item.id = database.insertMedia(item)
        } else {
            database.updateMedia(item)
        }
    }

    fun delete(item: MediaItem) {
        database.deleteMedia(item.id)
    }

    fun filter(source: List<MediaItem>, filter: String): List<MediaItem> {
        return source.filter { item ->
            filter == FILTER_ALL ||
                item.type == filter ||
                (filter == FILTER_FAVORITES && item.favorite) ||
                item.status == filter
        }
    }

    fun calculateStats(media: List<MediaItem>): MediaStats {
        return MediaStats(
            total = media.size,
            movies = media.count { it.type == MediaItem.TYPE_MOVIE },
            series = media.count { it.type == MediaItem.TYPE_SERIES },
            watched = media.count { it.status == MediaItem.STATUS_WATCHED },
            watching = media.count { it.status == MediaItem.STATUS_WATCHING },
            favorites = media.count { it.favorite }
        )
    }

    companion object {
        const val FILTER_ALL = "All"
        const val FILTER_FAVORITES = "Favorites"
    }
}
