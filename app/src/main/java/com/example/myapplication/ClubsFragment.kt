package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.ClubAdapter
import com.example.myapplication.databinding.FragmentClubsBinding
import com.example.myapplication.models.Club
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import java.util.UUID

class ClubsFragment : Fragment() {

    private var _binding: FragmentClubsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ClubAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClubsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        loadClubs()
    }

    private fun setupRecyclerView() {
        adapter = ClubAdapter(
            clubs = emptyList(),
            onJoinClick = { club -> requestToJoinClub(club.clubId) },
            onDeleteClick = { club -> deleteClub(club) }
        )
        binding.rvClubs.layoutManager = LinearLayoutManager(requireContext())
        binding.rvClubs.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.fabAddClub.setOnClickListener {
            createNewClub()
        }
    }

    private fun loadClubs() {
        binding.progressBar.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val clubs = SupabaseClient.client.postgrest["clubs"]
                    .select()
                    .decodeList<Club>()
                
                adapter.updateClubs(clubs)
            } catch (e: Exception) {
                Log.e("ClubsFragment", "Error loading clubs", e)
                Toast.makeText(requireContext(), "Error loading clubs", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun createNewClub() {
        val newClub = Club(
            clubId = UUID.randomUUID().toString(),
            name = "New Tech Club",
            description = "Innovation & Technology"
        )
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                SupabaseClient.client.postgrest["clubs"].insert(newClub)
                loadClubs()
                Toast.makeText(requireContext(), "Club created!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("ClubsFragment", "Error creating club", e)
            }
        }
    }

    private fun deleteClub(club: Club) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                SupabaseClient.client.postgrest["clubs"].delete {
                    filter {
                        eq("club_id", club.clubId)
                    }
                }
                loadClubs()
                Toast.makeText(requireContext(), "Club deleted", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("ClubsFragment", "Error deleting club", e)
            }
        }
    }

    private fun requestToJoinClub(clubId: String) {
        Toast.makeText(requireContext(), "Joined successfully!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}