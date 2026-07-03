package com.example.cinetrack.ui

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.cinetrack.R
import com.example.cinetrack.model.MediaItem
import com.example.cinetrack.model.MediaStats
import com.example.cinetrack.repository.MediaRepository
import com.example.cinetrack.viewmodel.MediaViewModel
import java.util.Locale

class MainActivity : Activity() {
    private lateinit var viewModel: MediaViewModel
    private lateinit var adapter: MediaAdapter
    private lateinit var filterText: TextView
    private lateinit var countText: TextView
    private lateinit var emptyText: TextView
    private var latestStats: MediaStats? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        filterText = findViewById(R.id.filterText)
        countText = findViewById(R.id.countText)
        emptyText = findViewById(R.id.emptyText)
        val addButton: Button = findViewById(R.id.addButton)
        val listView: ListView = findViewById(R.id.mediaList)

        viewModel = MediaViewModel(this)
        adapter = MediaAdapter(this) { item -> viewModel.toggleFavorite(item) }
        listView.adapter = adapter

        addButton.setOnClickListener { showMediaDialog(null) }
        listView.setOnItemClickListener { _, _, position, _ -> showMediaDialog(adapter.getItem(position)) }
        listView.setOnItemLongClickListener { _, _, position, _ ->
            confirmDelete(adapter.getItem(position))
            true
        }

