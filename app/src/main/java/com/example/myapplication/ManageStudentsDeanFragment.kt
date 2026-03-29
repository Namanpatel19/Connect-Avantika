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
import com.example.myapplication.databinding.FragmentManageStudentsDeanBinding
import com.example.myapplication.ui.viewmodel.AppViewModel
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.util.UUID

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
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Create a new user")
            .setView(dialogView)
            .setPositiveButton("Create user") { _, _ ->
                val name        = dialogView.findViewById<TextInputEditText>(R.id.etName).text.toString().trim()
                val email       = dialogView.findViewById<TextInputEditText>(R.id.etEmail).text.toString().trim()
                val password    = dialogView.findViewById<TextInputEditText>(R.id.etPassword).text.toString().trim()
                val autoConfirm = dialogView.findViewById<MaterialCheckBox>(R.id.cbAutoConfirm).isChecked
                val enrollment  = dialogView.findViewById<TextInputEditText>(R.id.etEnrollment).text.toString().trim()
                val dept        = dialogView.findViewById<TextInputEditText>(R.id.etDepartment).text.toString().trim()
                val batch       = dialogView.findViewById<TextInputEditText>(R.id.etBatch).text.toString().trim()

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || enrollment.isEmpty()) {
                    Toast.makeText(context, "Fill required fields", Toast.LENGTH_SHORT).show(); return@setPositiveButton
                }
                
                val user    = User(id = "", email = email, password = password, role = "student")
                val student = Student(userId = "", name = name, enrollment = enrollment, department = dept, batch = batch)
                
                vm.addStudent(user, student, autoConfirm) { success ->
                    Toast.makeText(context, if (success) "User created!" else "Error creating user", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null).show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
