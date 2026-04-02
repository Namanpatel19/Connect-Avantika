package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.AnnouncementAdapter
import com.example.myapplication.databinding.FragmentDeanHomeBinding
import com.example.myapplication.ui.viewmodel.AppViewModel

class DeanHomeFragment : Fragment() {
    private var _binding: FragmentDeanHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentDeanHomeBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        vm.studentCount.observe(viewLifecycleOwner) { binding.tvStudents.text = it.toString() }
        vm.facultyCount.observe(viewLifecycleOwner)  { binding.tvFaculty.text  = it.toString() }
        vm.clubCount.observe(viewLifecycleOwner)     { binding.tvClubs.text    = it.toString() }
        vm.eventCount.observe(viewLifecycleOwner)    { binding.tvEvents.text   = it.toString() }

        binding.rvActivity.layoutManager = LinearLayoutManager(context)
        vm.announcements.observe(viewLifecycleOwner) { list ->
            binding.rvActivity.adapter = AnnouncementAdapter(list.take(5))
        }

        // Quick action cards
        binding.cardManageStudents.setOnClickListener {
            (requireActivity() as MainActivity).navigateTo(R.id.navigation_manage_students_dean)
        }
        binding.cardManageFaculty.setOnClickListener {
            (requireActivity() as MainActivity).navigateTo(R.id.navigation_manage_faculty)
        }
        binding.cardEventApprovals.setOnClickListener {
            (requireActivity() as MainActivity).navigateTo(R.id.navigation_event_approvals)
        }
        binding.cardAnnouncements.setOnClickListener {
            (requireActivity() as MainActivity).navigateTo(R.id.navigation_manage_announcements)
        }

        vm.loadSystemStats()
        vm.loadAnnouncements()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
