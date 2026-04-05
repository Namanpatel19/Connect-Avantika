package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.FragmentFacultyProfileBinding
import com.example.myapplication.ui.viewmodel.AppViewModel
import com.onesignal.OneSignal
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class FacultyProfileFragment : Fragment() {
    private var _binding: FragmentFacultyProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentFacultyProfileBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        vm.currentFaculty.observe(viewLifecycleOwner) { f ->
            f?.let {
                binding.etName.setText(it.name)
                binding.etDepartment.setText(it.department ?: "")
                binding.etContact.setText(it.contact ?: "")
                if (!it.photoUrl.isNullOrEmpty()) Glide.with(this).load(it.photoUrl).circleCrop().into(binding.ivAvatar)
            }
        }

        binding.btnSave.setOnClickListener {
            val f = vm.currentFaculty.value ?: return@setOnClickListener
            val updated = f.copy(
                name       = binding.etName.text.toString().trim(),
                department = binding.etDepartment.text.toString().trim(),
                contact    = binding.etContact.text.toString().trim()
            )
            vm.updateFacultyProfile(updated) { success ->
                Toast.makeText(context, if (success) "Profile updated!" else "Error", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogout.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    SupabaseClient.client.auth.signOut()
                    OneSignal.logout()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                } catch (e: Exception) {
                    OneSignal.logout()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        }
        vm.loadCurrentFaculty()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
