package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.Announcement
import com.example.myapplication.databinding.ItemAnnouncementBinding

class AnnouncementAdapter(
    private val items: List<Announcement>,
    private val onDelete: ((Announcement) -> Unit)? = null
) : RecyclerView.Adapter<AnnouncementAdapter.VH>() {

    inner class VH(val b: ItemAnnouncementBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemAnnouncementBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.b.tvTitle.text   = item.title
        holder.b.tvContent.text = item.content ?: ""
        holder.b.tvDate.text    = item.createdAt?.take(10) ?: ""
        if (onDelete != null) {
            holder.b.btnDelete.visibility = View.VISIBLE
            holder.b.btnDelete.setOnClickListener { onDelete.invoke(item) }
        } else {
            holder.b.btnDelete.visibility = View.GONE
        }
    }
}
