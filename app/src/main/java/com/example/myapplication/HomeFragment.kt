package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.AnnouncementAdapter
import com.example.myapplication.adapters.EventAdapter
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.ui.viewmodel.AppViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AppViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        setupClickListeners()
        setupRecyclerViews()
        observeViewModel()

        // Load data
        viewModel.loadCurrentStudent()
        viewModel.loadApprovedEvents()
        viewModel.loadAnnouncements()
        viewModel.loadNotifications()
    }

    private fun setupClickListeners() {
        binding.btnNotifications.setOnClickListener {
            (activity as? MainActivity)?.navigateTo(R.id.navigation_notifications)
        }

        binding.qaEvents.setOnClickListener {
            (activity as? MainActivity)?.navigateTo(R.id.navigation_events)
        }

        binding.qaClubs.setOnClickListener {
            (activity as? MainActivity)?.navigateTo(R.id.navigation_clubs)
        }

        // Updated academics button to show selection dialog
        binding.qaAcademics.setOnClickListener {
            AcademicsDialog().show(parentFragmentManager, "AcademicsDialog")
        }

        binding.tvSeeAllEvents.setOnClickListener {
            (activity as? MainActivity)?.navigateTo(R.id.navigation_events)
        }
    }

    private fun setupRecyclerViews() {
        binding.rvUpcomingEvents.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvAnnouncements.layoutManager = LinearLayoutManager(context)
    }

    private fun observeViewModel() {
        viewModel.currentStudent.observe(viewLifecycleOwner) { student ->
            student?.let {
                binding.tvGreeting.text = "Hi, ${it.name.split(" ")[0]}! 👋"
            }
        }

        viewModel.events.observe(viewLifecycleOwner) { events ->
            binding.rvUpcomingEvents.adapter = EventAdapter(events.take(5)) { event ->
                // Handle event click
            }
        }

        viewModel.announcements.observe(viewLifecycleOwner) { announcements ->
            binding.rvAnnouncements.adapter = AnnouncementAdapter(announcements.take(3))
        }

        viewModel.unreadNotificationsCount.observe(viewLifecycleOwner) { count ->
            if (count > 0) {
                binding.tvNotificationBadge.text = count.toString()
                binding.tvNotificationBadge.visibility = View.VISIBLE
            } else {
                binding.tvNotificationBadge.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
