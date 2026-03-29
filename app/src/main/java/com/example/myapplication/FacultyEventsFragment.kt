package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.EventAdapter
import com.example.myapplication.databinding.FragmentFacultyEventsBinding
import com.example.myapplication.ui.viewmodel.AppViewModel

class FacultyEventsFragment : Fragment() {
    private var _binding: FragmentFacultyEventsBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentFacultyEventsBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]
        binding.rvEvents.layoutManager = LinearLayoutManager(context)
        vm.events.observe(viewLifecycleOwner) { events ->
            binding.rvEvents.adapter = EventAdapter(events) {}
            binding.tvEmpty.visibility = if (events.isEmpty()) View.VISIBLE else View.GONE
        }
        vm.loadApprovedEvents()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
