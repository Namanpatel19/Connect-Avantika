package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        vm.myClub.observe(viewLifecycleOwner) { club ->
            club?.let {
                binding.tvClubName.text = it.name
                binding.tvClubDesc.text = it.description ?: ""
            }
        }
        vm.clubEvents.observe(viewLifecycleOwner) { events ->
            binding.tvEventsCount.text = events.size.toString()
        }
        vm.clubRequests.observe(viewLifecycleOwner) { reqs ->
            binding.tvMembersCount.text = reqs.count { it.status == "accepted" }.toString()
            binding.tvPendingCount.text = reqs.count { it.status == "pending" }.toString()
        }

        binding.rvUpcomingEvents.layoutManager = LinearLayoutManager(context)
        vm.clubEvents.observe(viewLifecycleOwner) { events ->
            binding.rvUpcomingEvents.adapter = EventAdapter(events.take(3)) {}
        }

        binding.cardCreateEvent.setOnClickListener {
            CreateEventDialog().show(parentFragmentManager, "CreateEvent")
        }
        binding.cardMembers.setOnClickListener {
            (requireActivity() as MainActivity).navigateTo(R.id.navigation_member_requests)
        }

        vm.loadMyClub()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
