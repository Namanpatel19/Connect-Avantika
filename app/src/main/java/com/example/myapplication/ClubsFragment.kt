package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.adapters.ClubAdapter
import com.example.myapplication.data.FirebaseManager
import com.example.myapplication.databinding.FragmentClubsBinding
import kotlinx.coroutines.launch

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
        loadClubs()
    }

    private fun setupRecyclerView() {
        adapter = ClubAdapter(emptyList()) { club ->
            requestToJoinClub(club.id)
        }
        binding.rvClubs.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvClubs.adapter = adapter
    }

    private fun loadClubs() {
        binding.progressBar.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            val clubs = FirebaseManager.getClubs()
            adapter.updateClubs(clubs)
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun requestToJoinClub(clubId: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val success = FirebaseManager.requestToJoinClub(clubId)
            if (success) {
                Toast.makeText(requireContext(), "Join request sent!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to send request.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}