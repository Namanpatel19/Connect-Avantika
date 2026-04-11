package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.EventAdapter
import com.example.myapplication.data.Event
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
            binding.rvEvents.adapter = EventAdapter(
                events, 
                isLeadView = true,
                onDeleteClick = { event -> showDeleteConfirmation(event) },
                onActionClick = { event ->
                    EventEntriesDialog(event).show(parentFragmentManager, "EventEntries")
                }
            )
            binding.tvEmpty.visibility = if (events.isEmpty()) View.VISIBLE else View.GONE
        }
        
        binding.fabCreate.setOnClickListener {
            CreateEventDialog().show(parentFragmentManager, "CreateEvent")
        }
        
        if (vm.myClub.value == null) vm.loadMyClub()
    }

    private fun showDeleteConfirmation(event: Event) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Event")
            .setMessage("Are you sure you want to delete '${event.title}'? This will also remove all registrations.")
            .setPositiveButton("Delete") { _, _ ->
                vm.deleteEvent(event.id ?: "") { success ->
                    if (success) {
                        Toast.makeText(context, "Event deleted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to delete event", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
