package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.EventAdapter
import com.example.myapplication.databinding.FragmentFacultyHomeBinding
import com.example.myapplication.ui.viewmodel.AppViewModel

class FacultyHomeFragment : Fragment() {
    private var _binding: FragmentFacultyHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentFacultyHomeBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        vm.currentFaculty.observe(viewLifecycleOwner) { f ->
            f?.let {
                binding.tvWelcome.text = "Welcome back,"
                binding.tvName.text = it.name
                binding.tvDept.text = it.department ?: ""
            }
        }

        binding.rvEvents.layoutManager = LinearLayoutManager(context)
        vm.events.observe(viewLifecycleOwner) { events ->
            binding.rvEvents.adapter = EventAdapter(events.take(5)) {}
        }

        binding.cardManageStudents.setOnClickListener {
            (requireActivity() as MainActivity).navigateTo(R.id.navigation_manage_students)
        }

        vm.loadCurrentFaculty()
        vm.loadApprovedEvents()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
