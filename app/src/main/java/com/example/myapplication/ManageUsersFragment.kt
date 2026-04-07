package com.example.myapplication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.UserAdapter
import com.example.myapplication.databinding.FragmentManageStudentsDeanBinding
import com.example.myapplication.ui.viewmodel.AppViewModel

class ManageUsersFragment : Fragment() {
    private var _binding: FragmentManageStudentsDeanBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentManageStudentsDeanBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        binding.rvStudents.layoutManager = LinearLayoutManager(context)
        binding.tvHeaderTitle.text = "Auth User Audit"
        binding.btnAddStudent.visibility = View.GONE
        binding.etSearch.hint = "Search by email..."

        vm.allUsers.observe(viewLifecycleOwner) { users ->
            binding.tvCount.text = "Total System Users: ${users.size}"
            binding.rvStudents.adapter = UserAdapter(users)
        }

        vm.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Local filter for audit panel
                val query = s.toString().lowercase()
                val filtered = vm.allUsers.value?.filter { it.email.lowercase().contains(query) } ?: emptyList()
                binding.rvStudents.adapter = UserAdapter(filtered)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        vm.loadAllUsers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
