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
import com.example.myapplication.adapters.FacultyAdapter
import com.example.myapplication.data.Faculty
import com.example.myapplication.data.User
import com.example.myapplication.databinding.FragmentManageStudentsDeanBinding
import com.example.myapplication.ui.viewmodel.AppViewModel
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class ManageFacultyFragment : Fragment() {
    private var _binding: FragmentManageStudentsDeanBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentManageStudentsDeanBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        binding.rvStudents.layoutManager = LinearLayoutManager(context)
        
        // We can reuse the same layout, just change the title
        // binding.tvTitle.text = "Manage Faculty" // If we had a title view in XML

        vm.faculty.observe(viewLifecycleOwner) { faculty ->
            binding.tvCount.text = "All Faculty (${faculty.size})"
            binding.rvStudents.adapter = FacultyAdapter(faculty, onDelete = { showDeleteDialog(it) })
        }
        vm.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // To be implemented in VM if needed, for now just load all
                if (s.isNullOrBlank()) vm.loadAllFaculty()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnAddStudent.text = "+ Add Faculty"
        binding.btnAddStudent.setOnClickListener { showAddDialog() }
        vm.loadAllFaculty()
    }

    private fun showDeleteDialog(faculty: Faculty) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Faculty")
            .setMessage("Remove ${faculty.name}?")
            .setPositiveButton("Delete") { _, _ ->
                vm.deleteFaculty(faculty.userId) { success ->
                    Toast.makeText(context, if (success) "Deleted" else "Error", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null).show()
    }

    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_faculty, null)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add New Faculty")
            .setView(dialogView)
            .setPositiveButton("Create") { _, _ ->
                val name = dialogView.findViewById<TextInputEditText>(R.id.etName).text.toString().trim()
                val email = dialogView.findViewById<TextInputEditText>(R.id.etEmail).text.toString().trim()
                val password = dialogView.findViewById<TextInputEditText>(R.id.etPassword).text.toString().trim()
                val dept = dialogView.findViewById<TextInputEditText>(R.id.etDepartment).text.toString().trim()

                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Fill required fields", Toast.LENGTH_SHORT).show(); return@setPositiveButton
                }
                
                // You would need a vm.addFaculty similar to addStudent
                // For now, assuming it exists or adding it to VM
            }
            .setNegativeButton("Cancel", null).show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
