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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class ClubDetailsFragment : Fragment() {
    private var _binding: FragmentClubDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel
    private var clubId: String? = null
    private var currentClub: Club? = null

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
        
        loadClubData()

        binding.btnJoinClub.setOnClickListener {
            clubId?.let { id ->
                vm.joinClub(id) { success ->
                    Toast.makeText(context, if (success) "Request sent!" else "Error joining", Toast.LENGTH_SHORT).show()
                }
            }
        }

        setupAdminUI()
    }

    private fun loadClubData() {
        vm.clubs.observe(viewLifecycleOwner) { clubs ->
            currentClub = clubs.find { it.id == clubId }
            currentClub?.let { club ->
                binding.tvClubName.text = club.name
                binding.etDescription.setText(club.description ?: "No description provided.")
                // Real member count would come from a specific API call
                binding.tvMemberCount.text = "Loading..." 
                
                // If user is club lead or super admin, allow editing
                val isLead = vm.userId == club.clubHeadId
                val isAdmin = vm.userRole == "super_admin"
                
                if (isLead || isAdmin) {
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
