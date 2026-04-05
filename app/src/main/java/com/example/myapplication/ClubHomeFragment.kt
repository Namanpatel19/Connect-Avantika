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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myapplication.adapters.EventAdapter
import com.example.myapplication.databinding.FragmentClubHomeBinding
import com.example.myapplication.ui.viewmodel.AppViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class ClubHomeFragment : Fragment() {
    private var _binding: FragmentClubHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { uploadBanner(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentClubHomeBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        binding.rvUpcomingEvents.layoutManager = LinearLayoutManager(context)
        val adapter = EventAdapter(emptyList()) { _ -> }
        binding.rvUpcomingEvents.adapter = adapter

        vm.myClub.observe(viewLifecycleOwner) { club ->
            club?.let {
                binding.tvClubName.text = it.name
                binding.tvClubDesc.text = it.description
                if (!it.bannerUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(it.bannerUrl)
                        .placeholder(R.drawable.bg_teal_header)
                        .into(binding.ivBanner)
                }
            }
        }

        vm.clubEvents.observe(viewLifecycleOwner) { events ->
            adapter.updateEvents(events)
            binding.tvEventsCount.text = events.size.toString()
        }

        vm.clubRequests.observe(viewLifecycleOwner) { requests ->
            val pending = requests.filter { it.status == "pending" || it.status == "interview" }
            binding.tvPendingCount.text = pending.size.toString()
            
            // Show real member count if available (assuming accepted requests are members)
            val members = requests.filter { it.status == "accepted" }
            binding.tvMembersCount.text = members.size.toString()
        }

        binding.cardCreateEvent.setOnClickListener {
            CreateEventDialog().show(parentFragmentManager, "CreateEvent")
        }

        binding.cardMembers.setOnClickListener {
            (activity as? MainActivity)?.navigateTo(R.id.navigation_member_requests)
        }

        binding.btnEditBanner.setOnClickListener { pickImage.launch("image/*") }

        vm.loadMyClub()
    }

    private fun uploadBanner(uri: Uri) {
        lifecycleScope.launch {
            try {
                val file = uriToFile(uri)
                val clubId = vm.myClub.value?.id
                if (clubId != null) {
                    vm.uploadClubBanner(clubId, file) { success ->
                        Toast.makeText(context, if (success) "Banner updated!" else "Upload failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().cacheDir, "club_banner_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return file
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
