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
import com.example.myapplication.adapters.DeanAdapter
import com.example.myapplication.data.User
import com.example.myapplication.databinding.FragmentManageStudentsDeanBinding
import com.example.myapplication.ui.viewmodel.AppViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class ManageDeansFragment : Fragment() {
    private var _binding: FragmentManageStudentsDeanBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentManageStudentsDeanBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        binding.rvStudents.layoutManager = LinearLayoutManager(context)
        binding.tvHeaderTitle.text = "Dean Management"
        binding.btnAddStudent.text = "+ Add Dean"
        binding.etSearch.hint = "Search deans..."

        vm.deans.observe(viewLifecycleOwner) { deans ->
            binding.tvCount.text = "All Deans (${deans.size})"
            binding.rvStudents.adapter = DeanAdapter(deans, onDelete = { showDeleteDialog(it) })
        }
        
        vm.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        binding.btnAddStudent.setOnClickListener { showAddDeanDialog() }
        vm.loadDeans()
    }

    private fun showDeleteDialog(dean: User) {
        AlertDialog.Builder(requireContext())
            .setTitle("Remove Dean")
            .setMessage("Remove ${dean.email}?")
            .setPositiveButton("Remove") { _, _ ->
                vm.deleteDean(dean.id) { success ->
                    Toast.makeText(context, if (success) "Removed" else "Error", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null).show()
    }

    private fun showAddDeanDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_dean, null)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Create a new Dean")
            .setView(dialogView)
            .setPositiveButton("Create") { _, _ ->
                val email = dialogView.findViewById<TextInputEditText>(R.id.etEmail).text.toString().trim()
                val password = dialogView.findViewById<TextInputEditText>(R.id.etPassword).text.toString().trim()

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Email and Password are required", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val user = User(id = "", email = email, password = password, role = "dean")
                vm.addDean(user) { success ->
                    Toast.makeText(context, if (success) "Dean created!" else "Error", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
