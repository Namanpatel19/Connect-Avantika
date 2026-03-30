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
import com.example.myapplication.databinding.FragmentManageStudentsDeanBinding
import com.example.myapplication.ui.viewmodel.AppViewModel

class ManageAnnouncementsFragment : Fragment() {
    private var _binding: FragmentManageStudentsDeanBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentManageStudentsDeanBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        binding.rvStudents.layoutManager = LinearLayoutManager(context)
        vm.announcements.observe(viewLifecycleOwner) { announcements ->
            binding.tvCount.text = "All Announcements (${announcements.size})"
            binding.rvStudents.adapter = AnnouncementAdapter(announcements, onDelete = { ann ->
                showDeleteDialog(ann)
            })
        }

        vm.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        binding.btnAddStudent.text = "+ Add Announcement"
        binding.btnAddStudent.setOnClickListener {
            // Show dialog to add announcement if needed
        }
        vm.loadAllAnnouncements()
    }

    private fun showDeleteDialog(ann: Announcement) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Announcement")
            .setMessage("Remove this announcement?")
            .setPositiveButton("Delete") { _, _ ->
                vm.deleteAnnouncement(ann.id ?: "") { success ->
                    Toast.makeText(context, if (success) "Deleted" else "Error", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null).show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
