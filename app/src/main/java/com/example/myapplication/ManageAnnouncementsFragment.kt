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
import com.example.myapplication.adapters.AnnouncementAdapter
import com.example.myapplication.data.Announcement
import com.example.myapplication.databinding.FragmentManageAnnouncementsBinding
import com.example.myapplication.ui.viewmodel.AppViewModel

class ManageAnnouncementsFragment : Fragment() {
    private var _binding: FragmentManageAnnouncementsBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentManageAnnouncementsBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        binding.rvAnnouncements.layoutManager = LinearLayoutManager(context)

        vm.announcements.observe(viewLifecycleOwner) { announcements ->
            binding.tvCount.text = "All Announcements (${announcements.size})"
            binding.tvEmpty.visibility = if (announcements.isEmpty()) View.VISIBLE else View.GONE
            binding.progressBar.visibility = View.GONE
            binding.rvAnnouncements.adapter = AnnouncementAdapter(announcements, onDelete = { ann ->
                showDeleteDialog(ann)
            })
        }

        vm.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        binding.btnCreate.setOnClickListener { handleCreate() }
        vm.loadAnnouncements()
    }

    private fun handleCreate() {
        val title   = binding.etTitle.text?.toString()?.trim() ?: ""
        val content = binding.etContent.text?.toString()?.trim() ?: ""

        if (title.isEmpty()) {
            binding.tilTitle.error = "Title is required"
            return
        }
        binding.tilTitle.error = null

        val ann = Announcement(title = title, content = content, createdBy = vm.userId)
        vm.createAnnouncement(ann) { success ->
            Toast.makeText(context, if (success) "Announcement posted!" else "Error posting announcement", Toast.LENGTH_SHORT).show()
            if (success) {
                binding.etTitle.text?.clear()
                binding.etContent.text?.clear()
            }
        }
    }

    private fun showDeleteDialog(ann: Announcement) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Announcement")
            .setMessage("Remove \"${ann.title}\"?")
            .setPositiveButton("Delete") { _, _ ->
                vm.deleteAnnouncement(ann.id ?: "") { success ->
                    Toast.makeText(context, if (success) "Deleted" else "Error", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null).show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
