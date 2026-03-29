package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.AnnouncementAdapter
import com.example.myapplication.data.Announcement
import com.example.myapplication.databinding.DialogAnnouncementsBinding
import com.example.myapplication.ui.viewmodel.AppViewModel

class AnnouncementsDeanDialog : DialogFragment() {
    private var _binding: DialogAnnouncementsBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        DialogAnnouncementsBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        binding.rvAnnouncements.layoutManager = LinearLayoutManager(context)
        vm.announcements.observe(viewLifecycleOwner) { list ->
            binding.rvAnnouncements.adapter = AnnouncementAdapter(list, onDelete = { a ->
                vm.deleteAnnouncement(a.id!!) { success ->
                    if (!success) Toast.makeText(context, "Error deleting", Toast.LENGTH_SHORT).show()
                }
            })
        }

        binding.btnCreate.setOnClickListener {
            val title   = binding.etTitle.text.toString().trim()
            val content = binding.etContent.text.toString().trim()
            if (title.isEmpty()) { Toast.makeText(context, "Enter title", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            val ann = Announcement(title = title, content = content, createdBy = vm.userId)
            vm.createAnnouncement(ann) { success ->
                if (success) { binding.etTitle.text?.clear(); binding.etContent.text?.clear() }
                Toast.makeText(context, if (success) "Created!" else "Error", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnClose.setOnClickListener { dismiss() }
        vm.loadAnnouncements()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
