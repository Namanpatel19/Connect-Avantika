package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapters.StudyMaterialAdapter
import com.example.myapplication.databinding.FragmentFacultyUploadBinding
import com.example.myapplication.ui.viewmodel.AppViewModel
import java.io.File
import java.io.FileOutputStream

class FacultyMaterialsFragment : Fragment() {

    private var _binding: FragmentFacultyUploadBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel
    private var selectedFile: File? = null
    private lateinit var materialsAdapter: StudyMaterialAdapter

    private val pickFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                selectedFile = uriToFile(it)
                val name = selectedFile?.name ?: "file"
                binding.chipFileName.text = name
                binding.chipFileName.visibility = View.VISIBLE
                binding.tvPickFile.text = "File selected ✓"
            } catch (e: Exception) {
                Toast.makeText(context, "Error selecting file: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = FragmentFacultyUploadBinding.inflate(inflater, container, false)
        .also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        setupDropdowns()
        setupRecycler()

        binding.cardPickFile.setOnClickListener {
            pickFile.launch("*/*")
        }

        binding.btnUpload.setOnClickListener { handleUpload() }

        vm.uploadProgress.observe(viewLifecycleOwner) { uploading ->
            binding.progressUpload.visibility = if (uploading) View.VISIBLE else View.GONE
            binding.btnUpload.isEnabled = !uploading
            binding.btnUpload.text = if (uploading) "Uploading..." else "Upload Material"
        }

        vm.studyMaterials.observe(viewLifecycleOwner) { list ->
            materialsAdapter.update(list)
        }

        vm.loadStudyMaterials()
    }

    private fun setupDropdowns() {
        val batches = (2021..2030).map { "$it" }
        val batchAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, batches)
        binding.actvBatch.setAdapter(batchAdapter)

        val departments = listOf("BTech", "BCA", "MCA", "MBA", "BSc", "BSc Agriculture", "BDes", "MDes", "LLB", "BA+LLB", "BBA+LLB")
        val deptAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, departments)
        binding.actvDepartment.setAdapter(deptAdapter)
    }

    private fun setupRecycler() {
        materialsAdapter = StudyMaterialAdapter(emptyList())
        binding.rvMaterials.layoutManager = LinearLayoutManager(context)
        binding.rvMaterials.adapter = materialsAdapter
    }

    private fun handleUpload() {
        val title = binding.etTitle.text?.toString()?.trim() ?: ""
        val subject = binding.etSubject.text?.toString()?.trim() ?: ""
        val batch = binding.actvBatch.text?.toString()?.trim() ?: ""
        val dept = binding.actvDepartment.text?.toString()?.trim() ?: ""
        val file = selectedFile

        if (title.isEmpty()) { binding.tilTitle.error = "Title is required"; return }
        if (subject.isEmpty()) { binding.tilSubject.error = "Subject is required"; return }
        if (file == null) { Toast.makeText(context, "Please pick a file first", Toast.LENGTH_SHORT).show(); return }

        binding.tilTitle.error = null
        binding.tilSubject.error = null

        vm.uploadStudyMaterial(title, subject, batch, dept, file) { success, message ->
            context?.let {
                Toast.makeText(it, message, Toast.LENGTH_LONG).show()
                if (success) {
                    binding.etTitle.text?.clear()
                    binding.etSubject.text?.clear()
                    binding.actvBatch.text.clear()
                    binding.actvDepartment.text.clear()
                    binding.chipFileName.visibility = View.GONE
                    binding.tvPickFile.text = "Tap to choose file (PDF, DOC, PPT...)"
                    selectedFile = null
                }
            }
        }
    }

    private fun uriToFile(uri: Uri): File? {
        val context = context ?: return null
        val contentResolver = context.contentResolver
        val fileName = getFileNameFromUri(uri) ?: "upload_${System.currentTimeMillis()}"
        val file = File(context.cacheDir, fileName)
        contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        var name: String? = null
        context?.contentResolver?.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val idx = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (idx >= 0) name = cursor.getString(idx)
            }
        }
        return name
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
