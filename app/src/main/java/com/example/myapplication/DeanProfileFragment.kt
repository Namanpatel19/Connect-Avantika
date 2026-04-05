package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.FragmentDeanProfileBinding
import com.example.myapplication.ui.viewmodel.AppViewModel
import com.onesignal.OneSignal
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class DeanProfileFragment : Fragment() {
    private var _binding: FragmentDeanProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentDeanProfileBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        vm.currentFaculty.observe(viewLifecycleOwner) { f ->
            f?.let {
                binding.tvName.text    = it.name
                binding.tvDept.text   = it.department ?: "Administration"
                binding.tvContact.text = it.contact ?: ""
            }
        }
        binding.tvRole.text = "Dean"

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
