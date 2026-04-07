package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.Student
import com.example.myapplication.databinding.ItemStudentBinding

class StudentAdapter(
    private val students: List<Student>,
    private val onDelete: (Student) -> Unit = {}
) : RecyclerView.Adapter<StudentAdapter.VH>() {

    inner class VH(val b: ItemStudentBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = students.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val s = students[position]
        holder.b.tvName.text       = s.name
        holder.b.tvDept.text       = "${s.department ?: "—"} • ${s.batch ?: "—"}"
        holder.b.tvEnrollment.text = s.enrollment
        holder.b.btnDelete.setOnClickListener { onDelete(s) }

        val initials = s.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
        holder.b.tvInitials.text = initials
    }
}
