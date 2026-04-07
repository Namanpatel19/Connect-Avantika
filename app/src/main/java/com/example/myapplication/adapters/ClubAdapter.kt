package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemClubBinding
import com.example.myapplication.data.Club
import com.example.myapplication.data.ClubRequest

class ClubAdapter(
    private var clubs: List<Club>,
    private var pendingRequests: List<ClubRequest> = emptyList(),
    private var joinedClubIds: Set<String> = emptySet(),
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

        // Load Club Logo/Banner
        if (!club.bannerUrl.isNullOrEmpty()) {
            Glide.with(ctx)
                .load(club.bannerUrl)
                .placeholder(R.drawable.ic_clubs)
                .error(R.drawable.ic_clubs)
                .into(b.clubLogo)
        } else {
            b.clubLogo.setImageResource(R.drawable.ic_clubs)
        }

        // Check request status
        val myRequest = pendingRequests.find { it.clubId == club.id }
        val status = myRequest?.status

        // Join button logic
        if (isStudentRole) {
            b.btnJoin.visibility = View.VISIBLE
            b.btnJoin.isEnabled = true // Default
            
            when (status) {
                "accepted" -> {
                    b.btnJoin.text = "Joined"
                    b.btnJoin.setIconResource(R.drawable.ic_check)
                    b.btnJoin.backgroundTintList = ContextCompat.getColorStateList(ctx, R.color.separator)
                    b.btnJoin.setTextColor(ContextCompat.getColor(ctx, R.color.text_secondary))
                    b.btnJoin.iconTint = ContextCompat.getColorStateList(ctx, R.color.text_secondary)
                    b.btnJoin.isEnabled = false
                }
                "pending" -> {
                    b.btnJoin.text = "Pending"
                    b.btnJoin.setIconResource(R.drawable.ic_clubs)
                    b.btnJoin.backgroundTintList = ContextCompat.getColorStateList(ctx, R.color.badge_intermediate_bg)
                    b.btnJoin.setTextColor(ContextCompat.getColor(ctx, R.color.badge_intermediate_text))
                    b.btnJoin.iconTint = ContextCompat.getColorStateList(ctx, R.color.badge_intermediate_text)
                    b.btnJoin.isEnabled = false
                }
                "interview" -> {
                    b.btnJoin.text = "Interview"
                    b.btnJoin.setIconResource(R.drawable.ic_calendar)
                    b.btnJoin.backgroundTintList = ContextCompat.getColorStateList(ctx, R.color.primary_light)
                    b.btnJoin.setTextColor(ContextCompat.getColor(ctx, R.color.primary))
                    b.btnJoin.iconTint = ContextCompat.getColorStateList(ctx, R.color.primary)
                    b.btnJoin.isEnabled = false
                }
                "rejected" -> {
                    b.btnJoin.text = "Re-apply"
                    b.btnJoin.setIconResource(R.drawable.ic_clubs)
                    b.btnJoin.backgroundTintList = ContextCompat.getColorStateList(ctx, R.color.accent_rose)
                    b.btnJoin.setTextColor(ContextCompat.getColor(ctx, android.R.color.white))
                    b.btnJoin.iconTint = ContextCompat.getColorStateList(ctx, android.R.color.white)
                    b.btnJoin.setOnClickListener { onJoinClick(club) }
                }
                else -> {
                    b.btnJoin.text = "Apply"
                    b.btnJoin.setIconResource(R.drawable.ic_clubs)
                    b.btnJoin.backgroundTintList = ContextCompat.getColorStateList(ctx, R.color.secondary)
                    b.btnJoin.setTextColor(ContextCompat.getColor(ctx, android.R.color.white))
                    b.btnJoin.iconTint = ContextCompat.getColorStateList(ctx, android.R.color.white)
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

    fun updateData(newClubs: List<Club>, newRequests: List<ClubRequest>, newJoinedIds: Set<String>) {
        clubs = newClubs
        pendingRequests = newRequests
        joinedClubIds = newJoinedIds
        notifyDataSetChanged()
    }
}
