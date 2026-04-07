package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.EventRegistration
import com.example.myapplication.data.Student

class EventEntriesAdapter(
    private var entries: List<EventRegistration>,
    private val students: List<Student>
) : RecyclerView.Adapter<EventEntriesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvInitial: TextView = view.findViewById(R.id.tvInitial)
        val tvStudentName: TextView = view.findViewById(R.id.tvStudentName)
        val tvEnrollment: TextView = view.findViewById(R.id.tvEnrollment)
        val tvContact: TextView = view.findViewById(R.id.tvContact)
        val tvRegisteredAt: TextView = view.findViewById(R.id.tvRegisteredAt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_registration_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        val student = students.find { it.userId == entry.studentId }

        if (student != null) {
            holder.tvStudentName.text = student.name
            holder.tvEnrollment.text = "Batch: ${student.batch ?: "N/A"} | Dept: ${student.department ?: "N/A"}"
            holder.tvContact.text = "Contact: ${entry.contact ?: student.contact ?: "N/A"}"
            holder.tvInitial.text = student.name.take(1).uppercase()
        } else {
            holder.tvStudentName.text = "Unknown Student"
            holder.tvEnrollment.text = "ID: ${entry.studentId}"
            holder.tvContact.text = "Contact: ${entry.contact ?: "N/A"}"
            holder.tvInitial.text = "?"
        }

        holder.tvRegisteredAt.text = entry.registeredAt?.take(10) ?: "Today"
    }

    override fun getItemCount() = entries.size

    fun updateData(newEntries: List<EventRegistration>) {
        this.entries = newEntries
        notifyDataSetChanged()
    }
}
