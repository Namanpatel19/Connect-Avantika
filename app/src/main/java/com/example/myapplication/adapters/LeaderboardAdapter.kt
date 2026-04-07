package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.Student
import com.example.myapplication.data.UserPoint

class LeaderboardAdapter(
    private var data: List<Pair<Student, UserPoint>>
) : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRank: TextView = view.findViewById(R.id.tv_rank)
        val ivAvatar: ImageView = view.findViewById(R.id.iv_avatar)
        val tvName: TextView = view.findViewById(R.id.tv_name)
        val tvPoints: TextView = view.findViewById(R.id.tv_points)
        val tvDept: TextView = view.findViewById(R.id.tv_dept)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (student, point) = data[position]
        
        holder.tvRank.text = "#${position + 1}"
        holder.tvName.text = student.name
        holder.tvPoints.text = "${point.totalPoints} pts"
        holder.tvDept.text = "${student.department} | ${student.batch}"

        Glide.with(holder.itemView.context)
            .load(student.photoUrl)
            .placeholder(R.drawable.ic_person)
            .circleCrop()
            .into(holder.ivAvatar)
            
        // Highlight top 3
        when(position) {
            0 -> holder.tvRank.setTextColor(holder.itemView.context.getColor(R.color.secondary))
            1 -> holder.tvRank.setTextColor(holder.itemView.context.getColor(R.color.primary))
            2 -> holder.tvRank.setTextColor(holder.itemView.context.getColor(R.color.primary_dark))
            else -> holder.tvRank.setTextColor(holder.itemView.context.getColor(R.color.text_secondary))
        }
    }

    override fun getItemCount() = data.size

    fun update(newData: List<Pair<Student, UserPoint>>) {
        data = newData
        notifyDataSetChanged()
    }
}
