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
import com.example.myapplication.databinding.FragmentManageStudentsDeanBinding
import com.example.myapplication.ui.viewmodel.AppViewModel

class ManageEventsFragment : Fragment() {
    private var _binding: FragmentManageStudentsDeanBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentManageStudentsDeanBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        binding.rvStudents.layoutManager = LinearLayoutManager(context)
        vm.events.observe(viewLifecycleOwner) { events ->
            binding.tvCount.text = "All Events (${events.size})"
            binding.rvStudents.adapter = EventAdapter(events) { event ->
                showDeleteDialog(event)
            }
        }

        vm.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        binding.btnAddStudent.visibility = View.VISIBLE
        binding.btnAddStudent.text = "+ Add Event"
        binding.btnAddStudent.setOnClickListener {
            // Reusing CreateEventDialog for adding events
            CreateEventDialog().show(parentFragmentManager, "CreateEvent")
        }

        vm.loadAllEvents()
    }

    private fun showDeleteDialog(event: Event) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Event")
            .setMessage("Remove ${event.title}?")
            .setPositiveButton("Delete") { _, _ ->
                vm.deleteEvent(event.id ?: "") { success ->
                    Toast.makeText(context, if (success) "Deleted" else "Error", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null).show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
