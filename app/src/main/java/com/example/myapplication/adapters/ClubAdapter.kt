package com.example.myapplication.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
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
        b.tvClubCategory.text = club.category ?: "General"

        // Joined status UI
        val isJoined = joinedClubIds.contains(club.id)
        b.btnJoin.text = if (isJoined) "Joined ✓" else "Join Club"
        
        if (isJoined) {
            b.btnJoin.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.separator)
            b.btnJoin.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.text_secondary))
            b.btnJoin.isEnabled = false
        } else {
            b.btnJoin.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.secondary)
            b.btnJoin.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white))
            b.btnJoin.isEnabled = true
        }

        b.btnJoin.setOnClickListener { onJoinClick(club) }
        b.btnDelete.setOnClickListener { onDeleteClick(club) }
        
        // Fix for "View Info" navigation
        b.btnViewClub.setOnClickListener {
            val bundle = Bundle().apply {
                putString("club_id", club.id)
            }
            holder.itemView.findNavController().navigate(R.id.navigation_club_details, bundle)
        }
    }

    override fun getItemCount() = clubs.size

    fun updateClubs(newClubs: List<Club>) {
        clubs = newClubs
        notifyDataSetChanged()
    }
}
