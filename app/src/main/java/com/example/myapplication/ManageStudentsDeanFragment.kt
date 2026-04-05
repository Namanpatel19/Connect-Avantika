package com.example.myapplication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.StudentAdapter
import com.example.myapplication.data.Student
import com.example.myapplication.data.User
import com.example.myapplication.databinding.FragmentManageStudentsDeanBinding
import com.example.myapplication.ui.viewmodel.AppViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class ManageStudentsDeanFragment : Fragment() {
    private var _binding: FragmentManageStudentsDeanBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentManageStudentsDeanBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        binding.rvStudents.layoutManager = LinearLayoutManager(context)

        vm.students.observe(viewLifecycleOwner) { students ->
            binding.tvCount.text = "All Students (${students.size})"
            binding.rvStudents.adapter = StudentAdapter(students, onDelete = { showDeleteDialog(it) })
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

        binding.btnAddStudent.setOnClickListener { showAddDialog() }
        vm.loadAllStudents()
    }

    private fun showDeleteDialog(student: Student) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Student")
            .setMessage("Remove ${student.name}?")
            .setPositiveButton("Delete") { _, _ ->
                vm.deleteStudent(student.userId) { success ->
                    Toast.makeText(context, if (success) "Deleted" else "Error", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null).show()
    }

    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_student, null)
        
        // Department Dropdown
        val actvDept = dialogView.findViewById<AutoCompleteTextView>(R.id.actvDepartment)
        val departments = arrayOf("B-Tech", "BBA", "BBA-LLB", "BALLB", "BCA", "BDes", "Mdes", "MCA", "Bcom", "BSC agriculture")
        val deptAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, departments)
        actvDept.setAdapter(deptAdapter)

        // Batch Dropdown
        val actvBatch = dialogView.findViewById<AutoCompleteTextView>(R.id.actvBatch)
        val batches = arrayOf("2022", "2023", "2024", "2025", "2026", "2027", "2029", "2030")
        val batchAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, batches)
        actvBatch.setAdapter(batchAdapter)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Create a new student")
            .setView(dialogView)
            .setPositiveButton("Create student") { _, _ ->
                val name        = dialogView.findViewById<TextInputEditText>(R.id.etName).text.toString().trim()
                val email       = dialogView.findViewById<TextInputEditText>(R.id.etEmail).text.toString().trim()
                val password    = dialogView.findViewById<TextInputEditText>(R.id.etPassword).text.toString().trim()
                val enrollment  = dialogView.findViewById<TextInputEditText>(R.id.etEnrollment).text.toString().trim().uppercase()
                val dept        = actvDept.text.toString().trim()
                val year        = dialogView.findViewById<TextInputEditText>(R.id.etYear).text.toString().trim()
                val batch       = actvBatch.text.toString().trim()

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || enrollment.isEmpty() || dept.isEmpty() || year.isEmpty() || batch.isEmpty()) {
                    Toast.makeText(context, "Fill required fields (marked *)", Toast.LENGTH_SHORT).show(); return@setPositiveButton
                }
                
                val combinedDept = "$dept (Year $year)"
                val user    = User(id = "", email = email, password = password, role = "student")
                val student = Student(userId = "", name = name, enrollment = enrollment, department = combinedDept, batch = batch)
                
                // Auto Confirm is now true by default
                vm.addStudent(user, student, autoConfirm = true) { success ->
                    if (success) {
                        Toast.makeText(context, "Student user created!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error creating student. Check logs.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null).show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
