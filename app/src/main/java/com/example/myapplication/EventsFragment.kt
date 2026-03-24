package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.EventAdapter
import com.example.myapplication.databinding.FragmentEventsBinding
import com.example.myapplication.models.Event
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class EventsFragment : Fragment() {

    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadEvents()
    }

    private fun setupRecyclerView() {
        adapter = EventAdapter(emptyList()) { event ->
            registerForEvent(event.eventId)
        }
        binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvents.adapter = adapter
    }

    private fun loadEvents() {
        binding.progressBar.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val events = SupabaseClient.client.postgrest["events"]
                    .select()
                    .decodeList<Event>()
                
                adapter.updateEvents(events)
            } catch (e: Exception) {
                Log.e("EventsFragment", "Error loading events", e)
                Toast.makeText(requireContext(), "Error loading events", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun registerForEvent(eventId: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // To register, you would insert into the event_registrations table
                // val registration = Registration(event_id = eventId, student_id = currentStudentId)
                // SupabaseClient.client.postgrest["event_registrations"].insert(registration)
                
                Toast.makeText(requireContext(), "Registering for event...", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("EventsFragment", "Registration failed", e)
                Toast.makeText(requireContext(), "Registration failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}