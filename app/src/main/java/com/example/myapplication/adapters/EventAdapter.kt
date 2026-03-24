package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemEventBubbleBinding
import com.example.myapplication.models.Event
import com.bumptech.glide.Glide
import com.example.myapplication.R

class EventAdapter(
    private var events: List<Event>,
    private val onRegisterClick: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(val binding: ItemEventBubbleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBubbleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.binding.eventTitle.text = event.title
        holder.binding.eventDescription.text = event.description
        holder.binding.eventDate.text = "${event.date} • ${event.location}"
        
        // Use placeholder or actual image
        if (!event.coverImageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(event.coverImageUrl)
                .into(holder.binding.eventImage)
        } else {
            holder.binding.eventImage.setImageResource(R.drawable.movie)
        }

        holder.binding.btnRegister.setOnClickListener {
            onRegisterClick(event)
        }
    }

    override fun getItemCount() = events.size

    fun updateEvents(newEvents: List<Event>) {
        events = newEvents
        notifyDataSetChanged()
    }
}