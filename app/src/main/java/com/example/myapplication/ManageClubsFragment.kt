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
import com.example.myapplication.adapters.ClubAdapter
import com.example.myapplication.data.Club
import com.example.myapplication.databinding.FragmentManageStudentsDeanBinding
import com.example.myapplication.ui.viewmodel.AppViewModel

class ManageClubsFragment : Fragment() {
    private var _binding: FragmentManageStudentsDeanBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentManageStudentsDeanBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        binding.rvStudents.layoutManager = LinearLayoutManager(context)
        vm.clubs.observe(viewLifecycleOwner) { clubs ->
            binding.tvCount.text = "All Clubs (${clubs.size})"
            binding.rvStudents.adapter = ClubAdapter(clubs, onDeleteClick = { club ->
                showDeleteDialog(club)
            })
        }

        vm.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        binding.btnAddStudent.text = "+ Add Club"
        vm.loadAllClubs()
    }

    private fun showDeleteDialog(club: Club) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Club")
            .setMessage("Remove ${club.name}?")
            .setPositiveButton("Delete") { _, _ ->
                vm.deleteClub(club.id ?: "") { success ->
                    Toast.makeText(context, if (success) "Deleted" else "Error", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null).show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
