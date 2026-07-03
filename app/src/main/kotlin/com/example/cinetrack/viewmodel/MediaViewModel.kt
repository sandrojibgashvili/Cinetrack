package com.example.cinetrack.viewmodel

import android.content.Context
import com.example.cinetrack.model.MediaItem
import com.example.cinetrack.model.MediaStats
import com.example.cinetrack.repository.MediaRepository

class MediaViewModel(context: Context) {
    fun interface Observer {
        fun onMediaChanged(visibleMedia: List<MediaItem>, filterName: String, stats: MediaStats)
    }

    private val repository = MediaRepository(context)
    private val observers = mutableListOf<Observer>()
    private var allMedia: List<MediaItem> = emptyList()
    var activeFilter: String = MediaRepository.FILTER_ALL
        private set

    init {
        refresh()
    }

    fun observe(observer: Observer) {
        observers.add(observer)
        notifyObservers()
    }

    fun setFilter(filter: String) {
        activeFilter = filter
        notifyObservers()
    }

    fun save(item: MediaItem) {
        repository.save(item)
        refresh()
    }

    fun delete(item: MediaItem) {
        repository.delete(item)
        refresh()
    }

    fun toggleFavorite(item: MediaItem) {
        item.favorite = !item.favorite
        save(item)
    }

    private fun refresh() {
        allMedia = repository.getMedia()
        notifyObservers()
    }

    private fun notifyObservers() {
        val visible = repository.filter(allMedia, activeFilter)
        val stats = repository.calculateStats(allMedia)
        observers.forEach { it.onMediaChanged(visible, activeFilter, stats) }
    }
}
