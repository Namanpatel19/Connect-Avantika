package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemClubBinding
import com.example.myapplication.data.Club
import com.example.myapplication.data.ClubRequest

class ClubAdapter(
    private var clubs: List<Club>,
    private var pendingRequests: List<ClubRequest> = emptyList(),
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

        b.clubName.text = club.name ?: "Unnamed Club"
        b.clubDescription.text = club.description ?: "No description available."
        b.tvClubCategory.text = club.category ?: "Other"

        // Check if joined or pending
        val isJoined = joinedClubIds.contains(club.id)
        val isPending = pendingRequests.any { it.clubId == club.id && it.status == "pending" }

        // Join button logic
        if (isStudentRole) {
            b.btnJoin.visibility = View.VISIBLE
            when {
                isJoined -> {
                    b.btnJoin.text = "Joined"
                    b.btnJoin.setIconResource(R.drawable.ic_clubs) // Assuming checkmark icon or similar
                    b.btnJoin.backgroundTintList = ContextCompat.getColorStateList(ctx, R.color.separator)
                    b.btnJoin.setTextColor(ContextCompat.getColor(ctx, R.color.text_secondary))
                    b.btnJoin.iconTint = ContextCompat.getColorStateList(ctx, R.color.text_secondary)
                    b.btnJoin.isEnabled = false
                }
                isPending -> {
                    b.btnJoin.text = "Pending"
                    b.btnJoin.setIconResource(R.drawable.ic_clubs)
                    b.btnJoin.backgroundTintList = ContextCompat.getColorStateList(ctx, R.color.badge_intermediate_bg)
                    b.btnJoin.setTextColor(ContextCompat.getColor(ctx, R.color.badge_intermediate_text))
                    b.btnJoin.iconTint = ContextCompat.getColorStateList(ctx, R.color.badge_intermediate_text)
                    b.btnJoin.isEnabled = false
                }
                else -> {
                    b.btnJoin.text = "" // Icon only as per figma
                    b.btnJoin.setIconResource(R.drawable.ic_clubs)
                    b.btnJoin.backgroundTintList = ContextCompat.getColorStateList(ctx, R.color.secondary)
                    b.btnJoin.setTextColor(ContextCompat.getColor(ctx, android.R.color.white))
                    b.btnJoin.iconTint = ContextCompat.getColorStateList(ctx, android.R.color.white)
                    b.btnJoin.isEnabled = true
                    b.btnJoin.setOnClickListener { onJoinClick(club) }
                }
            }
        } else {
            b.btnJoin.visibility = View.GONE
        }

        // View Club button
        b.btnViewClub.setOnClickListener { onViewInfoClick(club) }
        
        // Delete button for admin
        b.btnDelete.visibility = if (isAdminRole) View.VISIBLE else View.GONE
        b.btnDelete.setOnClickListener { onDeleteClick(club) }
    }

    override fun getItemCount() = clubs.size

    fun updateData(newClubs: List<Club>, newRequests: List<ClubRequest> = pendingRequests) {
        clubs = newClubs
        pendingRequests = newRequests
        notifyDataSetChanged()
    }
}
