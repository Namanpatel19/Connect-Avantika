package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemClubBinding
import com.example.myapplication.data.Club

class ClubAdapter(
    private var clubs: List<Club>,
    private val joinedClubIds: Set<String> = emptySet(),
    private val onDeleteClick: (Club) -> Unit = {},
    private val onJoinClick: (Club) -> Unit = {}
) : RecyclerView.Adapter<ClubAdapter.ClubViewHolder>() {

    class ClubViewHolder(val binding: ItemClubBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubViewHolder {
        val binding = ItemClubBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClubViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClubViewHolder, position: Int) {
        val club = clubs[position]
        val b = holder.binding

        b.clubName.text = club.name
        b.clubDescription.text = club.description ?: ""

        // Member count placeholder (real count needs join query)
        b.clubMembers.text = "Members"

        // Category chip from description (first word or custom field)
        val category = when {
            club.description?.contains("Tech", ignoreCase = true) == true -> "Technology"
            club.description?.contains("Sport", ignoreCase = true) == true -> "Sports"
            club.description?.contains("Art", ignoreCase = true) == true -> "Arts & Culture"
            club.description?.contains("Music", ignoreCase = true) == true -> "Music"
            club.description?.contains("Business", ignoreCase = true) == true -> "Business"
            else -> "General"
        }
        b.tvClubCategory.text = category

        // Joined chip visibility
        val isJoined = joinedClubIds.contains(club.id)
        b.tvJoinedChip.visibility = if (isJoined) android.view.View.VISIBLE else android.view.View.GONE
        b.btnJoin.text = if (isJoined) "Joined ✓" else "Join Club"
        if (isJoined) {
            b.btnJoin.backgroundTintList = androidx.core.content.ContextCompat.getColorStateList(
                holder.itemView.context, com.example.myapplication.R.color.separator
            )
            b.btnJoin.setTextColor(
                androidx.core.content.ContextCompat.getColor(
                    holder.itemView.context, com.example.myapplication.R.color.text_secondary
                )
            )
        } else {
            b.btnJoin.backgroundTintList = androidx.core.content.ContextCompat.getColorStateList(
                holder.itemView.context, com.example.myapplication.R.color.secondary
            )
            b.btnJoin.setTextColor(
                androidx.core.content.ContextCompat.getColor(
                    holder.itemView.context, android.R.color.white
                )
            )
        }

        b.btnJoin.setOnClickListener { onJoinClick(club) }
        b.btnDelete.setOnClickListener { onDeleteClick(club) }
        b.btnViewClub.setOnClickListener { /* navigate to club detail */ }
    }

    override fun getItemCount() = clubs.size

    fun updateClubs(newClubs: List<Club>) {
        clubs = newClubs
        notifyDataSetChanged()
    }
}
