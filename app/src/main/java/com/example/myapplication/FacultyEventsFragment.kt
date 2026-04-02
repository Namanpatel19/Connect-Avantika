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
import com.example.myapplication.databinding.FragmentEventsBinding
import com.example.myapplication.ui.viewmodel.AppViewModel

class FacultyEventsFragment : Fragment() {
    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentEventsBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        binding.rvEvents.layoutManager = LinearLayoutManager(context)

        vm.events.observe(viewLifecycleOwner) { events ->
            if (events.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvEvents.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.rvEvents.visibility = View.VISIBLE
                // Faculty can view events but register button shows info only
                binding.rvEvents.adapter = EventAdapter(events) { event ->
                    Toast.makeText(context, "Event: ${event.title}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        vm.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        vm.loadApprovedEvents()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
