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
import com.example.myapplication.databinding.FragmentClubProfileBinding
import com.example.myapplication.ui.viewmodel.AppViewModel
import com.onesignal.OneSignal
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class ClubProfileFragment : Fragment() {
    private var _binding: FragmentClubProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentClubProfileBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        vm.myClub.observe(viewLifecycleOwner) { club ->
            club?.let {
                binding.tvClubName.text = it.name
                binding.etClubName.setText(it.name)
                binding.etDescription.setText(it.description ?: "")
            }
        }
        vm.clubEvents.observe(viewLifecycleOwner) { binding.tvEvents.text = it.size.toString() }
        vm.clubRequests.observe(viewLifecycleOwner) { reqs ->
            binding.tvMembers.text = reqs.count { it.status == "accepted" }.toString()
        }

        binding.btnSave.setOnClickListener {
            val club = vm.myClub.value?.copy(
                name        = binding.etClubName.text.toString().trim(),
                description = binding.etDescription.text.toString().trim()
            ) ?: return@setOnClickListener
            vm.updateClub(club) { success ->
                Toast.makeText(context, if (success) "Club profile updated!" else "Error", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                try {
                    SupabaseClient.client.auth.signOut()
                    OneSignal.logout()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                } catch (e: Exception) {
                    OneSignal.logout()
                    Toast.makeText(context, "Logout failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (vm.myClub.value == null) vm.loadMyClub()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