        viewModel.observe { visibleMedia, filterName, stats -> render(visibleMedia, filterName, stats) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(Menu.NONE, MENU_ALL, Menu.NONE, "All")
        menu.add(Menu.NONE, MENU_MOVIES, Menu.NONE, "Movies")
        menu.add(Menu.NONE, MENU_SERIES, Menu.NONE, "Series")
        menu.add(Menu.NONE, MENU_FAVORITES, Menu.NONE, "Favorites")
        menu.add(Menu.NONE, MENU_WATCHING, Menu.NONE, "Watching")
        menu.add(Menu.NONE, MENU_WATCHED, Menu.NONE, "Watched")
        menu.add(Menu.NONE, MENU_STATS, Menu.NONE, "Statistics")
        menu.add(Menu.NONE, MENU_ABOUT, Menu.NONE, "About")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            MENU_ALL -> {
                viewModel.setFilter(MediaRepository.FILTER_ALL)
                true
            }
            MENU_MOVIES -> {
                viewModel.setFilter(MediaItem.TYPE_MOVIE)
                true
            }
            MENU_SERIES -> {
                viewModel.setFilter(MediaItem.TYPE_SERIES)
                true
            }
            MENU_FAVORITES -> {
                viewModel.setFilter(MediaRepository.FILTER_FAVORITES)
                true
            }
            MENU_WATCHING -> {
                viewModel.setFilter(MediaItem.STATUS_WATCHING)
                true
            }
            MENU_WATCHED -> {
                viewModel.setFilter(MediaItem.STATUS_WATCHED)
                true
            }
            MENU_STATS -> {
                showStats()
                true
            }
            MENU_ABOUT -> {
                showAbout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun render(visibleMedia: List<MediaItem>, filterName: String, stats: MediaStats) {
        latestStats = stats
        adapter.submitList(visibleMedia)
        filterText.text = if (filterName == MediaRepository.FILTER_ALL) "All titles" else filterName
        countText.text = String.format(Locale.getDefault(), "%d item(s)", visibleMedia.size)
        emptyText.visibility = if (visibleMedia.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showMediaDialog(original: MediaItem?) {
        val view = layoutInflater.inflate(R.layout.dialog_media, null)
        val titleInput: EditText = view.findViewById(R.id.titleInput)
        val genreInput: EditText = view.findViewById(R.id.genreInput)
        val yearInput: EditText = view.findViewById(R.id.yearInput)
        val ratingInput: EditText = view.findViewById(R.id.ratingInput)
        val typeSpinner: Spinner = view.findViewById(R.id.typeSpinner)
        val statusSpinner: Spinner = view.findViewById(R.id.statusSpinner)
        val favoriteInput: CheckBox = view.findViewById(R.id.favoriteInput)

        val types = arrayOf(MediaItem.TYPE_MOVIE, MediaItem.TYPE_SERIES)
        val statuses = arrayOf(MediaItem.STATUS_WANT, MediaItem.STATUS_WATCHING, MediaItem.STATUS_WATCHED)
        typeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types)
        statusSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statuses)

        original?.let {
            titleInput.setText(it.title)
            genreInput.setText(it.genre)
            yearInput.setText(it.year.toString())
            ratingInput.setText(it.rating.toString())
            typeSpinner.setSelection(if (it.type == MediaItem.TYPE_SERIES) 1 else 0)
            statusSpinner.setSelection(statusIndex(it.status))
            favoriteInput.isChecked = it.favorite
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(if (original == null) "Add title" else "Edit title")
            .setView(view)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val item = buildItem(original, titleInput, genreInput, yearInput, ratingInput, typeSpinner, statusSpinner, favoriteInput)
            if (item != null) {
                viewModel.save(item)
                dialog.dismiss()
            }
        }
    }

    private fun buildItem(
        original: MediaItem?,
        titleInput: EditText,
        genreInput: EditText,
        yearInput: EditText,
        ratingInput: EditText,
        typeSpinner: Spinner,
        statusSpinner: Spinner,
        favoriteInput: CheckBox
    ): MediaItem? {
        val title = titleInput.text.toString().trim()
        val genre = genreInput.text.toString().trim()
        if (title.isEmpty() || genre.isEmpty()) {
            Toast.makeText(this, "Title and genre are required", Toast.LENGTH_SHORT).show()
            return null
        }

        val year = yearInput.text.toString().trim().toIntOrNull()
        val rating = ratingInput.text.toString().trim().toFloatOrNull()
        if (year == null || rating == null) {
            Toast.makeText(this, "Year and rating must be valid numbers", Toast.LENGTH_SHORT).show()
            return null
        }
        if (rating !in 0f..5f) {
            Toast.makeText(this, "Rating must be between 0 and 5", Toast.LENGTH_SHORT).show()
            return null
        }

        return MediaItem(
            id = original?.id ?: 0,
            title = title,
            genre = genre,
            year = year,
            type = typeSpinner.selectedItem.toString(),
            status = statusSpinner.selectedItem.toString(),
            rating = rating,
            favorite = favoriteInput.isChecked
        )
    }

    private fun statusIndex(status: String): Int {
        return when (status) {
            MediaItem.STATUS_WATCHING -> 1
            MediaItem.STATUS_WATCHED -> 2
            else -> 0
        }
    }

    private fun confirmDelete(item: MediaItem) {
        AlertDialog.Builder(this)
            .setTitle("Delete title")
            .setMessage("Delete ${item.title}?")
            .setPositiveButton("Delete") { _, _ -> viewModel.delete(item) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showStats() {
        val stats = latestStats ?: return
        val message = """
            Total: ${stats.total}
            Movies: ${stats.movies}
            Series: ${stats.series}
            Watched: ${stats.watched}
            Watching: ${stats.watching}
            Favorites: ${stats.favorites}
        """.trimIndent()
        AlertDialog.Builder(this)
            .setTitle("Watchlist Statistics")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showAbout() {
        AlertDialog.Builder(this)
            .setTitle("About CineTrack")
            .setMessage("CineTrack is a movie and series watchlist built with Kotlin, MVVM, SQLite database, menu filters and statistics.")
            .setPositiveButton("OK", null)
            .show()
    }

    companion object {
        private const val MENU_ALL = 1
        private const val MENU_MOVIES = 2
        private const val MENU_SERIES = 3
        private const val MENU_FAVORITES = 4
        private const val MENU_WATCHING = 5
        private const val MENU_WATCHED = 6
        private const val MENU_STATS = 7
        private const val MENU_ABOUT = 8
    }
}
