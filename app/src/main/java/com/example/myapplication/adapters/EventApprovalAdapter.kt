package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.Event
import com.example.myapplication.databinding.ItemEventApprovalBinding

class EventApprovalAdapter(
    private val events: List<Event>,
    private val onApprove: (Event) -> Unit,
    private val onReject:  (Event) -> Unit
) : RecyclerView.Adapter<EventApprovalAdapter.VH>() {

    inner class VH(val b: ItemEventApprovalBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemEventApprovalBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = events.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val e = events[position]
        holder.b.tvTitle.text       = e.title
        holder.b.tvDescription.text = e.description ?: ""
        holder.b.tvDate.text        = e.eventDate?.take(10) ?: "No date"
        holder.b.tvStatus.text      = e.status.uppercase()
        holder.b.btnApprove.setOnClickListener { onApprove(e) }
        holder.b.btnReject.setOnClickListener  { onReject(e) }
    }
}
