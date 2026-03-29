package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.ClubRequest
import com.example.myapplication.databinding.ItemMemberRequestBinding

class ClubRequestAdapter(
    private val requests: List<ClubRequest>,
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
        holder.b.tvStudentId.text = "Student: ${req.studentId.take(8)}…"
        holder.b.tvStatus.text    = req.status
        holder.b.btnAccept.setOnClickListener    { onAccept(req) }
        holder.b.btnReject.setOnClickListener    { onReject(req) }
        holder.b.btnInterview.setOnClickListener { onInterview(req) }
    }
}
