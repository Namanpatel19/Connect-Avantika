package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.EventEntriesAdapter
import com.example.myapplication.data.Event
import com.example.myapplication.databinding.DialogEventEntriesBinding
import com.example.myapplication.ui.viewmodel.AppViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EventEntriesDialog(private val event: Event) : BottomSheetDialogFragment() {

    private var _binding: DialogEventEntriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        DialogEventEntriesBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        binding.tvEventTitle.text = event.title
        binding.rvEntries.layoutManager = LinearLayoutManager(context)
        binding.btnClose.setOnClickListener { dismiss() }

        vm.eventRegistrations.observe(viewLifecycleOwner) { registrations ->
            binding.tvCount.text = "${registrations.size} registered"
            binding.tvEmpty.visibility = if (registrations.isEmpty()) View.VISIBLE else View.GONE
            
            // We need students data to show names. Students should already be loaded in vm.
            binding.rvEntries.adapter = EventEntriesAdapter(registrations, vm.students.value ?: emptyList())
        }

        vm.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        vm.loadEventEntries(event.id!!)
        if (vm.students.value.isNullOrEmpty()) vm.loadAllStudents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
