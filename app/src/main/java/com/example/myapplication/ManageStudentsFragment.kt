package com.example.myapplication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.StudentAdapter
import com.example.myapplication.data.Student
import com.example.myapplication.data.User
import com.example.myapplication.databinding.FragmentManageStudentsBinding
import com.example.myapplication.ui.viewmodel.AppViewModel
import java.util.UUID

class ManageStudentsFragment : Fragment() {
    private var _binding: FragmentManageStudentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentManageStudentsBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        binding.rvStudents.layoutManager = LinearLayoutManager(context)

        vm.students.observe(viewLifecycleOwner) { students ->
            binding.tvCount.text = "All Students (${students.size})"
            binding.rvStudents.adapter = StudentAdapter(students, onDelete = { s ->
                showDeleteDialog(s)
            })
        }

        vm.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrBlank()) vm.loadAllStudents() else vm.searchStudents(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        vm.loadAllStudents()
    }

    private fun showDeleteDialog(student: Student) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Student")
            .setMessage("Remove ${student.name} from the system?")
            .setPositiveButton("Delete") { _, _ ->
                vm.deleteStudent(student.userId) { success ->
                    Toast.makeText(context, if (success) "Deleted" else "Error deleting", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
