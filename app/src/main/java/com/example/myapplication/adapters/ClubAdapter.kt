package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemClubBinding
import com.example.myapplication.data.Club

class ClubAdapter(
    private var clubs: List<Club>,
    private val joinedClubIds: Set<String> = emptySet(),
    private val isStudentRole: Boolean = true,
    private val isAdminRole: Boolean = false,
    private val onDeleteClick: (Club) -> Unit = {},
    private val onJoinClick: (Club) -> Unit = {},
    private val onViewInfoClick: (Club) -> Unit = {}
) : RecyclerView.Adapter<ClubAdapter.ClubViewHolder>() {

    class ClubViewHolder(val binding: ItemClubBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubViewHolder {
        val binding = ItemClubBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClubViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClubViewHolder, position: Int) {
        val club = clubs[position]
        val b = holder.binding
        val ctx = holder.itemView.context

        b.clubName.text = club.name
        b.clubDescription.text = club.description ?: "No description available."
        b.tvClubCategory.text = club.category ?: "General"

        // Joined status
        val isJoined = joinedClubIds.contains(club.id)

        // Join button — only shown for students
        if (isStudentRole) {
            b.btnJoin.visibility = View.VISIBLE
            b.btnJoin.text = if (isJoined) "Joined ✓" else "Join Club"
            if (isJoined) {
                b.btnJoin.backgroundTintList = ContextCompat.getColorStateList(ctx, R.color.separator)
                b.btnJoin.setTextColor(ContextCompat.getColor(ctx, R.color.text_secondary))
                b.btnJoin.isEnabled = false
                b.tvJoinedChip.visibility = View.VISIBLE
            } else {
                b.btnJoin.backgroundTintList = ContextCompat.getColorStateList(ctx, R.color.secondary)
                b.btnJoin.setTextColor(ContextCompat.getColor(ctx, android.R.color.white))
                b.btnJoin.isEnabled = true
                b.tvJoinedChip.visibility = View.GONE
            }
            b.btnJoin.setOnClickListener { onJoinClick(club) }
        } else {
            b.btnJoin.visibility = View.GONE
            b.tvJoinedChip.visibility = View.GONE
        }

        // Delete button — only for super_admin
        b.btnDelete.visibility = if (isAdminRole) View.VISIBLE else View.GONE
        b.btnDelete.setOnClickListener { onDeleteClick(club) }

        b.btnViewClub.setOnClickListener { onViewInfoClick(club) }
    }

    override fun getItemCount() = clubs.size

    fun updateClubs(newClubs: List<Club>) {
        clubs = newClubs
        notifyDataSetChanged()
    }
}
