package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.Event
import com.google.android.material.button.MaterialButton

class EventAdapter(
    private var events: List<Event>,
    private val isLeadView: Boolean = false,
    private val onDeleteClick: ((Event) -> Unit)? = null,
    private val onActionClick: (Event) -> Unit = {}
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivEventBanner: ImageView = view.findViewById(R.id.iv_event_banner)
        val tvEventTitle: TextView = view.findViewById(R.id.tv_event_title)
        val tvEventLevel: TextView = view.findViewById(R.id.tv_event_level)
        val tvEventPoints: TextView = view.findViewById(R.id.tv_event_points)
        val tvEventDate: TextView = view.findViewById(R.id.tv_event_date)
        val tvEventTime: TextView = view.findViewById(R.id.tv_event_time)
        val tvEventLocation: TextView = view.findViewById(R.id.tv_event_location)
        val tvEventOrganizer: TextView = view.findViewById(R.id.tv_event_organizer)
        val btnAction: MaterialButton = view.findViewById(R.id.btn_register)
        val btnDelete: MaterialButton = view.findViewById(R.id.btn_delete_event)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event_bubble, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.tvEventTitle.text = event.title
        holder.tvEventDate.text = event.eventDate?.substringBefore("T") ?: "Date TBA"
        holder.tvEventTime.text = event.eventTime ?: "Time TBA"
        holder.tvEventLocation.text = event.venue ?: "Campus"
        holder.tvEventOrganizer.text = event.clubId ?: "University"

        if (!event.bannerUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(event.bannerUrl)
                .placeholder(R.drawable.bg_card)
                .centerCrop()
                .into(holder.ivEventBanner)
        } else {
            holder.ivEventBanner.setImageResource(R.drawable.bg_card)
        }

        val desc = (event.description ?: "").lowercase()
        val (levelText, levelBg) = when {
            desc.contains("advanced") || desc.contains("expert") -> Pair("Advanced", R.drawable.bg_badge_beginner)
            desc.contains("intermediate") || desc.contains("mid") -> Pair("Intermediate", R.drawable.bg_badge_beginner)
            else -> Pair("Beginner", R.drawable.bg_badge_beginner)
        }
        holder.tvEventLevel.text = levelText
        holder.tvEventLevel.setBackgroundResource(levelBg)

        if (isLeadView) {
            holder.btnAction.text = "View Entries"
            holder.btnAction.isEnabled = true
            holder.btnAction.alpha = 1.0f
            holder.btnDelete.visibility = View.VISIBLE
            holder.btnDelete.setOnClickListener { onDeleteClick?.invoke(event) }
        } else {
            holder.btnDelete.visibility = View.GONE
            if (event.isRegistered) {
                holder.btnAction.text = "Registered !!"
                holder.btnAction.isEnabled = false
                holder.btnAction.alpha = 0.7f
            } else {
                holder.btnAction.text = "Register Now"
                holder.btnAction.isEnabled = true
                holder.btnAction.alpha = 1.0f
            }
        }

        holder.btnAction.setOnClickListener { onActionClick(event) }
    }

    fun updateEvents(newEvents: List<Event>) {
        this.events = newEvents
        notifyDataSetChanged()
    }

    override fun getItemCount() = events.size
}
