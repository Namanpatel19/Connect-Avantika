package com.example.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemClubBinding
import com.example.myapplication.models.Club

class ClubAdapter(
    private var clubs: List<Club>,
    private val onJoinClick: (Club) -> Unit,
    private val onDeleteClick: (Club) -> Unit
) : RecyclerView.Adapter<ClubAdapter.ClubViewHolder>() {

    class ClubViewHolder(val binding: ItemClubBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubViewHolder {
        val binding = ItemClubBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClubViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClubViewHolder, position: Int) {
        val club = clubs[position]
        holder.binding.clubName.text = club.name
        
        holder.binding.btnJoin.setOnClickListener {
            onJoinClick(club)
        }

        holder.binding.btnDelete.setOnClickListener {
            onDeleteClick(club)
        }
    }

    override fun getItemCount() = clubs.size

    fun updateClubs(newClubs: List<Club>) {
        clubs = newClubs
        notifyDataSetChanged()
    }
}