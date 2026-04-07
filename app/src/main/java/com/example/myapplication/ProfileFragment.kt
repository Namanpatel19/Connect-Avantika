package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.FragmentProfileBinding
import com.example.myapplication.ui.viewmodel.AppViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.onesignal.OneSignal
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AppViewModel

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { uploadProfilePhoto(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        // Observe student profile data
        viewModel.currentStudent.observe(viewLifecycleOwner) { student ->
            student?.let {
                binding.tvName.text = it.name
                binding.tvRole.text = "${it.department ?: "Student"} | ${it.batch ?: ""}"
                binding.tvEnrollment.text = "Enrollment: ${it.enrollment}"
                binding.tvPhone.text = it.contact ?: "No contact added"
                binding.tvDepartment.text = it.department ?: "Avantika University"

                // Load profile photo
                if (!it.photoUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(it.photoUrl)
                        .placeholder(R.drawable.ic_person)
                        .circleCrop()
                        .into(binding.ivAvatar)
                }
            }
        }

        // Observe profile stats
        viewModel.studentClubsCount.observe(viewLifecycleOwner) { count ->
            binding.tvClubsJoined.text = count.toString()
        }

        viewModel.studentEventsCount.observe(viewLifecycleOwner) { count ->
            binding.tvEventsEnrolled.text = count.toString()
        }

        viewModel.myClubRequests.observe(viewLifecycleOwner) { requests ->
            val pending = requests.filter { it.status == "pending" || it.status == "interview" }
            binding.tvPendingRequests.text = pending.size.toString()
        }

        viewModel.studyMaterials.observe(viewLifecycleOwner) { materials ->
            binding.tvMaterials.text = materials.size.toString()
        }

        viewModel.userEmail.observe(viewLifecycleOwner) { email ->
            binding.tvEmail.text = email ?: "No email"
        }

        // Profile photo change
        binding.btnChangePhoto.setOnClickListener {
            pickImage.launch("image/*")
        }

        // Change password - send reset link
        binding.btnChangePassword.setOnClickListener {
            val email = viewModel.userEmail.value
            if (email != null) {
                sendPasswordResetLink(email)
            } else {
                Toast.makeText(context, "Email not found", Toast.LENGTH_SHORT).show()
            }
        }

        // Logout
        binding.btnLogout.setOnClickListener {
            (activity as? MainActivity)?.logout()
        }

        // Load all data
        if (viewModel.currentStudent.value == null) {
            viewModel.loadCurrentStudent()
        }
        viewModel.loadProfileStats()
        viewModel.loadMyClubRequests()
        viewModel.loadStudyMaterials()
    }

    private fun sendPasswordResetLink(emailStr: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                SupabaseClient.client.auth.resetPasswordForEmail(emailStr)
                Toast.makeText(requireContext(), "Password reset link sent to $emailStr", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun uploadProfilePhoto(uri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val file = File(requireContext().cacheDir, "profile_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            viewModel.uploadProfileImage(file) { success, url ->
                if (success) {
                    Toast.makeText(context, "Profile photo updated!", Toast.LENGTH_SHORT).show()
                    url?.let {
                        Glide.with(this)
                            .load(it)
                            .placeholder(R.drawable.ic_person)
                            .circleCrop()
                            .into(binding.ivAvatar)
                    }
                } else {
                    Toast.makeText(context, "Failed to upload photo", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
