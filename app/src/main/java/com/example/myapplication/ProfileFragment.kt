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
            // Update UI loading state if needed
        }

        binding.ivAvatar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE)
        }

        binding.btnLogout.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                try { SupabaseClient.client.auth.signOut() } catch (_: Exception) {}
                startActivity(android.content.Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }
        }
        
        binding.btnLeaderboard.setOnClickListener {
            (activity as? MainActivity)?.navigateTo(R.id.navigation_rank)
        }

        vm.loadCurrentStudent()
    }

    private fun bind(s: Student) {
        binding.tvName.text = s.name
        binding.tvRole.text = "${s.department ?: "Student"} | ${s.batch ?: ""}"
        binding.tvEmail.text = "Connect Profile" // You can add email to Student model if needed
        if (!s.photoUrl.isNullOrEmpty()) Glide.with(this).load(s.photoUrl).circleCrop().into(binding.ivAvatar)
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
                if (success) Toast.makeText(context, "Photo updated!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
