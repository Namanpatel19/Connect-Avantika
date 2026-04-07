package com.example.myapplication

import android.content.Intent
import android.net.Uri
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
                binding.tvName.text = it.name
                binding.tvDept.text = it.department ?: ""
            }
        }

        binding.rvEvents.layoutManager = LinearLayoutManager(context)
        vm.events.observe(viewLifecycleOwner) { events ->
            binding.rvEvents.adapter = EventAdapter(events.take(5)) { /* read-only for faculty */ }
        }

        // Quick action: Manage Students
        binding.cardManageStudents.setOnClickListener {
            (requireActivity() as MainActivity).navigateTo(R.id.navigation_manage_students)
        }

        // Quick action: Upload Materials
        binding.cardUploadMaterial.setOnClickListener {
            (requireActivity() as MainActivity).navigateTo(R.id.navigation_upload_material)
        }
        
        // Quick action: Open Moodle
        binding.cardMoodle.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://moodle.avantika.edu.in/login/index.php"))
            startActivity(intent)
        }

        vm.loadCurrentFaculty()
        vm.loadApprovedEvents()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
