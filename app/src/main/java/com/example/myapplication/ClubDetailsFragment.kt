package com.example.myapplication

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
import com.example.myapplication.data.Club
import com.example.myapplication.databinding.FragmentClubDetailsBinding
import com.example.myapplication.ui.viewmodel.AppViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class ClubDetailsFragment : Fragment() {
    private var _binding: FragmentClubDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel
    private var clubId: String? = null
    private var currentClub: Club? = null
    private var hasRequestedJoin = false

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { uploadBanner(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentClubDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        clubId = arguments?.getString("club_id")

        // Only students can join clubs
        val isStudent = vm.userRole == "student"
        if (!isStudent) {
            binding.btnJoinClub.visibility = View.GONE
            binding.tvJoinStatus.visibility = View.GONE
        }

        loadClubData()
        setupAdminUI()

        binding.btnJoinClub.setOnClickListener {
            if (hasRequestedJoin) return@setOnClickListener
            clubId?.let { id ->
                binding.btnJoinClub.isEnabled = false
                vm.joinClub(id) { success ->
                    activity?.runOnUiThread {
                        if (success) {
                            hasRequestedJoin = true
                            binding.btnJoinClub.text = "Request Sent ✓"
                            binding.btnJoinClub.isEnabled = false
                            binding.tvJoinStatus.text = "Your request is pending review"
                            binding.tvJoinStatus.visibility = View.VISIBLE
                            Toast.makeText(context, "Join request sent!", Toast.LENGTH_SHORT).show()
                        } else {
                            binding.btnJoinClub.isEnabled = true
                            Toast.makeText(context, "Failed to send request. You may have already applied.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun loadClubData() {
        vm.clubs.observe(viewLifecycleOwner) { clubs ->
            currentClub = clubs.find { it.id == clubId }
            currentClub?.let { club ->
                binding.tvClubName.text = club.name
                binding.etDescription.setText(club.description ?: "No description provided.")
                binding.tvMemberCount.text = "—"

                // Club head / super admin: allow editing
                val isLead = vm.userId == club.clubHeadId
                val isAdmin = vm.userRole == "super_admin"

                if (isLead || isAdmin) {
                    binding.btnJoinClub.visibility = View.GONE
                    binding.tvJoinStatus.visibility = View.GONE
                    binding.etDescription.isEnabled = true
                    binding.btnSaveDescription.visibility = View.VISIBLE
                    binding.btnChangeBanner.visibility = View.VISIBLE

                    binding.btnSaveDescription.setOnClickListener {
                        val newDesc = binding.etDescription.text.toString()
                        vm.updateClub(club.copy(description = newDesc)) { success ->
                            Toast.makeText(context, if (success) "Description updated" else "Update failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    binding.btnChangeBanner.setOnClickListener { pickImage.launch("image/*") }
                }
            }
        }

        if (vm.clubs.value.isNullOrEmpty()) vm.loadAllClubs()
    }

    private fun setupAdminUI() {
        if (vm.userRole == "super_admin") {
            binding.adminCard.visibility = View.VISIBLE
            binding.btnUpdateLead.setOnClickListener {
                val newLeadId = binding.etNewLeadId.text.toString().trim()
                if (newLeadId.isNotEmpty() && currentClub != null) {
                    vm.updateClub(currentClub!!.copy(clubHeadId = newLeadId)) { success ->
                        Toast.makeText(context, if (success) "Lead updated!" else "Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun uploadBanner(uri: Uri) {
        lifecycleScope.launch {
            try {
                val file = uriToFile(uri)
                clubId?.let { id ->
                    vm.uploadClubBanner(id, file) { success ->
                        Toast.makeText(context, if (success) "Banner updated!" else "Upload failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "File error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().cacheDir, "temp_banner_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return file
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
