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
    private val onRegisterClick: (Event) -> Unit = {}
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivEventBanner: ImageView = view.findViewById(R.id.iv_event_banner)
        val tvEventTitle: TextView = view.findViewById(R.id.tv_event_title)
        val tvEventLevel: TextView = view.findViewById(R.id.tv_event_level)
        val tvEventPoints: TextView = view.findViewById(R.id.tv_event_points)
        val tvEventDate: TextView = view.findViewById(R.id.tv_event_date)
        val tvEventLocation: TextView = view.findViewById(R.id.tv_event_location)
        val tvEventOrganizer: TextView = view.findViewById(R.id.tv_event_organizer)
        val btnRegister: MaterialButton = view.findViewById(R.id.btn_register)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event_bubble, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.tvEventTitle.text = event.title
        holder.tvEventDate.text = event.eventDate?.replace("T", " ") ?: "Date TBA"
        holder.tvEventLocation.text = "Campus"
        holder.tvEventOrganizer.text = event.clubId ?: "University"

        // Load Banner Image
        if (!event.bannerUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(event.bannerUrl)
                .placeholder(R.drawable.bg_card)
                .centerCrop()
                .into(holder.ivEventBanner)
        } else {
            holder.ivEventBanner.setImageResource(R.drawable.bg_card)
        }

        // Determine level badge
        val desc = (event.description ?: "").lowercase()
        val (levelText, levelBg) = when {
            desc.contains("advanced") || desc.contains("expert") -> Pair("Advanced", R.drawable.bg_badge_beginner)
            desc.contains("intermediate") || desc.contains("mid") -> Pair("Intermediate", R.drawable.bg_badge_beginner)
            else -> Pair("Beginner", R.drawable.bg_badge_beginner)
        }
        holder.tvEventLevel.text = levelText
        holder.tvEventLevel.setBackgroundResource(levelBg)

        // Registered state
        if (event.isRegistered) {
            holder.btnRegister.text = "Registered !!"
            holder.btnRegister.isEnabled = false
            holder.btnRegister.alpha = 0.7f
        } else {
            holder.btnRegister.text = "Register Now"
            holder.btnRegister.isEnabled = true
            holder.btnRegister.alpha = 1.0f
        }

        holder.btnRegister.setOnClickListener { onRegisterClick(event) }
    }

    fun updateEvents(newEvents: List<Event>) {
        this.events = newEvents
        notifyDataSetChanged()
    }

    override fun getItemCount() = events.size
}
