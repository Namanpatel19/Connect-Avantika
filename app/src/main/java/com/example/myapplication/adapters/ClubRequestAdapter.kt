package com.example.myapplication.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.ClubRequest
import com.example.myapplication.data.Student
import com.example.myapplication.databinding.ItemMemberRequestBinding

class ClubRequestAdapter(
    private val requests: List<ClubRequest>,
    private val studentMap: Map<String, Student> = emptyMap(),
    private val onAccept: (ClubRequest) -> Unit,
    private val onReject: (ClubRequest) -> Unit,
    private val onInterview: (ClubRequest) -> Unit
) : RecyclerView.Adapter<ClubRequestAdapter.VH>() {

    inner class VH(val b: ItemMemberRequestBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemMemberRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = requests.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val req = requests[position]
        with(holder.b) {

            // Student info — resolve from map if available, else show short ID
            val student = studentMap[req.studentId]
            tvStudentId.text = student?.name ?: "Student ${req.studentId.take(8)}…"
            tvEnrollment.text = student?.let { "Enr: ${it.enrollment}  •  ${it.department ?: ""}" }
                ?: "ID: ${req.studentId.take(12)}"

            // Status chip
            val (statusLabel, chipBg) = when (req.status) {
                "pending"   -> Pair("Pending",   Color.parseColor("#FFF59D"))   // amber
                "interview" -> Pair("Interview", Color.parseColor("#81D4FA"))   // blue
                "accepted"  -> Pair("Accepted",  Color.parseColor("#A5D6A7"))   // green
                "rejected"  -> Pair("Rejected",  Color.parseColor("#EF9A9A"))   // red
                else        -> Pair(req.status,  Color.parseColor("#E0E0E0"))
            }
            chipStatus.text = statusLabel
            chipStatus.chipBackgroundColor =
                android.content.res.ColorStateList.valueOf(chipBg)

            // Interview date row
            if (!req.interviewDate.isNullOrEmpty()) {
                layoutInterview.visibility = View.VISIBLE
                tvInterviewDate.text = "Interview: ${req.interviewDate} at ${req.interviewTime ?: "TBD"}"
            } else {
                layoutInterview.visibility = View.GONE
            }

            // Buttons
            btnAccept.setOnClickListener    { onAccept(req) }
            btnReject.setOnClickListener    { onReject(req) }
            btnInterview.setOnClickListener { onInterview(req) }
        }
    }
}
