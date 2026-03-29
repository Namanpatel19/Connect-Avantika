package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.Event
import com.google.android.material.button.MaterialButton

class EventAdapter(
    private var events: List<Event>,
    private val onRegisterClick: (Event) -> Unit = {}
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
        holder.tvEventDate.text = event.eventDate ?: "Date TBA"
        holder.tvEventLocation.text = "Campus"
        holder.tvEventOrganizer.text = event.clubId ?: "University"

        // Determine level badge from description keywords
        val desc = (event.description ?: "").lowercase()
        val (levelText, levelBg) = when {
            desc.contains("advanced") || desc.contains("expert") ->
                Pair("Advanced", R.drawable.bg_badge_beginner)
            desc.contains("intermediate") || desc.contains("mid") ->
                Pair("Intermediate", R.drawable.bg_badge_beginner)
            else ->
                Pair("Beginner", R.drawable.bg_badge_beginner)
        }
        holder.tvEventLevel.text = levelText
        holder.tvEventLevel.setBackgroundResource(levelBg)

        // Points chip
        holder.tvEventPoints.text = "100 pts"
        holder.tvEventPoints.visibility = View.VISIBLE

        holder.btnRegister.setOnClickListener { onRegisterClick(event) }
    }

    fun updateEvents(newEvents: List<Event>) {
        this.events = newEvents
        notifyDataSetChanged()
    }

    override fun getItemCount() = events.size
}
