package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.FragmentSuperAdminHomeBinding
import com.example.myapplication.ui.viewmodel.AppViewModel
import com.onesignal.OneSignal

class SuperAdminHomeFragment : Fragment() {
    private var _binding: FragmentSuperAdminHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentSuperAdminHomeBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        vm.students.observe(viewLifecycleOwner) { binding.tvStudentCount.text = it.size.toString() }
        vm.faculty.observe(viewLifecycleOwner) { binding.tvFacultyCount.text = it.size.toString() }
        vm.clubs.observe(viewLifecycleOwner) { binding.tvClubCount.text = it.size.toString() }
        vm.events.observe(viewLifecycleOwner) { binding.tvEventCount.text = it.size.toString() }
        vm.deans.observe(viewLifecycleOwner) { binding.tvDeanCount.text = it.size.toString() }

        binding.btnManageAll.setOnClickListener {
            (activity as? MainActivity)?.navigateTo(R.id.navigation_manage_all)
        }

        binding.btnLogout.setOnClickListener {
            OneSignal.logout()
            (activity as? MainActivity)?.logout()
        }

        vm.loadAllStudents()
        vm.loadAllFaculty()
        vm.loadAllClubs()
        vm.loadAllEvents()
        vm.loadDeans()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
