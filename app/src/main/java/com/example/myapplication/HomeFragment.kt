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
import com.example.myapplication.adapters.AnnouncementAdapter
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.ui.viewmodel.AppViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        // Setup recyclers
        binding.rvUpcomingEvents.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvAnnouncements.layoutManager  = LinearLayoutManager(context)

        // Observe
        vm.events.observe(viewLifecycleOwner) { events ->
            binding.rvUpcomingEvents.adapter = EventAdapter(events.take(5)) { event ->
                vm.registerForEvent(event.id ?: "") { success ->
                    Toast.makeText(context, if (success) "Registered!" else "Already registered or error", Toast.LENGTH_SHORT).show()
                }
            }
        }

        vm.announcements.observe(viewLifecycleOwner) { list ->
            binding.rvAnnouncements.adapter = AnnouncementAdapter(list)
        }

        vm.currentStudent.observe(viewLifecycleOwner) { student ->
            student?.let {
                binding.tvGreeting.text = "Hi, ${it.name.split(" ").first()}! 👋"
                binding.tvSubGreeting.text = "${it.department} • ${it.batch}"
            }
        }

        // Quick actions
        binding.cardEvents.setOnClickListener {
            (requireActivity() as MainActivity).navigateTo(R.id.navigation_events)
        }
        binding.cardClubs.setOnClickListener {
            (requireActivity() as MainActivity).navigateTo(R.id.navigation_clubs)
        }

        // Load data
        vm.loadApprovedEvents()
        vm.loadAnnouncements()
        vm.loadCurrentStudent()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
