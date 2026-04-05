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
import com.bumptech.glide.Glide
import com.example.myapplication.data.Club
import com.example.myapplication.data.ClubRequest
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

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        loadClubData()
        setupJoinLogic()
        setupAdminUI()
    }

    private fun setupJoinLogic() {
        val isStudent = vm.userRole == "student"
        if (!isStudent) {
            binding.btnJoinClub.visibility = View.GONE
            binding.cvStatus.visibility = View.GONE
            return
        }

        vm.myClubRequests.observe(viewLifecycleOwner) { requests ->
            val myRequest = requests.find { it.clubId == clubId }
            updateJoinButtonUI(myRequest)
        }

        binding.btnJoinClub.setOnClickListener {
            clubId?.let { id ->
                binding.btnJoinClub.isEnabled = false
                vm.joinClub(id, currentClub?.name ?: "", currentClub?.clubHeadId) { success ->
                    if (success) {
                        Toast.makeText(context, "Join request sent to Club Lead!", Toast.LENGTH_SHORT).show()
                    } else {
                        binding.btnJoinClub.isEnabled = true
                        Toast.makeText(context, "Failed to send request.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun updateJoinButtonUI(request: ClubRequest?) {
        if (request == null) {
            binding.btnJoinClub.visibility = View.VISIBLE
            binding.btnJoinClub.text = "Join Club"
            binding.btnJoinClub.isEnabled = true
            binding.cvStatus.visibility = View.GONE
            binding.cvInterview.visibility = View.GONE
        } else {
            binding.cvStatus.visibility = View.VISIBLE
            when (request.status) {
                "pending" -> {
                    binding.btnJoinClub.visibility = View.GONE
                    binding.tvJoinStatusTitle.text = "Request Pending"
                    binding.tvJoinStatus.text = "Your request is being reviewed by the club lead."
                    binding.cvInterview.visibility = View.GONE
                }
                "interview" -> {
                    binding.btnJoinClub.visibility = View.GONE
                    binding.tvJoinStatusTitle.text = "Interview Stage"
                    binding.tvJoinStatus.text = "Congratulations! You've been selected for an interview."
                    
                    binding.cvInterview.visibility = View.VISIBLE
                    binding.tvInterviewDetails.text = "Date: ${request.interviewDate ?: "TBD"}\nTime: ${request.interviewTime ?: "TBD"}\nVenue: ${request.interviewVenue ?: "TBD"}"
                }
                "accepted" -> {
                    binding.btnJoinClub.visibility = View.GONE
                    binding.tvJoinStatusTitle.text = "Member"
                    binding.tvJoinStatus.text = "Welcome! You are an official member of this club."
                    binding.cvInterview.visibility = View.GONE
                }
                "rejected" -> {
                    binding.btnJoinClub.visibility = View.VISIBLE
                    binding.btnJoinClub.text = "Re-apply"
                    binding.btnJoinClub.isEnabled = true
                    binding.tvJoinStatusTitle.text = "Request Declined"
                    binding.tvJoinStatus.text = "Your previous request was not accepted. You can try applying again later."
                    binding.cvInterview.visibility = View.GONE
                }
                else -> {
                    binding.btnJoinClub.visibility = View.VISIBLE
                    binding.cvStatus.visibility = View.GONE
                    binding.cvInterview.visibility = View.GONE
                }
            }
        }
    }

    private fun loadClubData() {
        vm.clubs.observe(viewLifecycleOwner) { clubs ->
            currentClub = clubs.find { it.id == clubId }
            currentClub?.let { club ->
                binding.tvClubName.text = club.name
                binding.tvDescription.text = club.description ?: "No description provided."
                
                if (!club.bannerUrl.isNullOrEmpty()) {
                    Glide.with(this).load(club.bannerUrl).placeholder(R.drawable.bg_teal_header).into(binding.ivBanner)
                }

                // Club head / super admin: allow editing
                val isLead = vm.userId == club.clubHeadId
                val isAdmin = vm.userRole == "super_admin"

                if (isLead || isAdmin) {
                    binding.btnJoinClub.visibility = View.GONE
                    binding.cvStatus.visibility = View.GONE
                    binding.tvDescription.visibility = View.GONE
                    binding.etDescription.visibility = View.VISIBLE
                    binding.etDescription.setText(club.description)
                    binding.btnSaveDescription.visibility = View.VISIBLE
                    binding.btnChangeBanner.visibility = View.VISIBLE

                    binding.btnSaveDescription.setOnClickListener {
                        val newDesc = binding.etDescription.text.toString()
                        vm.updateClub(club.copy(description = newDesc)) { success ->
                            if (success) {
                                binding.tvDescription.text = newDesc
                                Toast.makeText(context, "Description updated", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
                            }
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
                val leadEmail = binding.etNewLeadId.text.toString().trim()
                if (leadEmail.isNotEmpty() && clubId != null) {
                    vm.assignClubLead(clubId!!, leadEmail) { success ->
                        Toast.makeText(context, if (success) "Lead assigned!" else "Error: User not found", Toast.LENGTH_SHORT).show()
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
