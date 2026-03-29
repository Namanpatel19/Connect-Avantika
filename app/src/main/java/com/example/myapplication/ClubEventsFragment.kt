package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.EventAdapter
import com.example.myapplication.databinding.FragmentClubEventsBinding
import com.example.myapplication.ui.viewmodel.AppViewModel

class ClubEventsFragment : Fragment() {
    private var _binding: FragmentClubEventsBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentClubEventsBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]
        binding.rvEvents.layoutManager = LinearLayoutManager(context)

        vm.clubEvents.observe(viewLifecycleOwner) { events ->
            binding.rvEvents.adapter = EventAdapter(events) {}
            binding.tvEmpty.visibility = if (events.isEmpty()) View.VISIBLE else View.GONE
        }
        binding.fabCreate.setOnClickListener {
            CreateEventDialog().show(parentFragmentManager, "CreateEvent")
        }
        if (vm.myClub.value == null) vm.loadMyClub()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
