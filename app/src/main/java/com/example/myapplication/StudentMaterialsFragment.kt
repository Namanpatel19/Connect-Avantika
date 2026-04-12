package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.StudyMaterialAdapter
import com.example.myapplication.databinding.FragmentStudentMaterialsBinding
import com.example.myapplication.ui.viewmodel.AppViewModel

class StudentMaterialsFragment : Fragment() {

    private var _binding: FragmentStudentMaterialsBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel
    private lateinit var adapter: StudyMaterialAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentMaterialsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        setupRecycler()

        vm.studyMaterials.observe(viewLifecycleOwner) { list ->
            binding.progressBar.visibility = View.GONE
            if (list == null) return@observe
            
            if (list.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                adapter.update(emptyList(), vm.faculty.value ?: emptyList())
            } else {
                binding.tvEmpty.visibility = View.GONE
                adapter.update(list, vm.faculty.value ?: emptyList())
            }
        }
        
        vm.faculty.observe(viewLifecycleOwner) { facultyList ->
            adapter.update(vm.studyMaterials.value ?: emptyList(), facultyList ?: emptyList())
        }

        vm.currentStudent.observe(viewLifecycleOwner) { student ->
            student?.let {
                binding.tvFilterInfo.text = "Materials for ${it.department} | ${it.batch}"
            }
        }

        binding.progressBar.visibility = View.VISIBLE
        vm.loadStudyMaterials()
        vm.loadAllFaculty()
    }

    private fun setupRecycler() {
        adapter = StudyMaterialAdapter(
            materials = emptyList(),
            canManage = false,
            onViewClick = { material ->
                material.fileUrl?.let { url ->
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Cannot open file: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } ?: Toast.makeText(context, "File URL not available", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvMaterials.layoutManager = LinearLayoutManager(context)
        binding.rvMaterials.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
