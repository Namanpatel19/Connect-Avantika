package com.example.myapplication

import android.content.Intent
import android.net.Uri
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
            }
        }
        
        vm.unreadNotificationsCount.observe(viewLifecycleOwner) { count ->
            if (count > 0) {
                binding.tvNotificationBadge.visibility = View.VISIBLE
                binding.tvNotificationBadge.text = if (count > 9) "9+" else count.toString()
            } else {
                binding.tvNotificationBadge.visibility = View.GONE
            }
        }

        // Quick actions - Fully functional
        binding.qaEvents.setOnClickListener {
            (requireActivity() as MainActivity).navigateTo(R.id.navigation_events)
        }
        binding.qaClubs.setOnClickListener {
            (requireActivity() as MainActivity).navigateTo(R.id.navigation_clubs)
        }
        binding.qaAcademics.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://moodle.avantika.edu.in/login/index.php"))
            startActivity(intent)
        }
        binding.qaTransport.setOnClickListener {
             Toast.makeText(context, "GPS Tracking coming in next update", Toast.LENGTH_SHORT).show()
        }
        
        binding.btnNotifications.setOnClickListener {
            (requireActivity() as MainActivity).navigateTo(R.id.navigation_notifications)
        }

        binding.tvSeeAllEvents.setOnClickListener {
            (requireActivity() as MainActivity).navigateTo(R.id.navigation_events)
        }

        // Load data
        vm.loadApprovedEvents()
        vm.loadAnnouncements()
        vm.loadCurrentStudent()
        vm.loadNotifications()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
