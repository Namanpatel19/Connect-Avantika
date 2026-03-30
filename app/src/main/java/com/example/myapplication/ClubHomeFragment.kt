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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myapplication.adapters.EventAdapter
import com.example.myapplication.databinding.FragmentClubHomeBinding
import com.example.myapplication.ui.viewmodel.AppViewModel
import java.io.File

class ClubHomeFragment : Fragment() {
    private var _binding: FragmentClubHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: AppViewModel
    private val PICK_BANNER = 102

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentClubHomeBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[AppViewModel::class.java]

        vm.myClub.observe(viewLifecycleOwner) { club ->
            club?.let {
                binding.tvClubName.text = it.name
                binding.tvClubDesc.text = it.description ?: ""
                if (!it.bannerUrl.isNullOrEmpty()) {
                    Glide.with(this).load(it.bannerUrl).centerCrop().into(binding.ivBanner)
                }
            }
        }
        vm.clubEvents.observe(viewLifecycleOwner) { events ->
            binding.tvEventsCount.text = events.size.toString()
            binding.rvUpcomingEvents.adapter = EventAdapter(events.take(3)) {}
        }
        vm.clubRequests.observe(viewLifecycleOwner) { reqs ->
            binding.tvMembersCount.text = reqs.count { it.status == "accepted" }.toString()
            binding.tvPendingCount.text = reqs.count { it.status == "pending" }.toString()
        }

        binding.rvUpcomingEvents.layoutManager = LinearLayoutManager(context)

        binding.cardCreateEvent.setOnClickListener {
            CreateEventDialog().show(parentFragmentManager, "CreateEvent")
        }
        binding.cardMembers.setOnClickListener {
            (requireActivity() as MainActivity).navigateTo(R.id.navigation_member_requests)
        }

        binding.btnEditBanner.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_BANNER)
        }

        vm.loadMyClub()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_BANNER && resultCode == Activity.RESULT_OK) {
            val uri: Uri = data?.data ?: return
            val stream = requireContext().contentResolver.openInputStream(uri) ?: return
            val file = File(requireContext().cacheDir, "banner_${System.currentTimeMillis()}.jpg")
            file.outputStream().use { stream.copyTo(it) }
            
            val clubId = vm.myClub.value?.id ?: return
            vm.uploadClubBanner(clubId, file) { success ->
                if (success) Toast.makeText(context, "Banner updated!", Toast.LENGTH_SHORT).show()
                else Toast.makeText(context, "Banner upload failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
