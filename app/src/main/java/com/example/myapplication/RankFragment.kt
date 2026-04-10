package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myapplication.adapters.LeaderboardAdapter
import com.example.myapplication.data.Student
import com.example.myapplication.data.UserPoint
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

        // Pass offset 3 because first 3 are in podium
        adapter = LeaderboardAdapter(emptyList(), offset = 3)
        binding.rvRankings.layoutManager = LinearLayoutManager(context)
        binding.rvRankings.adapter = adapter
        
        vm.leaderboard.observe(viewLifecycleOwner) { data ->
            Log.d("RankFragment", "Leaderboard data size: ${data.size}")
            binding.progressBar.visibility = View.GONE
            if (data.isNullOrEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.layoutPodium.visibility = View.GONE
                adapter.update(emptyList())
            } else {
                binding.tvEmpty.visibility = View.GONE
                updatePodium(data)
                
                // Remaining students in list (from rank 4 onwards)
                val listData = if (data.size > 3) data.subList(3, data.size) else emptyList()
                adapter.update(listData)
            }
        }

        vm.isLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) binding.progressBar.visibility = View.VISIBLE
        }

        vm.loadLeaderboard()
    }

    private fun updatePodium(data: List<Pair<Student, UserPoint>>) {
        binding.layoutPodium.visibility = View.VISIBLE
        
        // Rank 1
        if (data.isNotEmpty()) {
            val (s, p) = data[0]
            binding.layoutRank1.visibility = View.VISIBLE
            binding.tvNameRank1.text = s.name
            binding.tvPtsRank1.text = "${p.totalPoints} pts"
            Glide.with(this)
                .load(s.photoUrl)
                .placeholder(R.drawable.ic_person)
                .circleCrop()
                .into(binding.ivRank1)
        } else {
            binding.layoutRank1.visibility = View.INVISIBLE
        }

        // Rank 2
        if (data.size > 1) {
            val (s, p) = data[1]
            binding.layoutRank2.visibility = View.VISIBLE
            binding.tvNameRank2.text = s.name
            binding.tvPtsRank2.text = "${p.totalPoints} pts"
            Glide.with(this)
                .load(s.photoUrl)
                .placeholder(R.drawable.ic_person)
                .circleCrop()
                .into(binding.ivRank2)
        } else {
            binding.layoutRank2.visibility = View.INVISIBLE
        }

        // Rank 3
        if (data.size > 2) {
            val (s, p) = data[2]
            binding.layoutRank3.visibility = View.VISIBLE
            binding.tvNameRank3.text = s.name
            binding.tvPtsRank3.text = "${p.totalPoints} pts"
            Glide.with(this)
                .load(s.photoUrl)
                .placeholder(R.drawable.ic_person)
                .circleCrop()
                .into(binding.ivRank3)
        } else {
            binding.layoutRank3.visibility = View.INVISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
