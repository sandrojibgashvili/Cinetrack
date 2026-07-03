package com.example.cinetrack.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.cinetrack.model.MediaItem

class CineTrackDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_MEDIA (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITLE TEXT NOT NULL,
                $COL_GENRE TEXT NOT NULL,
                $COL_YEAR INTEGER NOT NULL,
                $COL_TYPE TEXT NOT NULL,
                $COL_STATUS TEXT NOT NULL,
                $COL_RATING REAL NOT NULL,
                $COL_FAVORITE INTEGER NOT NULL
            )
            """.trimIndent()
        )
        seed(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MEDIA")
        onCreate(db)
    }

    fun getAllMedia(): List<MediaItem> {
        val media = mutableListOf<MediaItem>()
        val cursor = readableDatabase.query(TABLE_MEDIA, null, null, null, null, null, "$COL_TITLE ASC")
        cursor.use {
            while (it.moveToNext()) {
                media.add(it.toMediaItem())
            }
        }
        return media
    }

    fun insertMedia(item: MediaItem): Long {
        return writableDatabase.insert(TABLE_MEDIA, null, item.toValues())
    }

    fun updateMedia(item: MediaItem) {
        writableDatabase.update(TABLE_MEDIA, item.toValues(), "$COL_ID = ?", arrayOf(item.id.toString()))
    }

    fun deleteMedia(id: Long) {
        writableDatabase.delete(TABLE_MEDIA, "$COL_ID = ?", arrayOf(id.toString()))
    }

    private fun MediaItem.toValues(): ContentValues {
        return ContentValues().apply {
            put(COL_TITLE, title)
            put(COL_GENRE, genre)
            put(COL_YEAR, year)
            put(COL_TYPE, type)
            put(COL_STATUS, status)
            put(COL_RATING, rating)
            put(COL_FAVORITE, if (favorite) 1 else 0)
        }
    }

    private fun Cursor.toMediaItem(): MediaItem {
        return MediaItem(
            id = getLong(getColumnIndexOrThrow(COL_ID)),
            title = getString(getColumnIndexOrThrow(COL_TITLE)),
            genre = getString(getColumnIndexOrThrow(COL_GENRE)),
            year = getInt(getColumnIndexOrThrow(COL_YEAR)),
            type = getString(getColumnIndexOrThrow(COL_TYPE)),
            status = getString(getColumnIndexOrThrow(COL_STATUS)),
            rating = getFloat(getColumnIndexOrThrow(COL_RATING)),
            favorite = getInt(getColumnIndexOrThrow(COL_FAVORITE)) == 1
        )
    }

    private fun seed(db: SQLiteDatabase) {
        insertSeed(db, "Inception", "Sci-Fi", 2010, MediaItem.TYPE_MOVIE, MediaItem.STATUS_WATCHED, 5f, true)
        insertSeed(db, "Breaking Bad", "Crime Drama", 2008, MediaItem.TYPE_SERIES, MediaItem.STATUS_WATCHED, 5f, true)
        insertSeed(db, "Dune: Part Two", "Adventure", 2024, MediaItem.TYPE_MOVIE, MediaItem.STATUS_WANT, 0f, false)
        insertSeed(db, "The Bear", "Drama Comedy", 2022, MediaItem.TYPE_SERIES, MediaItem.STATUS_WATCHING, 4.5f, false)
    }

    private fun insertSeed(
        db: SQLiteDatabase,
        title: String,
        genre: String,
        year: Int,
        type: String,
        status: String,
        rating: Float,
        favorite: Boolean
    ) {
        val item = MediaItem(0, title, genre, year, type, status, rating, favorite)
        db.insert(TABLE_MEDIA, null, item.toValues())
    }

    companion object {
        private const val DATABASE_NAME = "cinetrack.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_MEDIA = "media"
        private const val COL_ID = "id"
        private const val COL_TITLE = "title"
        private const val COL_GENRE = "genre"
        private const val COL_YEAR = "year"
        private const val COL_TYPE = "type"
        private const val COL_STATUS = "status"
        private const val COL_RATING = "rating"
        private const val COL_FAVORITE = "favorite"
    }
}
