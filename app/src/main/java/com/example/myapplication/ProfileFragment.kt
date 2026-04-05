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
import com.example.myapplication.databinding.FragmentProfileBinding
import com.example.myapplication.ui.viewmodel.AppViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.onesignal.OneSignal
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AppViewModel

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

        viewModel.currentStudent.observe(viewLifecycleOwner) { student ->
            student?.let {
                binding.tvName.text = it.name
                binding.tvRole.text = "${it.department ?: ""} | ${it.batch ?: ""}"
                binding.tvEmail.text = it.enrollment
                binding.tvPhone.text = it.contact ?: "No contact"
            }
        }

        binding.btnChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        binding.btnLogout.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    SupabaseClient.client.auth.signOut()
                    // OneSignal Logout
                    OneSignal.logout()
                    
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                } catch (e: Exception) {
                    // Fallback to login anyway
                    OneSignal.logout()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        }

        if (viewModel.currentStudent.value == null) {
            viewModel.loadCurrentStudent()
        }
    }

    private fun showChangePasswordDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_change_password, null)
        val etNewPassword = dialogView.findViewById<TextInputEditText>(R.id.etNewPassword)
        val etConfirmPassword = dialogView.findViewById<TextInputEditText>(R.id.etConfirmPassword)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Change Password")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val newPass = etNewPassword.text.toString()
                val confirmPass = etConfirmPassword.text.toString()

                if (newPass.length < 6) {
                    Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (newPass != confirmPass) {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                updatePassword(newPass)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updatePassword(newPass: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                SupabaseClient.client.auth.updateUser {
                    password = newPass
                }
                Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
