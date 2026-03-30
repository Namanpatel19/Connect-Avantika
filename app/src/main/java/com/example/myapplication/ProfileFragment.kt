package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.myapplication.data.Student
import com.example.myapplication.databinding.FragmentProfileBinding
import com.example.myapplication.ui.viewmodel.AppViewModel
import io.github.jan.supabase.auth.auth
import java.io.File
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel
    private val PICK_IMAGE = 101

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        vm.currentStudent.observe(viewLifecycleOwner) { student ->
            student?.let { bind(it) }
        }

        vm.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.btnSave.isEnabled = !loading
        }

        binding.ivAvatar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE)
        }

        binding.btnSave.setOnClickListener { saveProfile() }

        binding.btnLogout.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                try { SupabaseClient.client.auth.signOut() } catch (_: Exception) {}
                startActivity(android.content.Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }
        }

        vm.loadCurrentStudent()
    }

    private fun bind(s: Student) {
        binding.etName.setText(s.name)
        binding.etDepartment.setText(s.department ?: "")
        binding.etContact.setText(s.contact ?: "")
        binding.etBatch.setText(s.batch ?: "")
        binding.tvEnrollment.text = s.enrollment
        if (!s.photoUrl.isNullOrEmpty()) Glide.with(this).load(s.photoUrl).circleCrop().into(binding.ivAvatar)
    }

    private fun saveProfile() {
        val current = vm.currentStudent.value ?: return
        val updated = current.copy(
            name       = binding.etName.text.toString().trim(),
            department = binding.etDepartment.text.toString().trim(),
            contact    = binding.etContact.text.toString().trim(),
            batch      = binding.etBatch.text.toString().trim()
        )
        vm.updateStudentProfile(updated) { success ->
            Toast.makeText(context, if (success) "Profile updated!" else "Update failed", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val uri: Uri = data?.data ?: return
            val stream = requireContext().contentResolver.openInputStream(uri) ?: return
            val file = File(requireContext().cacheDir, "profile_${System.currentTimeMillis()}.jpg")
            file.outputStream().use { stream.copyTo(it) }
            vm.uploadPhoto(file, "student") { success ->
                if (success) {
                    Toast.makeText(context, "Photo updated!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
