package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.EventAdapter
import com.example.myapplication.databinding.FragmentClubHomeBinding
import com.example.myapplication.ui.viewmodel.AppViewModel

class ClubHomeFragment : Fragment() {
    private var _binding: FragmentClubHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentClubHomeBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        binding.rvUpcomingEvents.layoutManager = LinearLayoutManager(context)
        val adapter = EventAdapter(emptyList()) { _ ->
            // Handle event click if needed
        }
        binding.rvUpcomingEvents.adapter = adapter

        vm.myClub.observe(viewLifecycleOwner) { club ->
            club?.let {
                binding.tvClubName.text = it.name
                binding.tvClubDesc.text = it.description
            }
        }

        vm.clubEvents.observe(viewLifecycleOwner) { events ->
            adapter.updateEvents(events)
            binding.tvEventsCount.text = events.size.toString()
            // Placeholder for members if not in model
            binding.tvMembersCount.text = "0" 
        }

        vm.clubRequests.observe(viewLifecycleOwner) { requests ->
            binding.tvPendingCount.text = requests.size.toString()
        }

        binding.cardCreateEvent.setOnClickListener {
            CreateEventDialog().show(parentFragmentManager, "CreateEvent")
        }

        binding.cardMembers.setOnClickListener {
            (activity as? MainActivity)?.navigateTo(R.id.navigation_member_requests)
        }

        vm.loadMyClub()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
