package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.EventApprovalAdapter
import com.example.myapplication.databinding.FragmentEventApprovalsBinding
import com.example.myapplication.ui.viewmodel.AppViewModel

class EventApprovalsFragment : Fragment() {
    private var _binding: FragmentEventApprovalsBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentEventApprovalsBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        binding.rvEvents.layoutManager = LinearLayoutManager(context)
        vm.pendingEvents.observe(viewLifecycleOwner) { events ->
            binding.tvPending.text = "${events.size} events pending approval"
            binding.tvEmpty.visibility = if (events.isEmpty()) View.VISIBLE else View.GONE
            binding.rvEvents.adapter = EventApprovalAdapter(events,
                onApprove = { event ->
                    vm.updateEventStatus(event.id!!, "approved") { success ->
                        Toast.makeText(context, if (success) "Event approved!" else "Error", Toast.LENGTH_SHORT).show()
                    }
                },
                onReject = { event ->
                    vm.updateEventStatus(event.id!!, "rejected") { success ->
                        Toast.makeText(context, if (success) "Event rejected" else "Error", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
        vm.loadPendingEvents()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
