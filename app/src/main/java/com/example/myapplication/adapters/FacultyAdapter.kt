package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.Faculty
import com.google.android.material.button.MaterialButton

class FacultyAdapter(
    private val facultyList: List<Faculty>,
    private val onDelete: (Faculty) -> Unit
) : RecyclerView.Adapter<FacultyAdapter.FacultyViewHolder>() {

    class FacultyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvInitials: TextView = view.findViewById(R.id.tvInitials)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvDept: TextView = view.findViewById(R.id.tvDept)
        val tvEnrollment: TextView = view.findViewById(R.id.tvEnrollment)
        val btnDelete: MaterialButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacultyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
        return FacultyViewHolder(view)
    }

    override fun onBindViewHolder(holder: FacultyViewHolder, position: Int) {
        val faculty = facultyList[position]
        holder.tvName.text = faculty.name
        holder.tvDept.text = faculty.department ?: "No Department"
        holder.tvEnrollment.text = faculty.contact ?: ""
        
        holder.tvInitials.text = faculty.name.take(1).uppercase()

        holder.btnDelete.setOnClickListener { onDelete(faculty) }
    }

    override fun getItemCount(): Int = facultyList.size
}
