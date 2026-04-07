package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.LeaderboardAdapter
import com.example.myapplication.databinding.FragmentRankBinding
import com.example.myapplication.ui.viewmodel.AppViewModel

class RankFragment : Fragment() {
    private var _binding: FragmentRankBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel
    private lateinit var adapter: LeaderboardAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentRankBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        adapter = LeaderboardAdapter(emptyList())
        binding.rvRankings.layoutManager = LinearLayoutManager(context)
        binding.rvRankings.adapter = adapter
        
        vm.leaderboard.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE
            if (data.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
            } else {
                binding.tvEmpty.visibility = View.GONE
                adapter.update(data)
            }
        }

        vm.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        vm.loadLeaderboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
