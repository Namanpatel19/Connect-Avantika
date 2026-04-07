package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.DialogEventRegistrationBinding
import com.example.myapplication.ui.viewmodel.AppViewModel

class EventRegistrationDialog : DialogFragment() {

    private var _binding: DialogEventRegistrationBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    companion object {
        private const val ARG_EVENT_ID = "event_id"
        private const val ARG_EVENT_TITLE = "event_title"

        fun newInstance(eventId: String, eventTitle: String): EventRegistrationDialog {
            return EventRegistrationDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_EVENT_ID, eventId)
                    putString(ARG_EVENT_TITLE, eventTitle)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = DialogEventRegistrationBinding.inflate(inflater, container, false)
        .also { _binding = it }.root

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        val eventId = arguments?.getString(ARG_EVENT_ID) ?: ""
        val eventTitle = arguments?.getString(ARG_EVENT_TITLE) ?: "Event"

        binding.tvEventName.text = "Register: $eventTitle"

        // Pre-fill from student profile
        vm.currentStudent.observe(viewLifecycleOwner) { student ->
            student ?: return@observe
            binding.etName.setText(student.name)
            binding.etEnrollment.setText(student.enrollment)
            if (!student.contact.isNullOrBlank()) {
                binding.etContact.setText(student.contact)
            }
        }

        // Pre-fill email from ViewModel
        vm.userEmail.observe(viewLifecycleOwner) { email ->
            if (!email.isNullOrBlank()) binding.etEmail.setText(email)
        }

        // Load profile data if not already loaded
        if (vm.currentStudent.value == null) vm.loadCurrentStudent()
        if (vm.userEmail.value == null) vm.loadProfileStats()

        binding.btnCancel.setOnClickListener { dismiss() }

        binding.btnRegister.setOnClickListener {
            val contact = binding.etContact.text.toString().trim()
            if (contact.isEmpty()) {
                Toast.makeText(context, "Please enter your contact number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!binding.cbConfirm.isChecked) {
                Toast.makeText(context, "Please confirm your participation", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnRegister.isEnabled = false
            vm.registerForEvent(eventId, contact) { success ->
                binding.btnRegister.isEnabled = true
                if (success) {
                    Toast.makeText(context, "Successfully registered for $eventTitle!", Toast.LENGTH_SHORT).show()
                    dismiss()
                } else {
                    Toast.makeText(context, "Registration failed. You may already be registered.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
