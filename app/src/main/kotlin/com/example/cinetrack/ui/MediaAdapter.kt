package com.example.cinetrack.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.cinetrack.R
import com.example.cinetrack.model.MediaItem
import java.util.Locale

class MediaAdapter(
    context: Context,
    private val onFavoriteClicked: (MediaItem) -> Unit
) : BaseAdapter() {
    private val inflater = LayoutInflater.from(context)
    private val items = mutableListOf<MediaItem>()

    fun submitList(media: List<MediaItem>) {
        items.clear()
        items.addAll(media)
        notifyDataSetChanged()
    }

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): MediaItem = items[position]

    override fun getItemId(position: Int): Long = items[position].id

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.item_media, parent, false).also {
            it.tag = ViewHolder(it)
        }
        val holder = view.tag as ViewHolder
        val item = getItem(position)

        holder.nameText.text = item.title
        holder.detailsText.text = String.format(
            Locale.getDefault(),
            "%s • %s • %d • Rating %.1f/5",
            item.type,
            item.genre,
            item.year,
            item.rating
        )
        holder.statusText.text = item.status
        holder.favoriteText.text = if (item.favorite) "★" else "☆"
        holder.favoriteText.setOnClickListener { onFavoriteClicked(item) }
        return view
    }

    private class ViewHolder(view: View) {
        val nameText: TextView = view.findViewById(R.id.nameText)
        val detailsText: TextView = view.findViewById(R.id.detailsText)
        val statusText: TextView = view.findViewById(R.id.statusText)
        val favoriteText: TextView = view.findViewById(R.id.favoriteText)
    }
}
