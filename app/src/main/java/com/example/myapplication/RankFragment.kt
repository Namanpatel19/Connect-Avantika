package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.StudentAdapter
import com.example.myapplication.databinding.FragmentRankBinding
import com.example.myapplication.ui.viewmodel.AppViewModel

class RankFragment : Fragment() {
    private var _binding: FragmentRankBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentRankBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        binding.rvRankings.layoutManager = LinearLayoutManager(context)
        
        vm.students.observe(viewLifecycleOwner) { students ->
            // In a real app, we'd sort by points/rank
            binding.rvRankings.adapter = StudentAdapter(students) {}
        }

        vm.loadAllStudents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
