package com.example.myapplication

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.Event
import com.example.myapplication.databinding.DialogCreateEventBinding
import com.example.myapplication.ui.viewmodel.AppViewModel
import java.util.Calendar

class CreateEventDialog : DialogFragment() {
    private var _binding: DialogCreateEventBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        DialogCreateEventBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        binding.btnPickDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d ->
                binding.tvDate.text = "$y-${(m + 1).toString().padStart(2,'0')}-${d.toString().padStart(2,'0')}"
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.btnSubmit.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val desc  = binding.etDescription.text.toString().trim()
            val date  = binding.tvDate.text.toString()
            
            if (title.isEmpty()) { 
                Toast.makeText(context, "Enter event title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener 
            }

            // If it's a club lead, use their club ID. If it's a dean, it might be a general event (null clubId)
            val clubId = vm.myClub.value?.id 

            val event = Event(
                title       = title,
                description = desc,
                clubId      = clubId,
                createdBy   = vm.userId,
                status      = if (vm.userRole == "dean" || vm.userRole == "super_admin") "approved" else "pending",
                eventDate   = if (date.isNotEmpty()) "${date}T00:00:00" else null
            )
            
            binding.btnSubmit.isEnabled = false
            vm.submitEventRequest(event) { success ->
                binding.btnSubmit.isEnabled = true
                if (success) {
                    Toast.makeText(context, "Event submitted successfully!", Toast.LENGTH_SHORT).show()
                    dismiss()
                } else {
                    Toast.makeText(context, "Failed to create event. Check logs.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnCancel.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
